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

import org.sfandroid.hooklib.enums.HookCompatibleType;
import org.sfandroid.hooklib.utils.ObjectUtil;
import org.sfandroid.hooklib.utils.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * 指定Hook方法的签名,当没有该注解时则默认为 void xx();
 *
 * @author beichen
 * @date 2019/11/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HookParameter {
    /**
     * 方法参数对应的类名集合,优先于{@link #params()}
     *
     * @return 方法的参数类名
     */
    String[] names() default {};

    /**
     * 方法参数对应的类集合 默认{@link ObjectUtil.Null}是为了区分无参方法
     *
     * @return 方法的参数类
     */
    Class[] params() default {ObjectUtil.Null.class};

    /**
     * {当返回值为void时应该设置为{@link Void#TYPE}
     * 值为{@code ObjectUtils.Null.class}时忽略返回值类型查找
     *
     * @return 方法返回值的类型
     */
    Class<?> returnType() default ObjectUtil.Null.class;

    /**
     * 值为空时忽略返回值类型查找
     *
     * @return 返回值的类名
     */
    String returnName() default StringUtil.EMPTY;

    /**
     * 方法查找时起作用,为空则忽略异常查找
     *
     * @return 方法抛出的所有异常的类名
     */
    String[] exceptionNames() default {};

    /**
     * 通过{@link Method#getExceptionTypes()}获取,泛型异常则要取最顶层父类型
     * 在方法查找时起作用,为空则忽略异常查找
     *
     * @return 方法抛出的所有异常类型
     */
    Class[] exceptionTypes() default {ObjectUtil.Null.class};

    /**
     * 在匹配方法签名时是否可以自动装箱,基本类型与包装类型匹配
     *
     * @return 类型自动装箱返回 {@code true},否者返回{@code false}
     */
    boolean autoBox() default false;

    /**
     * 0 == 完全匹配
     * 1 == 子类匹配父类
     * -1 == 父类匹配子类
     */
    HookCompatibleType compatible() default HookCompatibleType.EQUAL;

}
