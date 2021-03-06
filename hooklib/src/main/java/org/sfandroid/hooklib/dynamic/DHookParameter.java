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

import org.sfandroid.hooklib.annotation.HookParameter;
import org.sfandroid.hooklib.enums.HookCompatibleType;
import org.sfandroid.hooklib.utils.ArrayUtil;
import org.sfandroid.hooklib.utils.ObjectUtil;
import org.sfandroid.hooklib.utils.StringUtil;

/**
 * 应该具有默认的方法签名, void xx();
 * 当成员值为null则意味着忽略对应的方法签名匹配
 *
 * @author beichen
 */
public class DHookParameter {
    public static final DHookParameter DEFAULT = new DHookParameter();
    /**
     * 方法所有参数的类名
     */
    public String[] pNames;
    /**
     * 方法所有参数的类型
     */
    public Class<?>[] pTypes = ArrayUtil.EMPTY_CLASS_ARRAY;
    /**
     * 方法返回值类名
     */
    public String rName;
    /**
     * 方法返回值类型
     */
    public Class<?> rType = void.class;
    /**
     * 方法所有抛出异常类名
     */
    public String[] tNames;
    /**
     * 方法所有抛出异常类型
     */
    public Class<?>[] tTypes;

    public boolean autoBox = false;

    public HookCompatibleType compatible;

    public static DHookParameter toDParameter(HookParameter hookParameter) {
        if (hookParameter == null) {
            return DEFAULT;
        }
        DHookParameter ret = new DHookParameter();
        if (!StringUtil.isEmpty(hookParameter.returnName())) {
            ret.rName = hookParameter.returnName();
        }
        ret.rType = ObjectUtil.isNullClass(hookParameter.returnType()) ? null : hookParameter.returnType();
        if (!ArrayUtil.isEmpty(hookParameter.names())) {
            ret.pNames = hookParameter.names();
        }
        ret.pTypes = ObjectUtil.isNullClasses(hookParameter.params()) ? null : hookParameter.params();
        if (!ArrayUtil.isEmpty(hookParameter.exceptionNames())) {
            ret.tNames = hookParameter.exceptionNames();
        }
        if (!ObjectUtil.isNullClasses(hookParameter.exceptionTypes())) {
            ret.tTypes = hookParameter.exceptionTypes();
        }
        ret.autoBox = hookParameter.autoBox();
        ret.compatible = hookParameter.compatible();
        // 当所有配置为空时应该有默认签名 void xx();
        return ret;
    }

    public static String toSign(HookParameter HookParameter) {
        return toDParameter(HookParameter).toString();
    }

    public boolean isEmptyParameter() {
        if (StringUtil.isNotEmpty(rName) || rType != null) {
            return false;
        }
        if (!ArrayUtil.isEmpty(pNames) || !ArrayUtil.isEmpty(pTypes)) {
            return false;
        }
        if (!ArrayUtil.isEmpty(tNames) || !ArrayUtil.isEmpty(tTypes)) {
            return false;
        }
        return true;
    }
}
