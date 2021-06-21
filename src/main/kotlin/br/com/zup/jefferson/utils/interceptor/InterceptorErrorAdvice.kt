package br.com.zup.jefferson.utils.interceptor

import io.micronaut.aop.Around
import javax.validation.Constraint


@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Around
annotation class InterceptorErrorAdvice
