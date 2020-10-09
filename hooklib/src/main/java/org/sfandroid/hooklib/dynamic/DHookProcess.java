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

import org.sfandroid.hooklib.annotation.HookProcess;
import org.sfandroid.hooklib.enums.HookProcessType;
import org.sfandroid.hooklib.utils.ArrayUtil;

import java.util.Arrays;

/**
 * hook进程配置,应该有默认值所有进程都开启
 *
 * @author beichen
 */
public class DHookProcess {
    public static final DHookProcess DEFAULT = new DHookProcess();
    public HookProcessType hookProcessType = HookProcessType.ALL;
    public String[] processes;

    private DHookProcess() {
    }

    private DHookProcess(HookProcessType type, String[] processes) {
        this.hookProcessType = type;
        this.processes = processes;
    }

    public DHookProcess(HookProcessType hookProcessType) {
        this.hookProcessType = hookProcessType;
    }

    public static DHookProcess create(HookProcessType type, String[] processes) {
        return new DHookProcess(type, processes);
    }

    public static DHookProcess toDProcess(HookProcess process) {
        if (process == null) {
            return null;
        }
        DHookProcess dp = new DHookProcess();
        dp.hookProcessType = process.value();
        if (!ArrayUtil.isEmpty(process.processes())) {
            dp.processes = process.processes();
        }
        return optimization(dp);
    }

    private static DHookProcess optimization(DHookProcess process) {
        return DEFAULT.equals(process) ? DEFAULT : process;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DHookProcess process = (DHookProcess) o;

        if (hookProcessType != process.hookProcessType) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(processes, process.processes);
    }

    @Override
    public int hashCode() {
        int result = hookProcessType.hashCode();
        result = 31 * result + Arrays.hashCode(processes);
        return result;
    }
}
