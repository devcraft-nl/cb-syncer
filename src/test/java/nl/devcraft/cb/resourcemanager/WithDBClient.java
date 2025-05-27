package nl.devcraft.cb.resourcemanager;

import io.quarkus.test.common.WithTestResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@WithTestResource(DatabaseServerResourceManager.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WithDBClient {}
