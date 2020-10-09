package org.sfandroid.hooklib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态配置时方法上没有配置则继续查找类上,如果类上也没配置则默认开启
 * 静态配置时{@link HookMethod#enable()}已经包含,但是还要类上和方法配置的{@link HookEnable}上同时满足才开启
 *
 * @author beichen
 * @date 2020/10/09
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HookEnable {
    /**
     * @return Hook开启返回 {@code true},否者{@code false}
     */
    boolean value() default true;
}
