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

package org.sfandroid.hooklib;

/**
 * @author beichen
 * @date 2019/9/19
 */
public interface IHook {

    /**
     * 在实际执行添加到Hook表中时先执行初始化
     */
    default void hookInit() {
    }

    /**
     * 在实际执行取消Hook时先执行hook数据清理,
     * 也可以手动调用清理数据
     */
    default void hookClear() {
    }
}
