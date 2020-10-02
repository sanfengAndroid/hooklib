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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当使用方法签名推断要Hook的签名时,设置参数类型的真实类名,若参数类型可见则无需设置
 *
 * @author beichen
 * @date 2019/11/21
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HookFieldConfigure {
    String value() default "";

    boolean autoBox() default false;

    HookCompatibleType compatible() default HookCompatibleType.EQUAL;
}
