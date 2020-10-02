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

import org.sfandroid.hooklib.utils.ObjectUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 待Hook的类,当方法和类同时拥有时采用方法上配置
 *
 * @author beichen
 * @date 2019/05/23
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HookClass {
    /**
     * 针对不可访问类时设置,该配置优先与{@link #cls()}
     *
     * @return Hook类名
     */
    String value() default "";

    /**
     * 系统类或能直接访问的类可以设置,默认值时可从上级继承{@link #value()}
     *
     * @return Hook类的类型
     */
    Class<?> cls() default ObjectUtils.Null.class;
}
