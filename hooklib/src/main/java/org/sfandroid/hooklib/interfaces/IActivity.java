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
 * @author beichen
 * @date 2019/09/20
 */


import org.sfandroid.hooklib.XC_MethodHook;
import org.sfandroid.hooklib.annotation.HookMatch;


/**
 * 通用 {@link android.app.Activity} 生命周期函数回调
 * <b>注意: 此接口关联 Activity 生命周期函数,可以通用Hook系统Activity也可具体某一个子类</b>
 */
public interface IActivity {

    /**
     * 获取该对象监听的类集合
     *
     * @return 要监听的Activity类名, 可以同时监听多个,子类Hook时单独监听
     */
    default String[] activityNames() {
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

    default void onStartBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    default void onStartAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * Activity onResume 方法执行之前回调,注意Hook系统{@link android.app.Activity}时回调时机是在
     * super.onResume()之前
     *
     * @param param hook回调参数
     */
    default void onResumeBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * Activity onResume 方法执行之后回调,注意Hook系统{@link android.app.Activity}时回调时机是在
     * super.onResume()之后
     *
     * @param param hook回调参数
     */
    default void onResumeAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * Activity onCreate 方法执行之前回调,注意Hook系统{@link android.app.Activity}时回调时机是在
     * super.onCreate()之前
     *
     * @param param hook回调参数
     */
    default void onCreateBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * Activity onCreate 方法执行之后回调,注意Hook系统{@link android.app.Activity}时回调时机是在
     * super.onCreate()之后
     *
     * @param param hook回调参数
     */
    default void onCreateAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    default void onPauseBefore(XC_MethodHook.MethodHookParam param) throws Throwable {

    }

    default void onPauseAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    default void finishBefore(XC_MethodHook.MethodHookParam param) throws Throwable {

    }

    default void finishAfter(XC_MethodHook.MethodHookParam param) throws Throwable {

    }

    /**
     * Activity onDestroy 方法执行之前回调,注意Hook系统{@link android.app.Activity}时回调时机是在
     * super.onDestroy()之前
     *
     * @param param hook回调参数
     */
    default void onDestroyBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    /**
     * Activity onDestroy 方法执行之后回调,注意Hook系统{@link android.app.Activity}时回调时机是在
     * super.onDestroy()之后
     *
     * @param param hook回调参数
     */
    default void onDestroyAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    default void isTaskRootAfter(XC_MethodHook.MethodHookParam param) throws Throwable {

    }
}
