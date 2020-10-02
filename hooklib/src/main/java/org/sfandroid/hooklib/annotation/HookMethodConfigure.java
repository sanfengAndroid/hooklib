/*
 * Copyright (c) 2020 HookLib by sfandroid.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sfandroid.hooklib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定Hook回调方法,方法必须有此注解才能生效
 *
 * @author beichen
 * @date 2019/11/07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HookMethodConfigure {
    /**
     * @return 启用Hook则返回 {@code true},否者{@code false}
     */
    boolean value() default true;

    /**
     * 当名称为空且没有开启动态查找{@link #find()},{@link #findAll()}时则根据配置该参数的方法名称来推断
     * 例如:
     * {@code onCreateBefore}/{@code onCreate},Hook的方法名则为{@code onCreate},且在方法调用前回调
     * {@code onCreateAfter},Hook的方法名为{@code onCreate},且在方法调用后回调
     * 这里不支持方法替换,采用Before方法设置返回值忽略After方法调用即可
     * 构造函数应为{@code <init>},且可以同{@link #find()}{@link #findAll()}搭配查找符合条件的构造函数
     *
     * @return 方法名称
     */
    String name() default "";

    /**
     * 查找第一个匹配的方法,跟{@link HookParameter}搭配,当查找的是构造函数时忽略异常签名查找
     *
     * @return 动态查找方法时返回 {@code true},否者{@code false}
     */
    boolean find() default false;

    /**
     * 查找所有匹配的方法,跟{@link HookParameter}搭配,当查找的是构造函数时忽略异常签名查找
     *
     * @return 动态查找方法时返回 {@code true},否者{@code false}
     */
    boolean findAll() default false;

    /**
     * @return Hook当前所有同名方法则返回 {@code true},否者{@code false}
     */
    boolean all() default false;

    /**
     * 指定当前方法回调时机,在{@link #name()}为空的情况下不生效
     *
     * @return 方法调用前回调则返回 {@code true},否者{@code false}
     */
    boolean before() default true;

    /**
     * 当Hook为动态绑定时用到
     *
     * @return 方法绑定的id
     */
    int[] bind() default {};

    /**
     * 当{@link #name()}为构造函数时忽略该参数
     *
     * @return 可以查找父类匹配方法则返回 {@code true}否者{@code false}
     */
    boolean findSuper() default false;
}
