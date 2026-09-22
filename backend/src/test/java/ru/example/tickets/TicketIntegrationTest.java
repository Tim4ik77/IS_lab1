package ru.example.tickets;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

import ru.example.tickets.entity.Coordinates;

import java.util.*;

@SpringBootTest(
        properties = {
            "spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://127.0.0.1:5433/tickets?currentSchema=ticket_test}"
        })
@AutoConfigureMockMvc
@Transactional
class TicketIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @PersistenceContext EntityManager em;
    long coordinates, location, person, event;
    int venue;

    JsonNode request(String method, String path, Object body, int status) throws Exception {
        var builder =
                switch (method) {
                    case "POST" -> post(path);
                    case "PUT" -> put(path);
                    case "DELETE" -> delete(path);
                    default -> get(path);
                };
        if (body != null)
            builder.contentType("application/json").content(json.writeValueAsBytes(body));
        var response = mvc.perform(builder).andReturn().getResponse();
        assertThat(response.getStatus())
                .as(method + " " + path + ": " + response.getContentAsString())
                .isEqualTo(status);
        return response.getContentAsByteArray().length == 0
                ? null
                : json.readTree(response.getContentAsByteArray());
    }

    JsonNode create(String kind, Object data) throws Exception {
        return request("POST", "/api/references/" + kind, data, 201);
    }

    @BeforeEach
    void setup() throws Exception {
        coordinates = create("coordinates", Map.of("x", 156, "y", -12)).get("id").asLong();
        location =
                create("locations", Map.of("x", 0, "y", 0, "z", 0, "name", "Дом"))
                        .get("id")
                        .asLong();
        person = create("persons", Map.of("locationId", location)).get("id").asLong();
        event =
                create(
                                "events",
                                Map.of(
                                        "name", "Концерт",
                                        "ticketsCount",
                                        100,
                                        "eventType", "CONCERT"))
                        .get("id")
                        .asLong();
        venue = create("venues", Map.of("name", "Зал", "type", "THEATRE")).get("id").asInt();
    }

    Map<String, Object> ticket(String name) {
        var r = new HashMap<String, Object>();
        r.put("name", name);
        r.put("coordinatesId", coordinates);
        r.put("eventId", event);
        r.put("venueId", venue);
        r.put("price", 1000);
        r.put("discount", 10);
        return r;
    }

    JsonNode newTicket(String name) throws Exception {
        return request("POST", "/api/tickets", ticket(name), 201);
    }

    @Test
    void crudAndNestedDetails() throws Exception {
        var created = newTicket("Первый");
        long id = created.get("id").asLong();
        assertThat(id).isPositive();
        assertThat(created.get("creationDate").asText()).isNotBlank();
        assertThat(created.get("coordinates").get("x").asDouble()).isEqualTo(156);
        assertThat(created.get("person").isNull()).isTrue();
        var update = ticket("Изменён");
        update.put("personId", person);
        var changed = request("PUT", "/api/tickets/" + id, update, 200);
        assertThat(changed.get("creationDate")).isEqualTo(created.get("creationDate"));
        assertThat(changed.at("/person/location/name").asText()).isEqualTo("Дом");
        request("GET", "/api/tickets/" + id, null, 200);
        request("DELETE", "/api/tickets/" + id, null, 204);
        request("GET", "/api/tickets/" + id, null, 404);
        request("GET", "/api/references/events/" + event, null, 200);
    }

    @Test
    void exactFiltersSortAndPagination() throws Exception {
        newTicket("Alpha");
        newTicket("Alphabet");
        newTicket("Beta");
        var filtered = request("GET", "/api/tickets?name=Alpha", null, 200);
        assertThat(filtered.get("total").asLong()).isEqualTo(1);
        assertThat(filtered.at("/items/0/name").asText()).isEqualTo("Alpha");
        assertThat(request("GET", "/api/tickets?name=alpha", null, 200).get("total").asLong())
                .isZero();
        var page = request("GET", "/api/tickets?size=1&page=1&sort=name&direction=desc", null, 200);
        assertThat(page.at("/items/0/name").asText()).isEqualTo("Alphabet");
        request("GET", "/api/tickets?sort=price", null, 400);
        request("GET", "/api/tickets?size=0", null, 400);
        request("GET", "/api/tickets?page=-1", null, 400);
        request("GET", "/api/tickets?type=INVALID", null, 400);
        request("GET", "/api/operations/count-venue-greater", null, 400);
        request("GET", "/api/missing-resource", null, 404);
    }

    @Test
    void stringFiltersOnRelationsAndEnums() throws Exception {
        var r = ticket("Related");
        r.put("personId", person);
        r.put("type", "VIP");
        request("POST", "/api/tickets", r, 201);
        assertThat(
                        request(
                                        "GET", "/api/tickets?eventName=Концерт&venueName=Зал&locationName=Дом&type=VIP&sort=locationName",
                                        null,
                                        200)
                                .get("total")
                                .asLong())
                .isEqualTo(1);
        assertThat(
                        request("GET", "/api/tickets?type=USUAL&sort=type", null, 200)
                                .get("total")
                                .asLong())
                .isZero();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "price", "discount",
                "number", "coordinatesId",
                "eventId", "venueId",
                "personId"
            })
    void rejectsNonpositiveTicketFields(String field) throws Exception {
        var r = ticket("Invalid");
        r.put(field, 0);
        assertThat(request("POST", "/api/tickets", r, 400).get("fields").has(field)).isTrue();
    }

    @Test
    void rejectsBadInputAndReadOnlyFields() throws Exception {
        var r = ticket("");
        request("POST", "/api/tickets", r, 400);
        r = ticket("Invalid");
        r.put("discount", 101);
        request("POST", "/api/tickets", r, 400);
        r = ticket("Invalid");
        r.put("price", 1.5);
        request("POST", "/api/tickets", r, 400);
        r = ticket("Invalid");
        r.put("type", 1);
        request("POST", "/api/tickets", r, 400);
        r = ticket("Invalid");
        r.put("id", 99);
        request("POST", "/api/tickets", r, 400);
        r = ticket("Invalid");
        r.put("eventId", Long.MAX_VALUE);
        request("POST", "/api/tickets", r, 404);
        request("POST", "/api/references/coordinates", Map.of("x", 157, "y", 0), 400);
        request("POST", "/api/references/coordinates", Map.of("x", "NaN", "y", 0), 400);
        request(
                "POST", "/api/references/persons",
                Map.of("locationId", location, "weight", 0),
                400);
        request(
                "POST", "/api/references/venues",
                Map.of("name", "x", "type", "PUB", "capacity", 0),
                400);
        request("POST", "/api/references/locations", Map.of("x", 0, "y", 0, "z", 0), 400);
        create("locations", Map.of("x", 0, "y", 0, "z", 0, "name", ""));
    }

    @Test
    void functionsCountPrefixAndLess() throws Exception {
        var first = newTicket("100%_exact");
        newTicket("100xx");
        int larger = create("venues", Map.of("name", "Second", "type", "LOFT")).get("id").asInt();
        var r = ticket("Other");
        r.put("venueId", larger);
        request("POST", "/api/tickets", r, 201);
        assertThat(
                        request(
                                        "GET",
                                        "/api/operations/count-venue-greater?venueId=" + venue,
                                        null,
                                        200)
                                .get("count")
                                .asLong())
                .isEqualTo(1);
        assertThat(request("GET", "/api/operations/venue-less?venueId=" + larger, null, 200).size())
                .isEqualTo(2);
        var response =
                mvc.perform(get("/api/operations/name-prefix").param("prefix", "100%_"))
                        .andReturn()
                        .getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        var found = json.readTree(response.getContentAsByteArray());
        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0).get("id")).isEqualTo(first.get("id"));
        assertThat(request("GET", "/api/operations/name-prefix?prefix=", null, 200).size())
                .isEqualTo(3);
    }

    @Test
    void sellingAndCloningPersistAndPreserveOriginal() throws Exception {
        long id = newTicket("Sale").get("id").asLong();
        var sold =
                request(
                        "POST", "/api/operations/sell",
                        Map.of("ticketId", id, "personId", person, "amount", 1005),
                        200);
        assertThat(sold.get("price").asInt()).isEqualTo(1005);
        assertThat(sold.at("/person/id").asLong()).isEqualTo(person);
        var copy =
                request(
                        "POST", "/api/operations/clone",
                        Map.of("ticketId", id, "discount", 10),
                        201);
        assertThat(copy.get("id").asLong()).isNotEqualTo(id);
        assertThat(copy.get("price").asInt()).isEqualTo(1106);
        assertThat(copy.get("discount").asLong()).isEqualTo(10);
        assertThat(copy.at("/person/id").asLong()).isEqualTo(person);
        assertThat(request("GET", "/api/tickets/" + id, null, 200).get("price").asInt())
                .isEqualTo(1005);
    }

    @ParameterizedTest
    @ValueSource(strings = {"coordinates", "persons", "events", "venues", "locations"})
    void deletingSharedReferencesRequiresAndUsesReplacement(String kind) throws Exception {
        var r = ticket("Linked");
        r.put("personId", person);
        long ticketId = request("POST", "/api/tickets", r, 201).get("id").asLong();
        long oldId;
        JsonNode replacement;
        String path;
        switch (kind) {
            case "coordinates" -> {
                oldId = coordinates;
                replacement = create(kind, Map.of("x", 1, "y", 2));
                path = "/coordinates/id";
            }
            case "persons" -> {
                oldId = person;
                replacement = create(kind, Map.of("locationId", location, "weight", 75));
                path = "/person/id";
            }
            case "events" -> {
                oldId = event;
                replacement =
                        create(
                                kind,
                                Map.of("name", "New", "ticketsCount", 1, "eventType", "OPERA"));
                path = "/event/id";
            }
            case "venues" -> {
                oldId = venue;
                replacement = create(kind, Map.of("name", "New", "type", "PUB"));
                path = "/venue/id";
            }
            default -> {
                oldId = location;
                replacement = create(kind, Map.of("x", 1, "y", 2, "z", 3, "name", "New"));
                path = "/person/location/id";
            }
        }
        String endpoint = "/api/references/" + kind + "/" + oldId;
        request("DELETE", endpoint, null, 409);
        request("DELETE", endpoint + "?replacementId=" + oldId, null, 400);
        request("DELETE", endpoint + "?replacementId=999999999", null, 404);
        request("GET", endpoint, null, 200);
        long replacementId = replacement.get("id").asLong();
        request("DELETE", endpoint + "?replacementId=" + replacementId, null, 204);
        em.clear();
        assertThat(request("GET", "/api/tickets/" + ticketId, null, 200).at(path).asLong())
                .isEqualTo(replacementId);
        request("GET", endpoint, null, 404);
    }

    @Test
    void longValuesAreLossless() throws Exception {
        var r = ticket("Long");
        r.put("number", "9223372036854775807");
        assertThat(request("POST", "/api/tickets", r, 201).get("number").asText())
                .isEqualTo("9223372036854775807");
    }

    @Test
    void databaseRejectsInvalidRowsEvenWithoutOrm() {
        assertThatThrownBy(() -> jdbc.update("insert into coordinates(x,y) values (157,0)"))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    @Test
    void ormRejectsInvalidEntityEvenWithoutController() {
        var c = new Coordinates();
        c.setX(157f);
        c.setY(0);
        assertThatThrownBy(
                        () -> {
                            em.persist(c);
                            em.flush();
                        })
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void databaseFunctionsRejectInvalidArgumentsDirectly() {
        assertThatThrownBy(
                        () ->
                                jdbc.queryForObject(
                                        "select clone_ticket_with_discount(1,0)", Long.class))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    @Test
    void copyOverflowDoesNotInsert() throws Exception {
        var r = ticket("Overflow");
        r.put("price", Integer.MAX_VALUE);
        long id = request("POST", "/api/tickets", r, 201).get("id").asLong();
        request("POST", "/api/operations/clone", Map.of("ticketId", id, "discount", 100), 400);
        // PostgreSQL aborts this test transaction after the function raises an error; teardown
        // rolls it back.
    }
}
