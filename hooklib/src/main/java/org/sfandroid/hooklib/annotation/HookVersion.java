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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当在方法上默认配置时会继承类上的配置,当类上也为默认值时则支持所有版本,
 * 在方法配置中有任意一个配置小于0时则从类上继承,若类上配置的值也小于0时则抛出错误
 *
 * @author beichen
 * @date 2019/11/05
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HookVersion {
    /**
     * 与{@link #max()},{@link #min()}不同时生效,当前所支持的版本号集合,
     * 该配置优先级高于 {@link #min()},{@link #max()}
     *
     * @return 当前支持的版本号
     */
    long[] value() default {};

    /**
     * 最大受支持的版本号,值必须大于0才有效,否者忽略版本号
     *
     * @return 最大支持的版本号(包含)
     */
    long max() default 0;

    /**
     * 最小受支持的版本号,值必须大于0才有效,否者忽略版本号
     *
     * @return 最小支持版本号(包含)
     */
    long min() default 0;
}
