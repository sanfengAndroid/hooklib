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

import java.util.List;

/**
 * @author beichen
 * @date 2019/10/23
 */
public interface IHookCollect<T> {
    /**
     * 对通用的Hook添加监听对象
     *
     * @param listeners 监听对象集合
     */
    @SuppressWarnings({"unchecked", "varargs"})
    void addListener(T... listeners);

    /**
     * 对通用的Hook移除指定监听对象
     *
     * @param listeners 监听对象集合
     */
    @SuppressWarnings({"unchecked", "varargs"})
    void removeListener(T... listeners);

    /**
     * 获取通用Hook下所有监听对象
     *
     * @return 所有监听对象
     */
    List<T> getAll();

    /**
     * 获取通用Hook是否为空监听
     *
     * @return 监听集合为空则返回真, 否者假
     */
    boolean isEmpty();
}
