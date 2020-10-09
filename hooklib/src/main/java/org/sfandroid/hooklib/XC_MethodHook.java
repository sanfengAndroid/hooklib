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

import org.sfandroid.hooklib.interfaces.IHookFrame;
import org.sfandroid.hooklib.utils.ClassUtil;

import java.lang.reflect.Member;

/**
 * {@link IHookFrame} 与之关联,这里只是提供一个通用的包装层,这样做到快速切换Hook框架实现
 *
 * @author beichen
 * @date 2019/11/03
 */
public class XC_MethodHook<Callback> {
    /**
     * 真实的回调对象
     */
    public Callback real;

    public void beforeHookedMethod(final MethodHookParam<?> param) throws Throwable {
    }

    public void afterHookedMethod(final MethodHookParam<?> param) throws Throwable {
    }

    public static class MethodHookParam<Param> {
        /**
         * 真实方法Hook回调参数
         */
        public Param real;
        /**
         * 实例方法{@code this}或静态方法{@code null}
         */
        public Object thisObject;
        /**
         * Hook的方法或构造函数
         */
        public Member method;

        public Object[] args;

        /**
         * 这里只添加调用入口,具体实现必须框架适配重写该方法
         *
         * @return 获取方法返回值
         */
        public Object getResult() {
            throw new UnsupportedOperationException("hook param class " + ClassUtil.getName(real,
                    "null") + " did not override this method getResult().");
        }

        public void setResult(Object result) {
            throw new UnsupportedOperationException("hook param class " + ClassUtil.getName(real, "null") + " did not override this method setResult(Object).");
        }

        public Throwable getThrowable() {
            throw new UnsupportedOperationException("hook param class " + ClassUtil.getName(real, "null") + " did not override this method getThrowable().");
        }

        public void setThrowable(Throwable throwable) {
            throw new UnsupportedOperationException("hook param class " + ClassUtil.getName(real, "null") + " did not override this method setThrowable(Throwable).");
        }

        public Object getResultOrThrowable() throws Throwable {
            throw new UnsupportedOperationException("hook param class " + ClassUtil.getName(real, "null") + " did not override this method getResultOrThrowable().");
        }
    }
}
