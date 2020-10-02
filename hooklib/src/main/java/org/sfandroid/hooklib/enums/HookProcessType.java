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

package org.sfandroid.hooklib.enums;

/**
 * @author beichen
 * @date 2019/09/24
 */
public enum HookProcessType {
    /**
     * 从上级配置继承
     */
    INHERIT,
    /**
     * 只Hook主进程
     */
    MAIN,
    /**
     * 只Hook指定进程,需搭配其它配置
     */
    SPECIAL,

    /**
     * Hook指定进程和主进程
     */
    SPECIAL_AND_MAIN,
    /**
     * 只Hook非主进程
     */
    NON_MAIN,
    /**
     * Hook所有进程
     */
    ALL;
}
