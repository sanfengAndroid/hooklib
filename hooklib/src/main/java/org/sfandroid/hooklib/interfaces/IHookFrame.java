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

import java.lang.reflect.Member;

/**
 * @param <Callback> 真实的回调对象如{@code de.robv.android.xposed.XC_MethodHook}
 * @param <Param>    真实回调对象的参数如{@code de.robv.android.xposed.XC_MethodHook$MethodHookParam}
 * @author beichen
 */
public interface IHookFrame<Callback, Param> {

    /**
     * Hook方法的具体实现,{@link XC_MethodHook#real}保存了真实的回调对象
     *
     * @param hookMethod 待Hook方法
     * @param callback   Hook回调对象
     * @return 与具体框架有关, 暂未使用
     */
    Object hookMethod(final Member hookMethod, final XC_MethodHook<Callback> callback);

    /**
     * 实例化一个新的真实回调对象,且该真实回调对象的{@code beforeHookedMethod}和{@code afterHookedMethod}方法
     * 必须回调{@link XC_MethodHook#beforeHookedMethod(XC_MethodHook.MethodHookParam)} 和{@link XC_MethodHook#afterHookedMethod(XC_MethodHook.MethodHookParam)}
     *
     * @param wrap 包装的回调对象
     * @return 真实回调对象
     */
    Callback newCallback(XC_MethodHook<Callback> wrap);

    /**
     * @return 真实Hook回调参数类型
     */
    Class<Param> getParamClass();

}
