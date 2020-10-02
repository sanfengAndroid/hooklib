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

import org.sfandroid.hooklib.XC_MethodHook;
import org.sfandroid.hooklib.annotation.HookMatch;


/**
 * @author beichen
 * @date 2019/09/20
 */
public interface IFragment {
    /**
     * 获取该对象监听的类集合
     *
     * @return 要监听的Fragment类名, 可以同时监听多个
     */
    default String[] fragmentNames() {
        // 保留静态注解配置
        HookMatch ann = this.getClass().getAnnotation(HookMatch.class);
        if (ann == null) {
            return null;
        }
        if (ann.cls().length > 0) {
            String[] ret = new String[ann.cls().length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = ann.cls()[i].getName();
            }
            return ret;
        }
        return ann.value();
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onAttach(Context)</code> 回调之前,注意Hook根
     * Fragment时回调时机在<code>super.onAttach()</code>之前
     *
     * @param param Hook回调参数
     */
    default void onAttachBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onAttach(Context)</code> 回调之后,注意Hook根
     * Fragment时回调时机在<code>super.onAttach()</code>之后
     *
     * @param param Hook回调参数
     */
    default void onAttachAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onCreateView(LayoutInflater,ViewGroup,Bundle)</code>
     * 回调之前,注意Hook根Fragment时回调时机在<code>super.onCreateView()</code>之前,且可能子类不会调用父类
     *
     * @param param Hook回调参数
     */
    default void onCreateViewBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onCreateView(LayoutInflater,ViewGroup,Bundle)</code>
     * 回调之后,注意Hook根Fragment时回调时机在<code>super.onCreateView()</code>之后,且可能子类不会调用父类
     *
     * @param param Hook回调参数
     */
    default void onCreateViewAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onActivityCreated(Bundle)</code>回调之前,
     * 注意Hook根Fragment时回调时机在<code>super.onActivityCreated()</code>之前
     *
     * @param param Hook回调参数
     */
    default void onActivityCreatedBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onActivityCreated(Bundle)</code>回调之后,
     * 注意Hook根Fragment时回调时机在<code>super.onActivityCreated()</code>之后
     *
     * @param param Hook回调参数
     */
    default void onActivityCreatedAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onResume()</code>回调之前,注意Hook根Fragment时
     * 回调时机在<code>super.onResume()</code>之前
     *
     * @param param Hook回调参数
     */
    default void onResumeBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onResume()</code>回调之后,注意Hook根Fragment时
     * 回调时机在<code>super.onResume()</code>之后
     *
     * @param param Hook回调参数
     */
    default void onResumeAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onDestroy()</code>回调之前,注意Hook根Fragment时
     * 回调时机在<code>super.onDestroy()</code>之前
     *
     * @param param Hook回调参数
     */
    default void onDestroyBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * {@see android.support.v4.app.Fragment} <code>onDestroy()</code>回调之后,注意Hook根Fragment时
     * 回调时机在<code>super.onDestroy()</code>之后
     *
     * @param param Hook回调参数
     */
    default void onDestroyAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

}
