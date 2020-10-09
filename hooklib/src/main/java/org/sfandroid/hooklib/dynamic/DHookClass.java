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

package org.sfandroid.hooklib.dynamic;

import org.sfandroid.hooklib.annotation.HookClass;
import org.sfandroid.hooklib.annotation.HookProcess;
import org.sfandroid.hooklib.annotation.HookVersion;
import org.sfandroid.hooklib.enums.HookProcessType;
import org.sfandroid.hooklib.utils.ObjectUtil;
import org.sfandroid.hooklib.utils.StringUtil;

/**
 * 动态Hook类
 *
 * @author beichen
 */
public class DHookClass {
    /**
     * 待Hook的类名,优先于{@link #type}
     */
    public String name;
    /**
     * 待Hook的类型
     * 当它和{@link #name}都为空时则从类上继承{@link HookClass}
     */
    public Class<?> type;

    /**
     * 要Hook的方法集合
     */
    public DHookMethod[] methods;

    /**
     * 配置当前类启用Hook的进程,如果{@link #methods}配置为{@link HookProcessType#INHERIT}或{@code null}时会查找当前配置,
     * 如果当前配置也为{@code null} 或{@link HookProcessType#INHERIT}则从对应的类上获取注解配置
     * {@link HookProcess}
     */
    public DHookProcess process;

    /**
     * 配置当前类启用Hook的版本,如果{@link #methods}配置为{@code null}时会查找当前配置,
     * 如果当前配置也为空则从类上获取注解配置{@link HookVersion}
     */
    public DHookVersion version;

    public static DHookClass toDClass(HookClass hookClass) {
        DHookClass ret = new DHookClass();
        if (hookClass == null) {
            return ret;
        }
        if (!StringUtil.isEmpty(hookClass.value())) {
            ret.name = hookClass.value();
        }
        if (!ObjectUtil.isNullClass(hookClass.clazz())) {
            ret.type = hookClass.clazz();
        }
        return ret;
    }
}
