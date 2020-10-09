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

import org.sfandroid.hooklib.interfaces.IActivity;
import org.sfandroid.hooklib.interfaces.IFragment;
import org.sfandroid.hooklib.interfaces.IHookError;
import org.sfandroid.hooklib.interfaces.IHookFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author beichen
 */
public final class HookEntry {
    public static final String DEFAULT_GROUP = "default";
    public static boolean debug = BuildConfig.DEBUG;
    private static HookEntry singleton = null;
    public static final String CONSTRUCTOR_NAME = "<init>";
    /**
     * 分组保存所有Hook项的映射表
     */
    private Map<String, List<IHook>> listeners = new HashMap<>();

    private HookEntry() {
    }

    public static HookEntry getInstance() {
        if (singleton == null) {
            synchronized (HookEntry.class) {
                if (singleton == null) {
                    singleton = new HookEntry();
                }
            }
        }
        return singleton;
    }

    /**
     * 注意这里返回原list,避免增删
     *
     * @return 返回默认分组所有Hook回调对象
     */
    public List<IHook> getHookGroup() {
        return getHookGroup(DEFAULT_GROUP);
    }

    /**
     * 注意这里返回原list,避免增删
     *
     * @param group 指定分组名称
     * @return 返回指定分组所有Hook回调对象
     */
    public List<IHook> getHookGroup(String group) {
        List<IHook> g = listeners.get(group);
        if (g == null) {
            g = new ArrayList<>();
            listeners.put(group, g);
        }
        return g;
    }

    /**
     * 这里只将Hook回调对象添加到映射表中,并未直接开启Hook,
     * 需要手动调用{@link #initHook}或{@link #initHookAll}才执行实际的Hook操作
     *
     * @param listeners Hook回调对象
     * @return 添加是否成功
     */
    public boolean addHook(IHook... listeners) {
        return addHook(DEFAULT_GROUP, listeners);
    }

    public boolean addHook(String group, IHook... listeners) {
        if (listeners == null || listeners.length == 0) {
            return false;
        }
        boolean ret = true;
        List<IHook> list = getHookGroup(group);
        for (IHook l : listeners) {
            ret = ret && list.add(l);
        }
        return ret;
    }

    /**
     * 这里只是从映射表中移除指定Hook回调对象,如果要取消Hook
     * 需手动调用{@link #closeHook}或{@link #closeHookAll()}
     *
     * @param listeners Hook回调对象
     * @return 移除是否成功
     */
    public boolean removeHook(IHook... listeners) {
        return removeHook(DEFAULT_GROUP, listeners);
    }

    /**
     * 见{@link #removeHook}说明
     */
    public boolean removeHook(String group, IHook... listeners) {
        if (listeners == null || listeners.length == 0) {
            return false;
        }
        List<IHook> list = getHookGroup(group);
        boolean ret = true;
        for (IHook l : listeners) {
            ret = ret && list.remove(l);
        }
        return ret;
    }

    public void initHook(IHook... listeners) {
        initHook(DEFAULT_GROUP, listeners);
    }

    public void initHook(String group, IHook... listeners) {
        if (listeners != null && listeners.length > 0) {
            List<IHook> list = getHookGroup(group);
            for (IHook listener : listeners) {
                if (list.contains(listener) && listener != null) {
                    listener.hookInit();
                    HookCore.addHook(listener);
                }
            }
        }
    }

    public void initHookAll() {
        for (List<IHook> list : listeners.values()) {
            for (IHook item : list) {
                item.hookInit();
                HookCore.addHook(item);
            }
        }
    }

    public void initHookAll(String group) {
        for (IHook item : getHookGroup(group)) {
            item.hookInit();
            HookCore.addHook(item);
        }
    }

    public void closeHook(IHook... listeners) {
        closeHook(DEFAULT_GROUP, listeners);
    }

    public void closeHook(String group, IHook... listeners) {
        if (listeners != null && listeners.length > 0) {
            List<IHook> list = getHookGroup(group);
            for (IHook listener : listeners) {
                if (list.contains(listener) && listener != null) {
                    listener.hookClear();
                    HookCore.removeHook(listener);
                }
            }
        }
    }

    public void closeHookAll() {
        for (List<IHook> list : listeners.values()) {
            for (IHook listener : list) {
                listener.hookClear();
                HookCore.removeHook(listener);
            }
        }
    }

    public void closeHookAll(String group) {
        for (IHook listener : getHookGroup(group)) {
            listener.hookClear();
            HookCore.removeHook(listener);
        }
    }

    public void clearHook(IHook... listeners) {
        clearHook(DEFAULT_GROUP, listeners);
    }

