package com.persoff68.fatodo.config.annotation;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionalOnPropertyNotNull.OnPropertyNotNullCondition.class)
public @interface ConditionalOnPropertyNotNull {

    String value();

    class OnPropertyNotNullCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Map<String, Object> attrs = metadata.getAnnotationAttributes(ConditionalOnPropertyNotNull.class.getName());
            String propertyName = (String) attrs.get("value");
            String val = context.getEnvironment().getProperty(propertyName);
            return val != null && !val.trim().equals("null") && !val.trim().isEmpty();
        }
    }
}
