package ru.example.tickets.exception;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<?> error(int status, String message, Map<String, String> fields) {
        return ResponseEntity.status(status).body(Map.of("message", message, "fields", fields));
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<?> business(BusinessException e) {
        return error(e.getStatus(), e.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException e) {
        var fields = new LinkedHashMap<String, String>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(
                        f ->
                                fields.put(
                                        f.getField(),
                                        translate(f.getCode(), f.getDefaultMessage())));
        return error(400, "Проверьте заполнение полей", fields);
    }

    private String translate(String code, String fallback) {
        return switch (code == null ? "" : code) {
            case "NotNull" -> "Обязательное поле";
            case "Positive" -> "Значение должно быть больше 0";
            case "Size" -> "Строка не может быть пустой";
            case "Min" -> "Значение должно быть не меньше 1";
            case "Max" -> "Значение должно быть не больше 100";
            case "DecimalMax" -> "Значение должно быть не больше 156";
            default -> fallback;
        };
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<?> constraint(ConstraintViolationException e) {
        var fields = new LinkedHashMap<String, String>();
        e.getConstraintViolations()
                .forEach(
                        v ->
                                fields.put(
                                        v.getPropertyPath().toString(),
                                        translate(
                                                v.getConstraintDescriptor()
                                                        .getAnnotation()
                                                        .annotationType()
                                                        .getSimpleName(),
                                                v.getMessage())));
        return error(400, "Недопустимые значения полей", fields);
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    ResponseEntity<?> format(Exception e) {
        return error(
                400,
                "Неверный формат: проверьте числа, допустимые значения и обязательные поля",
                Map.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<?> missingParameter(MissingServletRequestParameterException e) {
        return error(400, "Не указан обязательный параметр: " + e.getParameterName(), Map.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<?> missingResource(NoResourceFoundException e) {
        return error(404, "Ресурс не найден", Map.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<?> methodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return error(405, "Метод запроса не поддерживается", Map.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> other(Exception e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql) {
                String state = sql.getSQLState();
                if ("P0002".equals(state)) return error(404, databaseMessage(sql), Map.of());
                if ("22023".equals(state)) return error(400, databaseMessage(sql), Map.of());
                if ("23503".equals(state))
                    return error(
                            409,
                            "Объект используется или связанный объект уже удалён. Обновите данные и"
                                    + " выберите замену.",
                            Map.of());
                if (state != null && (state.startsWith("22") || state.startsWith("23")))
                    return error(
                            400,
                            "Данные нарушают ограничения БД: проверьте диапазоны чисел и"
                                    + " обязательные поля",
                            Map.of());
                if (state != null && state.startsWith("40"))
                    return error(
                            409,
                            "Данные изменяются другим запросом. Повторите операцию.",
                            Map.of());
            }
        }
        org.slf4j.LoggerFactory.getLogger(getClass()).error("Request failed", e);
        return error(500, "Ошибка сервера. Подробности записаны в журнал сервера.", Map.of());
    }

    private String databaseMessage(SQLException e) {
        return e.getMessage().split("\\n")[0].replaceFirst("^ERROR: ", "");
    }
}
