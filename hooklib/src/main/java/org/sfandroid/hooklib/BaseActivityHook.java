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

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.sfandroid.hooklib.annotation.HookClass;
import org.sfandroid.hooklib.annotation.HookMethod;
import org.sfandroid.hooklib.annotation.HookMethodConfigure;
import org.sfandroid.hooklib.annotation.HookParameter;
import org.sfandroid.hooklib.interfaces.IActivity;
import org.sfandroid.hooklib.interfaces.IHookCollect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@HookClass(clazz = Activity.class)
final class BaseActivityHook implements IHook, IActivity, IHookCollect<IActivity> {
    private static final String TAG = "ActivityListener ";
    private static boolean log = HookEntry.debug;
    private static BaseActivityHook singleton = null;
    private Activity top;
    private LinkedList<Activity> stack = new LinkedList<>();
    private boolean keepScreenOn = false;
    /**
     * 1 只使用 Application 注册生命周期方式回调,不在生命周期监听内的方法不能被Hook
     * 2 只使用 Hook 方式
     * 3 使用 注册 + Hook 方式
     * 注册生命周期回调限制在于无法干扰函数继续执行,回调时机都在{@link Activity}方法执行时
     * Hook方式限制在于Hook框架的稳定性
     */
    private int type = 0;
    /**
     * 这里允许多个类监听同一个方法,因为存在默认接口所有必须支持,回调顺序是按照添加的顺序
     */
    private Map<String, Set<IActivity>> callbacks = new HashMap<>();
    private Application.ActivityLifecycleCallbacks life = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    private BaseActivityHook() {
    }

    public static BaseActivityHook getInstance() {
        if (singleton == null) {
            synchronized (BaseActivityHook.class) {
                if (singleton == null) {
                    singleton = new BaseActivityHook();
                }
            }
        }
        return singleton;
    }

    public static void setLog(boolean z) {
        log = z;
    }

    public Activity getTop() {
        return top;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }

    public void register(Application app) {
        if (app != null) {
            type = type | 1;
            app.unregisterActivityLifecycleCallbacks(life);
            app.registerActivityLifecycleCallbacks(life);
        } else {
            Log.e(TAG, "Application is null");
        }
    }

    private Set<IActivity> getClassListener(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        Set<IActivity> set = callbacks.get(name);
        if (set == null) {
            set = new LinkedHashSet<>();
            callbacks.put(name, set);
        }
        return set;
    }

    @Override
    public void hookInit() {
        type = type | 2;
        clearNullListener();
    }

    @Override
    public void addListener(IActivity... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        for (IActivity al : listeners) {
            if (al == null) {
                continue;
            }
            String[] cs = al.activityNames();
            if (cs == null) {
                continue;
            }
            for (String s : cs) {
                Set<IActivity> set = getClassListener(s);
                if (set != null) {
                    set.add(al);
                }
            }
        }
    }

    @Override
    public void removeListener(IActivity... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        for (IActivity al : listeners) {
            if (al == null) {
                continue;
            }
            String[] cs = al.activityNames();
            if (cs == null) {
                continue;
            }
            for (String s : cs) {
                Set<IActivity> set = callbacks.get(s);
                if (set != null) {
                    set.remove(al);
                }
            }
        }
    }

    @Override
    public List<IActivity> getAll() {
        clearNullListener();
        List<IActivity> ret = new ArrayList<>(callbacks.size());
        for (Set<IActivity> set : callbacks.values()) {
            ret.addAll(set);
        }
        return ret;
    }

    @Override
    public boolean isEmpty() {
        clearNullListener();
        return callbacks.isEmpty();
    }

    private void clearNullListener() {
        Iterator<Map.Entry<String, Set<IActivity>>> itor = callbacks.entrySet().iterator();
        while (itor.hasNext()) {
            Map.Entry<String, Set<IActivity>> entry = itor.next();
            if (entry.getKey() == null || entry.getKey().length() == 0) {
                itor.remove();
                continue;
            }
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                itor.remove();
            }
        }
    }

    @Override
    public String[] activityNames() {
        return new String[0];
    }

    @Override
    public void onStartBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onStart() before");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onStartBefore(param);
            }
        }
    }

    @Override
    public void onStartAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onStart() after");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onStartAfter(param);
            }
        }
    }

    @Override
    public void onResumeBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        top = (Activity) param.thisObject;
        if (keepScreenOn) {
            top.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onResume() before");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onResumeBefore(param);
            }
        }
    }

    @Override
    public void onResumeAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onResume() after");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onResumeAfter(param);
            }
        }
    }

    @Override
    public void onPauseBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onPause() before");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onPauseBefore(param);
            }
        }
    }

    @Override
    public void onPauseAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onPause() after");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onPauseAfter(param);
            }
        }
    }

    @HookMethod(hook = @HookMethodConfigure(value = "onCreate"), param = @HookParameter(params = Bundle.class))
    @Override
    public void onCreateBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        stack.push((Activity) param.thisObject);
        if (log) {
            Log.d(TAG, name + " call onCreate() before");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onCreateBefore(param);
            }
        }
    }

    @HookMethod(hook = @HookMethodConfigure(value = "onCreate", before = false), param = @HookParameter(params = Bundle.class))
    @Override
    public void onCreateAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.i(TAG, name + " call onCreate() after");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onCreateAfter(param);
            }
        }
    }

    @Override
    public void finishBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.i(TAG, name + " call finish() before");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.finishBefore(param);
            }
        }
    }

    @Override
    public void finishAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.i(TAG, name + " call finish() after");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.finishAfter(param);
            }
        }
    }

    @Override
    public void onDestroyBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        stack.remove((Activity) param.thisObject);
        if (log) {
            Log.d(TAG, name + " call onDestroy() before");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onDestroyBefore(param);
            }
        }
    }

    @Override
    public void onDestroyAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onDestroy() after");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.onDestroyAfter(param);
            }
        }
    }

    @Override
    public void isTaskRootAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call isTaskRoot() after");
        }
        Set<IActivity> ls = callbacks.get(name);
        if (ls != null) {
            for (IActivity l : ls) {
                l.isTaskRootAfter(param);
            }
        }
    }

}
