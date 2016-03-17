package com.jsonbuilder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation responsible for store several {@link JSONNamedField}.
 * 
 * @author Emmanuel Kiametis
 * @author Messias Xavier Sant'Ana
 * @author Hugo Arthur Amaral
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONNamedFields {
	JSONNamedField[] value();
}
