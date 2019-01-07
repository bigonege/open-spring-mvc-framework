package org.springframework.stereotype;

import java.lang.annotation.*;


/**
 * 自动注入
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
	String value() default "";
}
