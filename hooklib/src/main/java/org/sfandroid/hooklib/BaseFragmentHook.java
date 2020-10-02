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

import android.util.Log;

import org.sfandroid.hooklib.annotation.HookClass;
import org.sfandroid.hooklib.interfaces.IFragment;
import org.sfandroid.hooklib.interfaces.IHookCollect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author beichen
 * @date 2019/09/20
 */
@HookClass("android.support.v4.app.Fragment")
final class BaseFragmentHook implements IHook, IFragment, IHookCollect<IFragment> {
    private static final String TAG = "FragmentListener ";
    private static BaseFragmentHook singleton = null;
    private static boolean log = HookEntry.debug;
    /**
     * 这里允许多个类监听同一个方法,因为存在默认接口所有必须支持,回调顺序是按照添加的顺序
     */
    private Map<String, Set<IFragment>> callbacks = new HashMap<>();

    private BaseFragmentHook() {
    }

    public static BaseFragmentHook getInstance() {
        if (singleton == null) {
            synchronized (BaseFragmentHook.class) {
                if (singleton == null) {
                    singleton = new BaseFragmentHook();
                }
            }
        }
        return singleton;
    }

    public static void setLog(boolean z) {
        log = z;
    }

    private Set<IFragment> getClassListener(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        Set<IFragment> set = callbacks.get(name);
        if (set == null) {
            set = new LinkedHashSet<>();
            callbacks.put(name, set);
        }
        return set;
    }

    private void clearNullListener() {
        Iterator<Map.Entry<String, Set<IFragment>>> itor = callbacks.entrySet().iterator();
        while (itor.hasNext()) {
            Map.Entry<String, Set<IFragment>> entry = itor.next();
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
    public String[] fragmentNames() {
        return new String[0];
    }

    @Override
    public void onAttachBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onAttach() before");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onAttachBefore(param);
            }
        }
    }

    @Override
    public void onAttachAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onAttach() after");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onAttachAfter(param);
            }
        }
    }

    @Override
    public void onCreateViewBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onCreateView() before");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onCreateViewBefore(param);
            }
        }
    }

    @Override
    public void onCreateViewAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onCreateView() after");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onCreateViewAfter(param);
            }
        }
    }

    @Override
    public void onActivityCreatedBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onActivityCreated() before");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onActivityCreatedBefore(param);
            }
        }
    }

    @Override
    public void onActivityCreatedAfter(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onActivityCreated() after");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onActivityCreatedAfter(param);
            }
        }
    }

    @Override
    public void onResumeBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onResume() before");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
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
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onResumeAfter(param);
            }
        }
    }

    @Override
    public void onDestroyBefore(XC_MethodHook.MethodHookParam param) throws Throwable {
        String name = param.thisObject.getClass().getName();
        if (log) {
            Log.d(TAG, name + " call onDestroy() before");
        }
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
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
        Set<IFragment> ls = callbacks.get(name);
        if (ls != null) {
            for (IFragment l : ls) {
                l.onDestroyAfter(param);
            }
        }
    }

    @Override
    public void addListener(IFragment... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        for (IFragment al : listeners) {
            if (al == null) {
                continue;
            }
            String[] cs = al.fragmentNames();
            if (cs == null) {
                continue;
            }
            for (String s : cs) {
                Set<IFragment> set = getClassListener(s);
                if (set != null) {
                    set.add(al);
                }
            }
        }
    }

    @Override
    public void removeListener(IFragment... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        for (IFragment al : listeners) {
            if (al == null) {
                continue;
            }
            String[] cs = al.fragmentNames();
            if (cs == null) {
                continue;
            }
            for (String s : cs) {
                Set<IFragment> set = callbacks.get(s);
                if (set != null) {
                    set.remove(al);
                }
            }
        }
        clearNullListener();
    }

    @Override
    public List<IFragment> getAll() {
        clearNullListener();
        List<IFragment> ret = new ArrayList<>(callbacks.size());
        for (Set<IFragment> set : callbacks.values()) {
            ret.addAll(set);
        }
        return ret;
    }

    @Override
    public boolean isEmpty() {
        clearNullListener();
        return callbacks.isEmpty();
    }
}
