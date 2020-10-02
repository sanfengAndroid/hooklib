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

package com.beichen.hooklib.test;

import android.util.Log;

import org.sfandroid.hooklib.IHook;
import org.sfandroid.hooklib.XC_MethodHook;
import org.sfandroid.hooklib.annotation.HookClass;
import org.sfandroid.hooklib.annotation.HookMethodConfigure;
import org.sfandroid.hooklib.annotation.HookFieldConfigure;
import org.sfandroid.hooklib.annotation.HookMethod;
import org.sfandroid.hooklib.annotation.HookParameter;
import org.sfandroid.hooklib.annotation.HookImplicit;
import org.sfandroid.hooklib.enums.HookCompatibleType;
import org.sfandroid.hooklib.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author beichen
 * @date 2019/11/25
 */
@HookClass(value = "com.beichen.hooklib.test.StaticHookClassTest")
public class StaticNameHook implements IHook {
    private static final String TAG = "StaticHook";

    @HookMethod
    public static void instanceOnlyThisAfter(Object thiz) {
        Log.i(TAG, "Default only this object callback instance method after: " + thiz);
        try {
            FieldUtils.writeDeclaredField(thiz, "hook", false, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @HookMethod(hook = @HookMethodConfigure(name = "instanceOnlyHookWrap", before = false, all = true))
    public static void instanceOnlyHookWrapWrapAfter(XC_MethodHook.MethodHookParam param) {
        Log.i(TAG, "Default only MethodHookParam callback instance method after");
        param.args[0] = false;
    }

    @HookMethod(hook = @HookMethodConfigure(name = "instanceHookWrapAndThis", before = false), param = @HookParameter(params = boolean.class))
    public static void instanceHookWrapAndThisAfter(XC_MethodHook.MethodHookParam param, Object thiz) {
        Log.i(TAG, "Default MethodHookParam and this object callback instance method after: " + thiz);
        param.args[0] = false;
        try {
            FieldUtils.writeDeclaredField(thiz, "hook", false, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @HookImplicit
    @HookMethod
    public static void instanceParamAfter(int i1, @HookFieldConfigure(autoBox = true) int i2, List list1,
                                          @HookFieldConfigure(compatible = HookCompatibleType.DOWN) List list2) {
        Log.i(TAG, "Default parameters callback instance method after");
        list1.clear();
        list2.clear();
    }

    @HookMethod(implicit = true)
    public static void instanceHookWrapAndParamAfter(XC_MethodHook.MethodHookParam param, boolean hooked, int i1, List list) {
        Log.i(TAG, "Default MethodHookParam and parameters callback instance method after");
        param.args[0] = false;
        list.clear();
    }

    @HookMethod(hook = @HookMethodConfigure(find = true, before = false), param = @HookParameter(params = {boolean.class, int.class}))
    public static void instanceHookWrapThisAndParamAfter(XC_MethodHook.MethodHookParam param,
                                                         Object thiz, boolean hooked, int i) {
        Log.i(TAG, "Default MethodHookParam, parameters and this object callback instance method after: " + thiz);
        param.args[0] = false;
        try {
            FieldUtils.writeDeclaredField(thiz, "hook", false, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @HookMethod(hook = @HookMethodConfigure(name = "instanceOnlyThis"))
    public void instanceOnlyThisBefore(Object thiz) {
        Log.i(TAG, "Default only this object callback instance method before: " + thiz);
        try {
            FieldUtils.writeDeclaredField(thiz, "hook", true, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @HookMethod(hook = @HookMethodConfigure(name = "instanceOnlyHookWrap"), param = @HookParameter(params = boolean.class))
    public void instanceOnlyHookWrapBefore(XC_MethodHook.MethodHookParam param) {
        Log.i(TAG, "Default only MethodHookParam callback instance method before");
        param.args[0] = true;
    }

    @HookMethod(hook = @HookMethodConfigure(name = "instanceHookWrapAndThis"), param = @HookParameter(params = boolean.class))
    public void instanceHookWrapAndThisBefore(XC_MethodHook.MethodHookParam param, Object thiz) {
        Log.i(TAG, "Default MethodHookParam and this object callback instance method before: " + thiz);
        param.args[0] = true;
        try {
            FieldUtils.writeDeclaredField(thiz, "hook", true, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @HookMethod(hook = @HookMethodConfigure(name = "instanceParam"), param = @HookParameter(params = {int.class,
            int.class, List.class, List.class}, autoBox = true, compatible = HookCompatibleType.DOWN))
    public void instanceParamBefore(int i1, Integer i2, List list1, ArrayList list2) {
        Log.i(TAG, "Default parameters callback instance method before");
        list1.add(new Object());
        list2.add(new Object());
    }

    @HookMethod(hook = @HookMethodConfigure(name = "instanceHookWrapAndParam"), param = @HookParameter(params = {boolean.class, int.class, List.class}))
    public void instanceHookWrapAndParamBefore(XC_MethodHook.MethodHookParam param, boolean hooked, int i1, List list) {
        Log.i(TAG, "Default MethodHookParam and parameters callback instance method before");
        param.args[0] = true;
        list.add(new Object());
    }

    @HookMethod(hook = @HookMethodConfigure(findAll = true), param = @HookParameter(params = {boolean.class, int.class}))
    public void instanceHookWrapThisAndParamBefore(XC_MethodHook.MethodHookParam param,
                                                   Object thiz, boolean hooked, int i) {
        Log.i(TAG, "Default MethodHookParam, parameters and this object callback instance method before: " + thiz);
        param.args[0] = true;
        try {
            FieldUtils.writeDeclaredField(thiz, "hook", true, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
