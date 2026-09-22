package ru.example.tickets.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.*;

@Configuration
public class JsonConfig {
    // JavaScript numbers cannot represent every Java long. Return longs as decimal strings.
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longSerialization() {
        return builder ->
                builder.serializerByType(Long.class, ToStringSerializer.instance)
                        .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }
}
