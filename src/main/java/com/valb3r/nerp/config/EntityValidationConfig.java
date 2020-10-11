package com.valb3r.nerp.config;

import lombok.RequiredArgsConstructor;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.event.Event;
import org.neo4j.ogm.session.event.EventListenerAdapter;
import org.springframework.context.annotation.Configuration;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class EntityValidationConfig {

    public EntityValidationConfig(SessionFactory factory, Validator validator) {
        factory.register(new Listener(validator));
    }

    @RequiredArgsConstructor
    private static class Listener extends EventListenerAdapter {

        private final Validator validator;

        @Override
        public void onPreSave(Event event) {
            Set<ConstraintViolation<Object>> errors = validator.validate(event.getObject());

            if (!errors.isEmpty()) {
                throw new IllegalStateException("Validation failed: " +
                        errors.stream()
                                .map(it -> it.getPropertyPath().toString() + ": " + it.getMessage())
                                .collect(Collectors.joining(","))
                );
            }
        }
    }
}