    public void clearHook(String group, IHook... listeners) {
        if (listeners != null && listeners.length > 0) {
            List<IHook> list = getHookGroup(group);
            for (IHook listener : listeners) {
                if (list.contains(listener) && listener != null) {
                    listener.hookClear();
                }
            }
        }
    }

    public void clearHookAll() {
        for (List<IHook> list : listeners.values()) {
            for (IHook listener : list) {
                listener.hookClear();
            }
        }
    }

    public void clearHookAll(String group) {
        for (IHook listener : getHookGroup(group)) {
            listener.hookClear();
        }
    }

    /**
     * 开启单个对象绑定的Hook,调用此函数之前请先调用
     * {@link #setHookProcess(String, boolean)},{@link #setHookVersion(long)}
     * {@link #initHookAll}或{@link #initHook}
     *
     * @param loader    类加载器
     * @param listeners 监听对象
     */
    public void openHook(ClassLoader loader, IHook... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        for (IHook listener : listeners) {
            HookCore.hookSpecialCallback(loader, listener);
        }
    }

    /**
     * 开启所有hook,调用此函数前请先调用{@link #initHookAll()}
     *
     * @param loader      类加载器
     * @param processName 当前进程名
     * @param mainProcess 当前是否为主进程
     * @param version     当前app版本
     */
    public void openAllHook(ClassLoader loader, String processName, boolean mainProcess, long version) {
        HookCore.hooks(loader, processName, mainProcess, version);
    }

    /**
     * 设置Hook或Hook回调执行过程中错误回调,传入null则是清理回调
     *
     * @param error 错误回调
     */
    public void setHookLogCallback(IHookError error) {
        HookCore.setLogHandler(error);
    }

    /**
     * 设置具体的Hook框架,在所有{@link #initHook}前调用
     *
     * @param frame Hook具体实现框架
     */
    public void setHookFrame(IHookFrame frame) {
        HookCore.setHookFrame(frame);
    }

    /**
     * 当单独Hook调用时需要先设置进程
     *
     * @param process     当前进程
     * @param mainProcess 是否是主进程
     */
    public void setHookProcess(String process, boolean mainProcess) {
        HookCore.setProcess(process, mainProcess);
    }

    public void setHookVersion(long version) {
        HookCore.setCurVersion(version);
    }

    /**
     * @return 不返回原对象, 返回新对象避免操作集合
     */
    public Map<String, List<IHook>> getHooksCopy() {
        Map<String, List<IHook>> ret = new HashMap<>(listeners.size());
        for (Map.Entry<String, List<IHook>> entry : listeners.entrySet()) {
            List<IHook> value = new ArrayList<>(entry.getValue());
            ret.put(entry.getKey(), value);
        }
        return ret;
    }

    public void openActivityListener() {
        addHook(BaseActivityHook.getInstance());
    }

    public void openActivityListener(Application app) {
        addHook(BaseActivityHook.getInstance());
        BaseActivityHook.getInstance().register(app);
    }

    public void addActivityListener(IActivity... listeners) {
        BaseActivityHook.getInstance().addListener(listeners);
    }

    public void removeActivityListener(IActivity... listeners) {
        BaseActivityHook.getInstance().removeListener(listeners);
    }

    public List<IActivity> getActivityListeners() {
        return BaseActivityHook.getInstance().getAll();
    }

    public boolean isEmptyActivityListener() {
        return BaseActivityHook.getInstance().isEmpty();
    }

    /**
     * 需要开启Activity监听,调用{@link #openActivityListener()}
     *
     * @param keep 保存屏幕长亮
     */
    public void setKeepScreenOn(boolean keep) {
        BaseActivityHook.getInstance().setKeepScreenOn(keep);
    }

    /**
     * 需要开启Activity监听,调用{@link #openActivityListener()}
     *
     * @return 当前顶部Activity, 未开启Activity监听则返回为null
     */
    public Activity getTopActivity() {
        return BaseActivityHook.getInstance().getTop();
    }


    public void openFragmentListener() {
        addHook(BaseFragmentHook.getInstance());
    }

    public void addFragmentListener(IFragment... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        BaseFragmentHook.getInstance().addListener(listeners);
    }

    public void removeFragmentListener(IFragment... listeners) {
        if (listeners == null || listeners.length == 0) {
            return;
        }
        BaseFragmentHook.getInstance().removeListener(listeners);
    }

    public List<IFragment> getFragmentListeners() {
        return BaseFragmentHook.getInstance().getAll();
    }

    public boolean isEmptyFragmentListener() {
        return BaseFragmentHook.getInstance().isEmpty();
    }
}
