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

import org.sfandroid.hooklib.annotation.HookMethodConfigure;
import org.sfandroid.hooklib.utils.ArrayUtils;
import org.sfandroid.hooklib.utils.StringUtils;

/**
 * 动态Hook配置,与{@link HookMethodConfigure}大部分参数一一对应
 *
 * @author beichen
 */
public class DHook {
    /**
     * 当before和after回调绑定值为-1时则忽略
     */
    public static final int INVALID_BINDING = -1;
    /**
     * Hook构造函数时应该配置的方法名
     */
    public static final String CONSTRUCTOR_NAME = "<init>";
    /**
     * 是否开启Hook,为空时从绑定的方法上获取
     */
    public Boolean enable;
    /**
     * 方法名,为空时则应该走查找或动态解析,构造函数名字应为{@link #CONSTRUCTOR_NAME}
     */
    public String name;
    /**
     * 是否走方法查找,为空时从绑定的方法上获取
     */
    public Boolean find;
    /**
     * 是否走方法查找,为空时从绑定的方法上获取
     */
    public Boolean findAll;
    /**
     * 是否Hook所有方法,为空时从绑定的方法上获取
     */
    public Boolean all;

    public Boolean findSuper;
    /**
     * before回调绑定值
     */
    public int beforeBind = INVALID_BINDING;
    /**
     * after回调绑定值
     */
    public int afterBind = INVALID_BINDING;

    /**
     * 将静态注解转换为动态配置对象,注意当方法拥有多个绑定id时只取第一个
     *
     * @param HookMethodConfigure Hook配置注解
     * @return 动态配置对象
     */
    public static DHook toDHook(HookMethodConfigure HookMethodConfigure) {
        DHook hook = new DHook();
        if (HookMethodConfigure == null) {
            return hook;
        }
        hook.enable = HookMethodConfigure.value();
        if (!StringUtils.isEmpty(HookMethodConfigure.name())) {
            hook.name = HookMethodConfigure.name();
        }
        hook.find = HookMethodConfigure.find();
        hook.findAll = HookMethodConfigure.findAll();
        hook.all = HookMethodConfigure.all();
        if (!ArrayUtils.isEmpty(HookMethodConfigure.bind())) {
            if (HookMethodConfigure.before()) {
                hook.beforeBind = HookMethodConfigure.bind()[0];
            } else {
                hook.afterBind = HookMethodConfigure.bind()[0];
            }
        }
        hook.findSuper = HookMethodConfigure.findSuper();
        return hook;
    }


}
