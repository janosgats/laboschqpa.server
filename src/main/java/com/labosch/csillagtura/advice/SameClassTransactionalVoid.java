package com.labosch.csillagtura.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

@Target(ElementType.METHOD)
public @interface SameClassTransactionalVoid {
}
