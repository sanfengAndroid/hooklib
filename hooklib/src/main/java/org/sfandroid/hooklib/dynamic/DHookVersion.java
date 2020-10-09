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

import org.sfandroid.hooklib.annotation.HookVersion;
import org.sfandroid.hooklib.utils.ArrayUtil;

import java.util.Arrays;

/**
 * 动态配置与{@link HookVersion}对应
 *
 * @author beichen
 * @date 2019/11/12
 */
public class DHookVersion {
    public static final DHookVersion DEFAULT = new DHookVersion();
    public long[] values;
    public long min = 0;
    public long max = 0;

    private DHookVersion() {
    }

    private DHookVersion(long[] versions, long min, long max) {
        this.values = versions;
        this.min = min;
        this.max = max;
    }

    public static DHookVersion create(long[] versions, long min, long max) {
        return new DHookVersion(versions, min, max);
    }

    public static DHookVersion toDVersion(HookVersion hookVersion) {
        if (hookVersion == null) {
            return null;
        }
        DHookVersion ret = new DHookVersion();
        ret.values = hookVersion.value();
        ret.min = hookVersion.min();
        ret.max = hookVersion.max();
        return optimization(ret);
    }

    private static DHookVersion optimization(DHookVersion dHookVersion) {
        // 主要优化该对象是否与默认相等
        return DEFAULT.equals(dHookVersion) ? DEFAULT : dHookVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DHookVersion version = (DHookVersion) o;

        if (min != version.min) return false;
        if (max != version.max) return false;
        return Arrays.equals(values, version.values);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(values);
        result = 31 * result + (int) (min ^ (min >>> 32));
        result = 31 * result + (int) (max ^ (max >>> 32));
        return result;
    }

    /**
     * 当前配置是否无效,需要从上级获取
     *
     * @return 是否从上级获取
     */
    public boolean inherit() {
        boolean inherit = false;
        if (!ArrayUtil.isEmpty(values)) {
            for (long ver : values) {
                if (ver < 0) {
                    inherit = true;
                    break;
                }
            }
        }
        return inherit || min < 0 || max < 0;
    }
}
