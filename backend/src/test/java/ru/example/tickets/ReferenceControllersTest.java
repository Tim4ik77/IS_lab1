package ru.example.tickets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.example.tickets.controller.*;
import ru.example.tickets.entity.*;
import ru.example.tickets.exception.GlobalExceptionHandler;
import ru.example.tickets.repository.EntityRepository;
import ru.example.tickets.service.*;

class ReferenceControllersTest {
    private EntityRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        repository = mock(EntityRepository.class);
        var deletion = new ReferenceDeletionService(repository);
        mvc = MockMvcBuilders.standaloneSetup(
                        new CoordinatesController(new CoordinatesService(repository, deletion)),
                        new LocationController(new LocationService(repository, deletion)),
                        new PersonController(new PersonService(repository, deletion)),
                        new EventController(new EventService(repository, deletion)),
                        new VenueController(new VenueService(repository, deletion)))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        var coordinates = new Coordinates();
        coordinates.setId(1L);
        var location = new Location();
        location.setId(1L);
        location.setName("Home");
        location.setY(0);
        var person = new Person();
        person.setId(1L);
        person.setLocation(location);
        var event = new Event();
        event.setId(1L);
        var venue = new Venue();
        venue.setId(1);
        when(repository.require(Coordinates.class, 1)).thenReturn(coordinates);
        when(repository.require(Location.class, 1)).thenReturn(location);
        when(repository.require(Person.class, 1)).thenReturn(person);
        when(repository.require(Event.class, 1)).thenReturn(event);
        when(repository.require(Venue.class, 1)).thenReturn(venue);
    }

    static Stream<Arguments> requests() {
        return Stream.of(
                Arguments.of("coordinates", "{\"x\":42,\"y\":-3}", "$.y", -3, Coordinates.class),
                Arguments.of("locations", "{\"x\":1,\"y\":2,\"z\":3,\"name\":\"\"}", "$.name", "", Location.class),
                Arguments.of("persons", "{\"locationId\":1,\"weight\":75}", "$.location.name", "Home", Person.class),
                Arguments.of("events", "{\"name\":\"Opera\",\"ticketsCount\":5,\"eventType\":\"OPERA\"}", "$.eventType", "OPERA", Event.class),
                Arguments.of("venues", "{\"name\":\"Hall\",\"capacity\":10,\"type\":\"THEATRE\"}", "$.capacity", 10, Venue.class));
    }

    @ParameterizedTest
    @MethodSource("requests")
    void createsThroughExistingRoutes(String kind, String body, String path, Object value, Class<?> type)
            throws Exception {
        mvc.perform(post("/api/references/" + kind).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(path).value(value));
        verify(repository).persist(isA(type));
        verify(repository, never()).flush();
    }

    @ParameterizedTest
    @MethodSource("requests")
    void updatesTypedEntityAndReturnsNestedView(String kind, String body, String path, Object value, Class<?> type)
            throws Exception {
        mvc.perform(put("/api/references/" + kind + "/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath(path).value(value));
        mvc.perform(get("/api/references/" + kind + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(path).value(value));
        verify(repository).flush();
        verify(repository, never()).persist(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"coordinates", "locations", "persons", "events", "venues"})
    void rejectsMissingRequiredFieldsBeforeCallingRepository(String kind) throws Exception {
        mvc.perform(post("/api/references/" + kind).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isNotEmpty());
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"coordinates", "locations"})
    void rejectsNonfiniteCoordinates(String kind) throws Exception {
        String body = kind.equals("coordinates")
                ? "{\"x\":\"-Infinity\",\"y\":0}"
                : "{\"x\":\"NaN\",\"y\":0,\"z\":0,\"name\":\"Home\"}";
        mvc.perform(post("/api/references/" + kind).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verify(repository, never()).persist(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"coordinates", "locations", "persons", "events", "venues"})
    void keepsListAndLinkRoutes(String kind) throws Exception {
        mvc.perform(get("/api/references/" + kind)).andExpect(status().isOk())
                .andExpect(content().json("[]"));
        mvc.perform(get("/api/references/" + kind + "/1/links")).andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));
    }
}
