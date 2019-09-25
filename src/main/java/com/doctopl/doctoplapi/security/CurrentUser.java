package com.doctopl.doctoplapi.security;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Target(value = { ElementType.PARAMETER, ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {

}
