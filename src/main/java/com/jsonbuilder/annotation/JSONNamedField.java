package com.jsonbuilder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation responsible for define the attributes that will be allowed on the build of a JSON.<br/>
 * <br/>
 * <b>name</b> - Alias to JSONNamedField<br/>
 * <b>fields</b> - Class attributes that will be allowed on the build of a JSON.
 * 
 * @author Emmanuel Kiametis
 * @author Messias Xavier Sant'Ana
 * @author Hugo Arthur Amaral
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONNamedField {
	String name();
	String[] fields();
}
