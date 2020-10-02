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

import org.sfandroid.hooklib.enums.HookProcessType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hook的进程规则,当方法和类同时拥有时则取方法上的配置
 *
 * @author beichen
 * @date 2019/11/07
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HookProcess {
    /**
     * 当指定为{@link HookProcessType#INHERIT}时从类上继承
     *
     * @return 进程过滤类型
     */
    HookProcessType value() default HookProcessType.INHERIT;

    /**
     * 当{@link #value()} == {@link HookProcessType#SPECIAL} / {@link HookProcessType#SPECIAL_AND_MAIN} 时生效
     *
     * @return 指定进程名称
     */
    String[] processes() default {};
}
