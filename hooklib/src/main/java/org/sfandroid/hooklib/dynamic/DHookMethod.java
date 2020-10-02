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
import org.sfandroid.hooklib.enums.HookProcessType;

/**
 * @author beichen
 */
public class DHookMethod {
    public static final int UN_BIND = -1;
    public static final DHookMethod DEFAULT = new DHookMethod();
    /**
     * 方法对应的参数签名,如果为null则从绑定的方法上查找
     */
    public DHookParameter param;

    /**
     * 方法所启用的进程,如果为{@code null}则从绑定的方法上继续查找
     * 如果为{@link HookProcessType#INHERIT}时应该从上级查找
     */
    public DHookProcess process;

    /**
     * 方法所使用的版本,如果为null则从绑定的方法上继续查找
     */
    public DHookVersion version;

    /**
     * 与对应的回调方法绑定,该方法必须包含{@link HookMethodConfigure#bind()}
     */
    public int bindId = UN_BIND;

    public DHookMethod() {
    }

    public DHookMethod(DHookParameter param, DHookProcess process, DHookVersion version) {
        this.param = param;
        this.process = process;
        this.version = version;
    }
}
