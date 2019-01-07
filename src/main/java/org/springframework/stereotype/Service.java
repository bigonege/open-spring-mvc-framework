package org.springframework.stereotype;

import java.lang.annotation.*;

/**
 * 业务逻辑,注入接口
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
	String value() default "";
}
