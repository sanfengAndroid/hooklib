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

import org.sfandroid.hooklib.interfaces.IHookFrame;
import org.sfandroid.hooklib.XC_MethodHook;

import java.lang.reflect.Member;

import de.robv.android.xposed.XposedBridge;


/**
 * @author beichen
 * @date 2020/09/28
 */
public class XposedFrame implements IHookFrame<de.robv.android.xposed.XC_MethodHook,
        de.robv.android.xposed.XC_MethodHook.MethodHookParam> {

    @Override
    public Object hookMethod(Member hookMethod, XC_MethodHook<de.robv.android.xposed.XC_MethodHook> callback) {
        return XposedBridge.hookMethod(hookMethod, callback.real);
    }

    @Override
    public de.robv.android.xposed.XC_MethodHook newCallback(XC_MethodHook<de.robv.android.xposed.XC_MethodHook> wrap) {
        return new de.robv.android.xposed.XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.d("Xposed", "before call: " + param.method);
                wrap.beforeHookedMethod(new ProxyMethodHookParam(param));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d("Xposed", "after call: " + param.method);
                wrap.afterHookedMethod(new ProxyMethodHookParam(param));
            }
        };
    }

    @Override
    public Class<de.robv.android.xposed.XC_MethodHook.MethodHookParam> getParamClass() {
        return de.robv.android.xposed.XC_MethodHook.MethodHookParam.class;
    }

    private static class ProxyMethodHookParam extends XC_MethodHook.MethodHookParam<de.robv.android.xposed.XC_MethodHook.MethodHookParam> {
        public ProxyMethodHookParam(de.robv.android.xposed.XC_MethodHook.MethodHookParam param) {
            super();
            this.thisObject = param.thisObject;
            this.real = param;
            this.args = param.args;
            this.method = param.method;
        }

        @Override
        public Object getResult() {
            return this.real.getResult();
        }

        @Override
        public void setResult(Object result) {
            this.real.setResult(result);
        }

        @Override
        public Throwable getThrowable() {
            return this.real.getThrowable();
        }

        @Override
        public void setThrowable(Throwable throwable) {
            this.real.setThrowable(throwable);
        }

        @Override
        public Object getResultOrThrowable() throws Throwable {
            return this.real.getResultOrThrowable();
        }
    }
}
