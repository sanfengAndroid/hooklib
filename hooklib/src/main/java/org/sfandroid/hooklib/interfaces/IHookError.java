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

/**
 * Hook或回调错误,一个进程一个实例即可
 *
 * @author beichen
 * @date 2019/09/26
 */
public interface IHookError {
    /**
     * @param msg 错误消息
     * @param e   产生的异常
     */
    void error(String msg, Throwable e);

    void warn(String msg);

    void info(String msg);

    void error(String msg);
}
