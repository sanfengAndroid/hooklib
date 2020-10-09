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
import org.sfandroid.hooklib.utils.ObjectUtil;
import org.sfandroid.hooklib.utils.StringUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注意当方法只有一个注解时需要获取{@link HookMethod},而有多个时需要获取{@link HookMethods}
 *
 * @author beichen
 * @date 2019/11/21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(HookMethods.class)
@Inherited
public @interface HookMethod {

    /**
     * 快捷配置方法名,当还需要其它配置时使用{@link #hook()},该优先级低于{@link #hook()}配置中的方法名
     *
     * @return 方法名
     */
    String value() default StringUtil.EMPTY;

    boolean enable() default true;

    /**
     * 不可见类时配置类名,优先级高于{@link #clazz()}
     *
     * @return 待Hook类的类名
     */
    String className() default StringUtil.EMPTY;

    /**
     * 如果该类的类加载器可以直接方法则可以配置类型,比如系统类,系统隐藏类
     *
     * @return 待Hook类的类型
     */
    Class<?> clazz() default ObjectUtil.Null.class;

    HookMethodConfigure hook() default @HookMethodConfigure;

    /**
     * 只推断Hook的方法名称和参数类型,忽略返回值和异常类型推断
     *
     * @return 是否开启Hook方法推断
     */
    boolean implicit() default false;

    /**
     * @return 方法的签名配置
     */
    HookParameter param() default @HookParameter;

    /**
     * 当指定为{@link HookProcessType#INHERIT}时从类上继承
     *
     * @return 进程过滤类型
     */
    HookProcessType processType() default HookProcessType.INHERIT;

    /**
     * 当{@link #processType()} == {@link HookProcessType#SPECIAL} / {@link HookProcessType#SPECIAL_AND_MAIN} 时生效
     *
     * @return 指定进程名称
     */
    String[] processes() default {};

    /**
     * 参考{@link HookVersion}
     *
     * @return 当前支持的版本号
     */
    long[] versions() default {};

    /**
     * 参考{@link HookVersion}
     *
     * @return 最大支持的版本号(包含)
     */
    long max() default 0;

    /**
     * 参考{@link HookVersion}
     *
     * @return 最小支持版本号(包含)
     */
    long min() default 0;
}
