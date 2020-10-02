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

package org.sfandroid.hooklib.interfaces;


import org.sfandroid.hooklib.IHook;
import org.sfandroid.hooklib.dynamic.DHookClass;

/**
 * 动态Hook项,实现接口可以动态添加Hook项
 *
 * @author beichen
 * @date 2019/09/19
 */
public interface IDHook extends IHook {
    /**
     * 获取当前对象所有配置的Hook项
     * 与{@link #getHooks()}二选一实现
     *
     * @return 当前对象配置的动态Hook
     */
    default DHookClass getHook() {
        return null;
    }

    /**
     * 获取当前对象所有配置的Hook项
     * 与{@link #getHook()}二选一实现
     *
     * @return 当前对象配置的动态Hook
     */
    default DHookClass[] getHooks() {
        DHookClass hook = getHook();
        return hook == null ? null : new DHookClass[]{hook};
    }
}
