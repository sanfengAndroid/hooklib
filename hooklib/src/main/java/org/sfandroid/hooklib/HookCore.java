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
import android.util.Pair;

import org.sfandroid.hooklib.annotation.HookClass;
import org.sfandroid.hooklib.annotation.HookEnable;
import org.sfandroid.hooklib.annotation.HookFieldConfigure;
import org.sfandroid.hooklib.annotation.HookImplicit;
import org.sfandroid.hooklib.annotation.HookMethod;
import org.sfandroid.hooklib.annotation.HookMethodConfigure;
import org.sfandroid.hooklib.annotation.HookMethods;
import org.sfandroid.hooklib.annotation.HookMultiple;
import org.sfandroid.hooklib.annotation.HookParameter;
import org.sfandroid.hooklib.annotation.HookProcess;
import org.sfandroid.hooklib.annotation.HookThis;
import org.sfandroid.hooklib.annotation.HookVersion;
import org.sfandroid.hooklib.dynamic.DHookClass;
import org.sfandroid.hooklib.dynamic.DHookMethod;
import org.sfandroid.hooklib.dynamic.DHookParameter;
import org.sfandroid.hooklib.dynamic.DHookProcess;
import org.sfandroid.hooklib.dynamic.DHookVersion;
import org.sfandroid.hooklib.enums.HookCompatibleType;
import org.sfandroid.hooklib.enums.HookProcessType;
import org.sfandroid.hooklib.interfaces.IDHook;
import org.sfandroid.hooklib.interfaces.IHookError;
import org.sfandroid.hooklib.interfaces.IHookFrame;
import org.sfandroid.hooklib.utils.ArrayUtil;
import org.sfandroid.hooklib.utils.ClassUtil;
import org.sfandroid.hooklib.utils.MethodFindUtil;
import org.sfandroid.hooklib.utils.ObjectUtil;
import org.sfandroid.hooklib.utils.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Hook是按照类的粒度来绑定对象,因此开启Hook是按照类的粒度来执行,但是回调方法区分了进程
 *
 * @author beichen
 */
final class HookCore {
    private static final String TAG = "HookCore";

    private static IHookFrame hook;
    private static boolean log = BuildConfig.DEBUG;
    private static IHookError errorHandler = new IHookError() {
        @Override
        public void error(String msg, Throwable e) {
            if (log) {
                Log.e(TAG, msg, e);
            }
        }

        @Override
        public void warn(String msg) {
            if (log) {
                Log.w(TAG, msg);
            }
        }

        @Override
        public void info(String msg) {
            if (log) {
                Log.i(TAG, msg);
            }
        }

        @Override
        public void error(String msg) {
            if (log) {
                Log.e(TAG, msg);
            }
        }
    };

    /**
     * 当前进程名,Hook时进程名过滤需要
     */
    private static String processName;
    /**
     * 当前进程是否是主进程,Hook过滤需要
     */
    private static Boolean mainProcess;

    /**
     * 当前版本号
     */
    private static Long curVersion;
    /**
     * 所有Hook类与对象集合,一个Hook类共用一个{@link HookCore}对象
     */
    private static Map<String, HookCore> hooks = new HashMap<>();

    /**
     * 静态配置需要解析的方法
     */
    private Map<IHook, List<WrapStaticMethod>> staticBinds = new HashMap<>();

    /**
     * 动态Hook配置绑定的对象和配置
     */
    private Map<IDHook, DHookClass> dynamicBinds = new HashMap<>();

    /**
     * 当前类所有要Hook的方法集合
     */
    private Map<Member, WrapCallback> items = new HashMap<>();

    /**
     * 当前Hook的类名,对于非可见类使用类名
     */
    private String className;
    /**
     * 当前Hook的类,对应可见类使用Class
     */
    private Class<?> that;
    private XC_MethodHook callbackMethodHook = new XC_MethodHook() {
        @Override
        public void beforeHookedMethod(MethodHookParam param) {
            WrapCallback wrapCallback = items.get(param.method);
            if (wrapCallback == null) {
                return;
            }
            if (wrapCallback.before != null && wrapCallback.bCall) {
                try {
                    switch (wrapCallback.beforeType) {
                        case ONLY_HOOK_WRAP:
                            wrapCallback.before.invoke(wrapCallback.beforeObj, wrapCallback.beforeHookWarp ? param : param.real);
                            break;
                        case THIS_AND_PARAM:
                            wrapCallback.before.invoke(wrapCallback.beforeObj, ArrayUtil.add(param.args, 0, param.thisObject));
                            break;
                        case HOOK_WRAP_THIS_AND_PARAM:
                            Object[] ps = new Object[param.args.length + 2];
                            ps[0] = wrapCallback.beforeHookWarp ? param : param.real;
                            ps[1] = param.thisObject;
                            System.arraycopy(param.args, 0, ps, 2, param.args.length);
                            wrapCallback.before.invoke(wrapCallback.beforeObj, ps);
                            break;
                        case ONLY_PARAM:
                            wrapCallback.before.invoke(wrapCallback.beforeObj, param.args);
                            break;
                        case HOOK_WRAP_AND_PARAM:
                            wrapCallback.before.invoke(wrapCallback.beforeObj, ArrayUtil.add(param.args, 0, wrapCallback.beforeHookWarp ? param : param.real));
                            break;
                        case ONLY_THIS:
                            wrapCallback.before.invoke(wrapCallback.beforeObj, param.thisObject);
                            break;
                        case HOOK_WRAP_AND_THIS:
                            wrapCallback.before.invoke(wrapCallback.beforeObj, wrapCallback.beforeHookWarp ? param : param.real, param.thisObject);
                            break;
                        default:
                            break;
                    }
                } catch (Throwable e) {
                    if (e instanceof InvocationTargetException) {
                        errorHandler.error("call before method orig: " + param.method + ", callback method: " + wrapCallback.before, ((InvocationTargetException) e).getTargetException());
                    } else {
                        errorHandler.error("call before method orig: " + param.method + ", callback method: " + wrapCallback.before, e);
                    }
                }
            }
        }

        @Override
        public void afterHookedMethod(MethodHookParam param) {
            WrapCallback wrapCallback = items.get(param.method);
            if (wrapCallback == null) {
                return;
            }
            if (wrapCallback.after != null && wrapCallback.aCall) {
                try {
                    switch (wrapCallback.afterType) {
                        case ONLY_HOOK_WRAP:
                            wrapCallback.after.invoke(wrapCallback.afterObj, wrapCallback.afterHookWarp ? param : param.real);
                            break;
                        case THIS_AND_PARAM:
                            wrapCallback.after.invoke(wrapCallback.afterObj, ArrayUtil.add(param.args, 0, param.thisObject));
                            break;
                        case HOOK_WRAP_THIS_AND_PARAM:
                            Object[] ps = new Object[param.args.length + 2];
                            ps[0] = wrapCallback.afterHookWarp ? param : param.real;
                            ps[1] = param.thisObject;
                            System.arraycopy(param.args, 0, ps, 2, param.args.length);
                            wrapCallback.after.invoke(wrapCallback.afterObj, ps);
                            break;
                        case ONLY_PARAM:
                            wrapCallback.after.invoke(wrapCallback.afterObj, param.args);
                            break;
                        case HOOK_WRAP_AND_PARAM:
                            wrapCallback.after.invoke(wrapCallback.afterObj, ArrayUtil.add(param.args, 0, wrapCallback.afterHookWarp ? param : param.real));
                            break;
                        case ONLY_THIS:
                            wrapCallback.after.invoke(wrapCallback.afterObj, param.thisObject);
                            break;
                        case HOOK_WRAP_AND_THIS:
                            wrapCallback.after.invoke(wrapCallback.afterObj, wrapCallback.afterHookWarp ? param : param.real, param.thisObject);
                            break;
                        default:
                            break;
                    }
                } catch (Throwable e) {
                    if (e instanceof InvocationTargetException) {
                        errorHandler.error("call after method orig: " + param.method + ", callback method: " + wrapCallback.after, ((InvocationTargetException) e).getTargetException());
                    } else {
                        errorHandler.error("call after method orig: " + param.method + ", callback method: " + wrapCallback.after, e);
                    }
                }
            }
        }
    };

    private HookCore(String name) {
        this.className = name;
    }


    private HookCore(Class<?> that) {
        this.that = that;
    }

    private static HookCore getInstance(String name) {
        if (hooks.get(name) == null) {
            synchronized (HookCore.class) {
                if (hooks.get(name) == null) {
                    hooks.put(name, new HookCore(name));
                }
            }
        }
        return hooks.get(name);
    }

    private static HookCore getInstance(Class<?> that) {
        if (hooks.get(that.getName()) == null) {
            synchronized (HookCore.class) {
                if (hooks.get(that.getName()) == null) {
                    hooks.put(that.getName(), new HookCore(that));
                }
            }
        }
        return hooks.get(that.getName());
    }

    /**
     * 静态注册,只能通过注解{@link HookClass}和{@link HookMethodConfigure}来配置
     *
     * @param listen 包含接口的回调对象
     */
    static void addHook(IHook listen) {
        if (listen == null) {
            return;
        }
        if (listen instanceof IDHook) {
            addDHook((IDHook) listen);
            return;
        }
        HookClass cClass = listen.getClass().getAnnotation(HookClass.class);
        String classConfName = cClass != null ? cClass.value() : StringUtil.EMPTY;
        Class<?> classConfClass = cClass != null ? !ObjectUtil.isNullClass(cClass.clazz()) ? cClass.clazz() : null : null;
        List<Method> methods = MethodFindUtil.getMethodsWithAnnotation(listen.getClass(), HookMethod.class, true);
        methods.addAll(MethodFindUtil.getMethodsWithAnnotation(listen.getClass(), HookMethods.class, true));
        for (Method method : methods) {
            HookMethod[] hms = method.getAnnotation(HookMethod.class) == null ? method.getAnnotation(HookMethods.class).value() : new HookMethod[]{method.getAnnotation(HookMethod.class)};
            for (HookMethod hm : hms) {
                // 方法上没有配置Hook类则从类上继承
                String name = StringUtil.isNoneEmpty(hm.className()) ? hm.className() : classConfName;
                Class<?> clazz = !ObjectUtil.isNullClass(hm.clazz()) ? hm.clazz() : classConfClass;
                boolean strEmpty = StringUtil.isEmpty(name);
                if (strEmpty && clazz == null) {
                    errorHandler.error("The(" + method + ") method annotation(" + ClassUtil.getName(HookMethod.class) +
                            ") does not have correct class name," +
                            "and the class(" + ClassUtil.getName(listen) + ") has no annotation(" + ClassUtil.getName(HookClass.class) +
                            ") or the correct class name is not configured.");
                    continue;
                }
                HookCore core = !strEmpty ? getInstance(name) : getInstance(clazz);
                // 获取该对象上所有绑定的静态配置方法
                List<WrapStaticMethod> list = core.staticBinds.get(listen);
                if (list == null) {
                    list = new ArrayList<>();
                    core.staticBinds.put(listen, list);
                }
                boolean find = false;
                for (WrapStaticMethod wrap : list) {
                    if (wrap.method == method) {
                        wrap.items.add(hm);
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    WrapStaticMethod wrap = new WrapStaticMethod();
                    wrap.method = method;
                    wrap.items.add(hm);
                    list.add(wrap);
                }
            }
        }
    }

    /**
     * 动态注册Hook,优先使用动态配置对象,当动态对象不存在时再查找静态配置
     *
     * @param listen 回调对象
     */
    private static void addDHook(IDHook listen) {
        DHookClass[] dc = listen.getHooks();
        if (ArrayUtil.isEmpty(dc)) {
            errorHandler.error(listen.getClass().getSimpleName() + " object dynamic configuration hook item cannot be empty");
            return;
        }
        HookClass ci = listen.getClass().getAnnotation(HookClass.class);
        if (ci != null) {
            // 修复动态配置为空时从类上获取注解
            for (DHookClass d : dc) {
                if (StringUtil.isEmpty(d.name) && ObjectUtil.isNullClass(d.type)) {
                    if (!StringUtil.isEmpty(ci.value())) {
                        d.name = ci.value();
                    }
                    if (!ObjectUtil.isNullClass(ci.clazz())) {
                        d.type = ci.clazz();
                    }
                }
            }
        }
        for (int i = 0; i < dc.length; i++) {
            boolean b1 = StringUtil.isEmpty(dc[i].name);
            boolean b2 = ObjectUtil.isNullClass(dc[i].type);
            if (b1 && b2) {
                errorHandler.error(listen.getClass().getSimpleName() + " object dynamic configuration items " + i + " contain empty classes");
                continue;
            }
            if (ArrayUtil.isEmpty(dc[i].methods)) {
                errorHandler.error(listen.getClass().getSimpleName() + " object dynamic configuration items " + i + " contain empty method");
                continue;
            }
            for (int j = 0; i < dc[i].methods.length; j++) {
                DHookMethod dm = dc[i].methods[j];
                if (dm.bindId == DHookMethod.UN_BIND) {
                    // 每个方法必须绑定一个id
                    errorHandler.error(listen.getClass().getSimpleName() + " object dynamic configuration items " + i + "hook method items " + j + " unbound id.");
                    return;
                }
            }
            HookCore core = b1 ? getInstance(dc[i].type) : getInstance(dc[i].name);
            core.dynamicBinds.put(listen, dc[i]);
        }
    }

    /**
     * 这里移除会将匹配的动态配置和静态配置对象有关方法都移除掉,所以不要在同一个类配置
     *
     * @param listen 移除对象
     */
    @SuppressWarnings("unused")
    static void removeDHook(IDHook listen) {
        removeHook(listen);
    }

    /**
     * 删除该回调对象中所有关联的回调方法
     * 注意这里只是从引用上删除,未调用Hook框架的取消Hook方法
     *
     * @param listen 回调对象
     */
    static void removeHook(IHook listen) {
        for (HookCore core : hooks.values()) {
            Iterator<Map.Entry<Member, WrapCallback>> iterator = core.items.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Member, WrapCallback> entry = iterator.next();
                WrapCallback callback = entry.getValue();
                if (callback.beforeObj == listen) {
                    callback.bCall = false;
                    callback.beforeObj = null;
                }
                if (callback.afterObj == listen) {
                    callback.aCall = false;
                    callback.afterObj = null;
                }
                if (!callback.bCall && !callback.aCall) {
                    iterator.remove();
                }
            }
        }
        clear();
    }

    /**
     * 开启所有已添加对象的Hook
     *
     * @param loader      类加载器
     * @param processName 进程名
     * @param mainProcess 是否是主进程
     */
    static void hooks(ClassLoader loader, String processName, boolean mainProcess, long version) {
        HookCore.processName = processName;
        HookCore.mainProcess = mainProcess;
        HookCore.curVersion = version;
        errorHandler.info("start hook process: " + processName + ", is main process: " + mainProcess);
        for (HookCore base : hooks.values()) {
            base.hook(loader, null);
        }
        clear();
    }

    /**
     * 按照代码中对象的粒度来开启Hook
     *
     * @param loader   类加载器
     * @param listener 回调承载对象
     */
    static void hookSpecialCallback(ClassLoader loader, IHook listener) {
        if (listener == null) {
            errorHandler.error("The callback object for opening the Hook is empty.", new Exception());
            return;
        }
        for (HookCore base : hooks.values()) {
            if (base.staticBinds.containsKey(listener) || base.dynamicBinds.containsKey(listener)) {
                base.hook(loader, listener);
            }
        }
    }

    static void setProcess(String name, boolean mainProcess) {
        HookCore.processName = name;
        HookCore.mainProcess = mainProcess;
    }

    static void setCurVersion(long version) {
        HookCore.curVersion = version;
    }

    static void setLogHandler(IHookError handler) {
        if (handler == null) {
            log = false;
            return;
        }
        errorHandler = handler;
    }

    static void setHookFrame(IHookFrame frame) {
        hook = frame;
    }

    private static DHookProcess findProcess(DHookProcess dynamicMethod, DHookProcess callbackMethod,
                                            DHookProcess dynamicClass, HookProcess callbackClass) {
        // 配置顺序优先级
        // 动态配置方法 > 静态配置方法 > 动态配置类 > 静态配置类
        DHookProcess[] processes = new DHookProcess[]{dynamicMethod, callbackMethod, dynamicClass, DHookProcess.toDProcess(callbackClass)};
        for (DHookProcess process : processes) {
            if (process != null && process.hookProcessType != HookProcessType.INHERIT) {
                return process;
            }
        }
        return DHookProcess.DEFAULT;
    }

    private static DHookVersion findVersion(DHookVersion dynamicMethod, DHookVersion callbackMethod,
                                            DHookVersion dynamicClass, HookVersion callbackClass) {
        // 配置顺序优先级
        // 动态配置方法 > 静态配置方法 > 动态配置类 > 静态配置类
        DHookVersion[] arr = new DHookVersion[]{dynamicMethod, callbackMethod, dynamicClass, DHookVersion.toDVersion(callbackClass)};
        for (DHookVersion version : arr) {
            if (version != null && !version.inherit()) {
                return version;
            }
        }
        return DHookVersion.DEFAULT;
    }

    private static DHookParameter findParameter(DHookParameter dynamicMethod, HookParameter callbackMethod) {
        // 方法签名优先级
        // 动态配置方法 > 静态配置方法
        if (dynamicMethod != null) {
            return dynamicMethod;
        }
        // 这里要考虑使用动态配置但是方法又有空的默认配置
        DHookParameter parameter = DHookParameter.toDParameter(callbackMethod);
        return parameter.isEmptyParameter() ? DHookParameter.DEFAULT : parameter;
    }

    private static Method findBindMethod(List<Method> methods, int id) {
        for (Method method : methods) {
            HookMethodConfigure conf = method.getAnnotation(HookMethodConfigure.class);
            if (ArrayUtil.contains(conf.bind(), id)) {
                return method;
            }
        }
        return null;
    }

    private static boolean atVersion(IHook obj, Method method, DHookVersion version) {
        // 没有配置版本则全部适用
        if (version == null || version == DHookVersion.DEFAULT) {
            return true;
        }
        if (!ArrayUtil.isEmpty(version.values)) {
            return ArrayUtil.contains(version.values, curVersion);
        }
        if (version.max < version.min) {
            throw new IllegalArgumentException("Wrong configuration version number on class(" + ClassUtil.getName(obj) + ") method(" + method + ")" +
                    ", max version " + version.max + " < min version " + version.min);
        }
        if (version.min < 0) {
            throw new IllegalArgumentException("Wrong configuration version number on class(" + ClassUtil.getName(obj) + ") method(" + method + "), min version " + version.min + " must more than 0.");
        }
        if (version.min == 0 && version.max == 0) {
            // 都为0,且指定版本号为null,则默认是全版本支持
            return true;
        }
        return curVersion >= version.min && curVersion <= version.max;
    }

    private static boolean isHookWrapClass(Class<?> type) {
        Class<?> realHookClass = hook.getParamClass();
        return realHookClass.isAssignableFrom(type) || XC_MethodHook.MethodHookParam.class.isAssignableFrom(type);
    }

    private static String toMethodString(Class returnType, String returnName, Class[] types, String[] params) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtil.isEmpty(returnName)) {
            sb.append(returnName);
        } else if (returnType != null) {
            sb.append(ClassUtil.isPrimitive(returnType) ? ClassUtil.getPrimitiveName(returnType) : returnType.getName());
        } else {
            sb.append("void");
        }
        sb.append(' ');
        if (!ArrayUtil.isEmpty(params)) {
            String s = ArrayUtil.toString(params);
            sb.append("(").append(s.substring(1, s.length() - 1)).append(")");
        } else if (!ArrayUtil.isEmpty(types)) {
            String s = ArrayUtil.toString(params);
            sb.append("(").append(s.substring(1, s.length() - 1)).append(")");
        } else {
            sb.append("()");
        }
        return sb.toString();
    }

    /**
     * @param hook      进程配置项
     * @param processes 配置的指定进程名数组,视情况可以为空
     * @return 当前进程匹配返回 {@code true},否者{@code false}
     */
    private static boolean atHookProcess(HookProcessType hook, String[] processes) {
        if (hook == null) {
            return false;
        }
        boolean ret = false;
        switch (hook) {
            case MAIN:
                if (mainProcess) {
                    ret = true;
                }
                break;
            case SPECIAL_AND_MAIN:
                if (mainProcess) {
                    ret = true;
                    break;
                }
            case SPECIAL:
                if (ArrayUtil.isEmpty(processes)) {
                    break;
                }
                if (ArrayUtil.contains(processes, processName)) {
                    ret = true;
                }
                break;
            case NON_MAIN:
                if (!mainProcess) {
                    ret = true;
                }
                break;
            case ALL:
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * 清理存在空的Hook项
     */
    private static void clear() {
        Iterator<Map.Entry<String, HookCore>> iterator = hooks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HookCore> entry = iterator.next();
            HookCore core = entry.getValue();
            if (core.items.isEmpty() && core.dynamicBinds.isEmpty() && core.staticBinds.isEmpty()) {
                iterator.remove();
            }
        }
    }

    private void init(ClassLoader loader) throws ClassNotFoundException {
        if (that == null) {
            that = ClassUtil.getClass(loader, className, false);
        }
        for (Map.Entry<IHook, List<WrapStaticMethod>> entry : staticBinds.entrySet()) {
            IHook obj = entry.getKey();
            List<WrapStaticMethod> methods = entry.getValue();
            for (WrapStaticMethod med : methods) {
                staticResolve(obj, med, loader);
            }
        }
        for (Map.Entry<IDHook, DHookClass> entry : dynamicBinds.entrySet()) {
            dynamicResolve(entry.getKey(), entry.getValue(), loader);
        }
    }

    private void staticResolve(IHook obj, WrapStaticMethod item, ClassLoader loader) {
        boolean mul = item.method.getAnnotation(HookMultiple.class) != null;
        HookProcess cProcess = obj.getClass().getAnnotation(HookProcess.class);
        HookVersion cVersion = obj.getClass().getAnnotation(HookVersion.class);
        boolean mImplicit = item.method.getAnnotation(HookImplicit.class) != null && item.method.getAnnotation(HookImplicit.class).value();
        for (HookMethod hm : item.items) {
            // 第一步判断Hook条件是否成立
            if (!hm.enable() || !findEnable(obj, item.method)) {
                continue;
            }
            resolve(obj, loader, item.method, hm.value(), hm.hook(), DHookParameter.toDParameter(hm.param()),
                    findProcess(null, DHookProcess.create(hm.processType(), hm.processes()), null, cProcess),
                    findVersion(null, DHookVersion.create(hm.versions(), hm.min(), hm.max()), null, cVersion),
                    mImplicit || hm.implicit());
            if (!mul) {
                return;
            }
        }
    }

    private void dynamicResolve(IDHook obj, DHookClass conf, ClassLoader loader) {
        HookProcess cProcess = obj.getClass().getAnnotation(HookProcess.class);
        HookVersion cVersion = obj.getClass().getAnnotation(HookVersion.class);
        List<Method> callbacks = MethodFindUtil.getMethodsWithAnnotation(obj.getClass(), HookMethodConfigure.class, true);
        // 动态解析不能存在隐式推断
        for (DHookMethod dm : conf.methods) {
            // 第一步查找绑定的方法
            Method callback = findBindMethod(callbacks, dm.bindId);
            if (callback == null) {
                continue;
            }
            if (!findEnable(obj, callback)) {
                continue;
            }
            boolean implicit = callback.getAnnotation(HookImplicit.class) != null && callback.getAnnotation(HookImplicit.class).value();
            resolve(obj, loader, callback, null, callback.getAnnotation(HookMethodConfigure.class),
                    findParameter(dm.param, callback.getAnnotation(HookParameter.class)),
                    findProcess(dm.process, DHookProcess.toDProcess(callback.getAnnotation(HookProcess.class)),
                            conf.process, cProcess),
                    findVersion(dm.version, DHookVersion.toDVersion(callback.getAnnotation(HookVersion.class)),
                            conf.version, cVersion),
                    implicit);
        }
    }

    private boolean findEnable(IHook obj, Method callback) {
        if (callback != null && callback.getAnnotation(HookEnable.class) != null) {
            return callback.getAnnotation(HookEnable.class).value();
        }
        // 方法上没有则从类上获取
        if (obj != null) {
            HookEnable enable = obj.getClass().getAnnotation(HookEnable.class);
            if (enable != null) {
                return enable.value();
            }
        }
        // 都没有则默认是开启的
        return true;
    }

    private void resolve(IHook obj, ClassLoader loader, Method callback, String name, HookMethodConfigure methodConf,
                         DHookParameter parameter, DHookProcess process, DHookVersion version, boolean implicit) {
        // 第一步判断进程名和版本
        if (process != null && !atHookProcess(process.hookProcessType, process.processes)) {
            return;
        }
        if (!atVersion(obj, callback, version)) {
            return;
        }
        // 第二步确定方法名
        Pair<String, Integer> pair = getMethodName(name, methodConf, callback);
        name = pair.first;
        int type = pair.second;
        boolean isConstructor = StringUtil.equals(name, HookEntry.CONSTRUCTOR_NAME);
        // 方法查找只能在方法名为空或者构造函数时才能开启
        if ((!StringUtil.isEmpty(name) && !isConstructor) && (methodConf.find() || methodConf.findAll())) {
            errorHandler.error("Current class(" + ClassUtil.getName(obj) + ")  callback method(" + callback + ") configuration error," +
                    "the method search can only be turned on when the method name is empty or the method name is '<init>'");
            return;
        }
        // 第三步确定方法签名
        Class<?>[] pTypes;
        Class<?>[] eTypes;
        Class<?> rType;
        HookFieldConfigure[] guessConfigures = null;
        HookCallbackType guessCallbackType = null;
        try {
            rType = ClassUtil.getClass(loader, parameter.rName, parameter.rType, false);
            if (implicit) {
                Object[] os = guessMethodType(callback, loader);
                pTypes = (Class<?>[]) os[0];
                guessConfigures = (HookFieldConfigure[]) os[1];
                guessCallbackType = (HookCallbackType) os[2];
            } else {
                pTypes = ClassUtil.getClass(loader, parameter.pNames, parameter.pTypes, false);
            }
            eTypes = ClassUtil.getClass(loader, parameter.tNames, parameter.tTypes, false);
        } catch (ClassNotFoundException e) {
            errorHandler.error(that.getName() + " class find method error", e);
            return;
        }
        Member[] matchMembers;
        try {
            matchMembers = matchMethod(name, methodConf, rType, pTypes, guessConfigures, eTypes,
                    parameter.autoBox, parameter.compatible);
        } catch (IllegalArgumentException e) {
            errorHandler.error(e.getMessage());
            return;
        }
        boolean before = type == 0 || (type == -1 && methodConf.before());
        wrapHook(matchMembers, callback, obj, before, guessCallbackType);
    }

    private Pair<String, Integer> getMethodName(String name, HookMethodConfigure hook, Method method) {
        String result = StringUtil.isEmpty(hook.value()) ? name : hook.value();
        int type = -1;
        if (StringUtil.isEmpty(result) && !hook.find() && !hook.findAll()) {
            // 根据方法名来推断
            result = method.getName();
            type = 0;
            if (result.endsWith("Before")) {
                result = result.substring(0, result.length() - 6);
            } else if (result.endsWith("After")) {
                result = result.substring(0, result.length() - 5);
                type = 1;
            }
        }
        return new Pair<>(result, type);
    }

    private Member[] matchMethod(String name, HookMethodConfigure hook, Class<?> rType, Class<?>[] pTypes, HookFieldConfigure[] pFields,
                                 Class<?>[] eTypes, boolean autoBoxing, HookCompatibleType compatible) throws IllegalArgumentException {
        List<Member> matchMembers = new ArrayList<>();
        boolean isConstructor = StringUtil.equals(name, HookEntry.CONSTRUCTOR_NAME);
        if (hook.find()) {
            Member find;
            find = isConstructor ? MethodFindUtil.findConstructor(that, pTypes, pFields, autoBoxing, compatible) :
                    MethodFindUtil.findMethodByParameterReturnTypeAndException(that, rType, pTypes, pFields, eTypes, hook.findSuper(), autoBoxing, compatible);
            if (find == null) {
                throw new IllegalArgumentException(that.getName() + "find: class not found special function sign");
            }
            matchMembers.add(find);
        } else if (hook.findAll()) {
            Member[] finds;
            finds = isConstructor ? MethodFindUtil.findConstructors(that, pTypes, pFields, autoBoxing, compatible) :
                    MethodFindUtil.findMethodsByParameterReturnTypeAndException(that, rType, pTypes, pFields, eTypes, hook.findSuper(), autoBoxing, compatible);
            if (ArrayUtil.isEmpty(finds)) {
                throw new IllegalArgumentException(that.getName() + "findAll: class not found " +
                        "special functions sign");
            }
            matchMembers.addAll(Arrays.asList(finds));
        } else if (hook.all()) {
            Member[] finds = isConstructor ? MethodFindUtil.findAllConstructor(that) : MethodFindUtil.findAllMethod(that, name, hook.findSuper());
            if (ArrayUtil.isEmpty(finds)) {
                throw new IllegalArgumentException(that.getName() + "all: class not found special method name: " + name);
            }
            matchMembers.addAll(Arrays.asList(finds));
        } else {
            Member find = isConstructor ? MethodFindUtil.findConstructor(that, pTypes, pFields, autoBoxing, compatible) :
                    MethodFindUtil.findMethod(that, name, rType, pTypes, pFields, eTypes, hook.findSuper(), autoBoxing, compatible);
            if (find == null) {
                throw new IllegalArgumentException("special: class(" + that + ")  not found special method: " + name +
                        " construction: " + isConstructor +
                        " parameter type: " + ArrayUtil.toString(pTypes));
            }
            matchMembers.add(find);
        }
        return matchMembers.toArray(new Member[0]);
    }

    private void wrapHook(Member[] hookMembers, Method callbackMethod, IHook obj, boolean before, HookCallbackType guessType) {
        for (Member member : hookMembers) {
            // 在解析参数时也应该确认回调方式
            WrapCallback callback = items.get(member);
            if (callback == null) {
                callback = new WrapCallback();
            }
            if ((before && callback.before != null) || (!before && callback.after != null)) {
                errorHandler.error("Duplicate callbacks are not allowed, hook method: " + member +
                        ",callback 1: " + (before ? callback.before : callback.after) +
                        ",callback 2: " + callbackMethod);
                return;
            }
            if (before) {
                try {
                    callback.beforeType = guessType == null ? resolveMethodType(callbackMethod, member) : guessType;
                } catch (IllegalArgumentException e) {
                    errorHandler.error("callback method is inconsistent with hook method parameters", e);
                    continue;
                }
                callback.before = callbackMethod;
                callback.beforeObj = obj;
                callback.bCall = true;
                switch (callback.beforeType) {
                    case ONLY_HOOK_WRAP:
                    case HOOK_WRAP_AND_PARAM:
                    case HOOK_WRAP_AND_THIS:
                    case HOOK_WRAP_THIS_AND_PARAM:
                        // 要具体解析Hook回调对象是我们的包装对象还是具体的框架实现对象
                        callback.beforeHookWarp = XC_MethodHook.MethodHookParam.class.isAssignableFrom(callback.before.getParameterTypes()[0]);
                        break;
                    default:
                        break;
                }
            } else {
                try {
                    callback.afterType = guessType == null ? resolveMethodType(callbackMethod, member) : guessType;
                } catch (IllegalArgumentException e) {
                    errorHandler.error("callback method is inconsistent with hook method parameters", e);
                    continue;
                }
                callback.after = callbackMethod;
                callback.afterObj = obj;
                callback.aCall = true;
                switch (callback.afterType) {
                    case ONLY_HOOK_WRAP:
                    case HOOK_WRAP_AND_PARAM:
                    case HOOK_WRAP_AND_THIS:
                    case HOOK_WRAP_THIS_AND_PARAM:
                        // 要具体解析Hook回调对象是我们的包装对象还是具体的框架实现对象
                        callback.afterHookWarp = XC_MethodHook.MethodHookParam.class.isAssignableFrom(callback.after.getParameterTypes()[0]);
                        break;
                    default:
                        break;
                }
            }
            items.put(member, callback);
        }
    }

    /**
     * 这里只匹配类型但实际不验证,直到hook触发时抛出错误
     *
     * @param method 回调方法
     * @param member hook方法
     * @return 回调类型
     */
    private HookCallbackType resolveMethodType(Method method, Member member) throws IllegalArgumentException {
        Class<?>[] callbackParamsType = method.getParameterTypes();
        // 当没有参数时则只能是包含参数
        if (callbackParamsType.length == 0) {
            return HookCallbackType.ONLY_PARAM;
        }
        Class<?>[] hookParamsType = member instanceof Method ? ((Method) member).getParameterTypes() : ((Constructor<?>) member).getParameterTypes();
        int delta = callbackParamsType.length - hookParamsType.length;
        if (delta > 2) {
            throw new IllegalArgumentException("param length not match, expect max length: " + hookParamsType.length + " +2 but actual length: " + callbackParamsType.length +
                    " hook callback method: " + method + ", hooked method: " + member);
        }
        Annotation[][] pas = method.getParameterAnnotations();
        int paramIndex = 0;
        int type = 0;
        for (int index = 0; index < callbackParamsType.length && index < 2; index++) {
            if (isHookWrapClass(callbackParamsType[index])) {
                type |= 1;
                if (index != 0) {
                    throw new IllegalArgumentException("Hook callback(MethodHookParam) must be the first parameter: " + method);
                }
                paramIndex++;
                continue;
            }
            // 可能没有HThis注解,接下来需要推导this
            HookThis mThis = getSpecialAnnotation(pas[index], HookThis.class);
            if (mThis != null) {
                if (index == 1 && (type & 1) == 0) {
                    throw new IllegalArgumentException("Hook callback thisObject must be the first or second parameter: " + method + ", parameter index: " + index);
                }
                type |= 2;
                paramIndex++;
            }
        }
        int callbackParamsLen = callbackParamsType.length - paramIndex;
        // 如果没有找到this对象则要分析第一个实际参数是否可能是this
        // 这里隐式this的条件是不能与实际参数类型匹配
        if ((type & 2) == 0 && callbackParamsLen > 0) {
            if (callbackParamsLen > hookParamsType.length) {
                type |= 2;
                callbackParamsLen--;
            } else {
                HookFieldConfigure fieldConf = getSpecialAnnotation(pas[paramIndex], HookFieldConfigure.class);
                // 隐式this无需配置HField,否者被认为是参数
                if (fieldConf == null) {
                    Class<?> firstCallbackType = callbackParamsType[paramIndex];
                    Class<?> firstHookType = hookParamsType[0];
                    if (firstCallbackType != firstHookType) {
                        type |= 2;
                        callbackParamsLen--;
                    }
                }
            }
        }
        delta = callbackParamsLen - hookParamsType.length;
        // 这里要么包含参数(参数数量必须匹配),要么不包含参数(完全没有参数)
        if (delta > 0 || (delta < 0 && callbackParamsLen > 0)) {
            throw new IllegalArgumentException("hook callback method: " + method + ", hooked method: " + member + " parameters miss match.");
        }
        if ((type & 2) != 0 && Modifier.isStatic(member.getModifiers())) {
            throw new IllegalArgumentException("hooked method: " + member + " is static function, the callback method cannot contain this object: " + method);
        }
        HookCallbackType callbackType;
        switch (type) {
            case 1:
                callbackType = delta == 0 ? HookCallbackType.HOOK_WRAP_AND_PARAM : HookCallbackType.ONLY_HOOK_WRAP;
                break;
            case 2:
                callbackType = delta == 0 ? HookCallbackType.THIS_AND_PARAM : HookCallbackType.ONLY_THIS;
                break;
            case 3:
                callbackType = delta == 0 ? HookCallbackType.HOOK_WRAP_THIS_AND_PARAM : HookCallbackType.HOOK_WRAP_AND_THIS;
                break;
            default:
                callbackType = HookCallbackType.ONLY_PARAM;
                break;
        }
        return callbackType;
    }

    /**
     * @param method Hook回调配置的方法
     * @param loader 类加载器
     * @return 推断的类型
     * @throws ClassNotFoundException 类未找到时抛出
     */
    private Object[] guessMethodType(Method method, ClassLoader loader) throws ClassNotFoundException, IllegalArgumentException {
        Class<?>[] callbackParamsType = method.getParameterTypes();
        // 没有参数则认为只包含参数
        if (callbackParamsType.length == 0) {
            return new Object[]{ArrayUtil.EMPTY_CLASS_ARRAY, new HookFieldConfigure[0], HookCallbackType.ONLY_PARAM};
        }
        List<Class<?>> hookParamsType = new ArrayList<>();
        List<HookFieldConfigure> hookParamsConf = new ArrayList<>();
        Annotation[][] pas = method.getParameterAnnotations();
        int type = 0;
        for (int i = 0; i < callbackParamsType.length; i++) {
            if (isHookWrapClass(callbackParamsType[i])) {
                type |= 1;
                if (i != 0) {
                    throw new IllegalArgumentException("Hook callback(MethodHookParam) must be the first parameter: " + method);
                }
                continue;
            }
            HookThis mThis = getSpecialAnnotation(pas[i], HookThis.class);
            if (mThis != null) {
                if (i == 1 && (type & 1) == 0 || i > 1) {
                    throw new IllegalArgumentException("Hook callback thisObject must be the first or second parameter: " + method + ", parameter index: " + i);
                }
                type |= 2;
                continue;
            }
            HookFieldConfigure mField = getSpecialAnnotation(pas[i], HookFieldConfigure.class);
            Class<?> paramType = mField == null || StringUtil.isEmpty(mField.value()) ?
                    callbackParamsType[i] : ClassUtil.getClass(loader, mField.value(), false);
            hookParamsType.add(paramType);
            hookParamsConf.add(mField);
        }
        HookCallbackType callbackType;
        switch (type) {
            case 1:
                callbackType = hookParamsType.isEmpty() ? HookCallbackType.ONLY_HOOK_WRAP : HookCallbackType.HOOK_WRAP_AND_PARAM;
                break;
            case 2:
                callbackType = hookParamsType.isEmpty() ? HookCallbackType.ONLY_THIS : HookCallbackType.THIS_AND_PARAM;
                break;
            case 3:
                callbackType = hookParamsType.isEmpty() ? HookCallbackType.HOOK_WRAP_AND_THIS : HookCallbackType.HOOK_WRAP_THIS_AND_PARAM;
                break;
            default:
                callbackType = HookCallbackType.ONLY_PARAM;
                break;
        }
        return new Object[]{hookParamsType.toArray(new Class<?>[0]), hookParamsConf.toArray(new HookFieldConfigure[0]), callbackType};
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T getSpecialAnnotation(Annotation[] as, Class<T> cls) {
        if (as == null) {
            return null;
        }
        for (Annotation anno : as) {
            // 通常注解的具体实现是动态代理,所以直接匹配类型相等
            if (cls.isAssignableFrom(anno.getClass())) {
                return (T) anno;
            }
        }
        return null;
    }

    /**
     * Hook 单个类时可以由外部调用,但是在调用前请先设置进程名和主进程才能过滤生效
     *
     * @param loader 类加载器
     */
    @SuppressWarnings("unchecked")
    private void hook(ClassLoader loader, IHook listener) {
        try {
            if (hook == null) {
                throw new RuntimeException("The " + IHookFrame.class.getSimpleName() +
                        " implements class is null, please invoke " + HookEntry.class +
                        ".setHookFrame() method.");
            }
            if (mainProcess == null || processName == null) {
                throw new RuntimeException("Must init mainProcess and processName, please invoke " +
                        HookEntry.class +
                        ".setHookProcess() method.");
            }
            if (curVersion == null) {
                throw new RuntimeException("Must init hook app version, please invoke " +
                        HookEntry.class +
                        ".setHookVersion() method.");
            }
            init(loader);
            for (Map.Entry<Member, WrapCallback> entry : items.entrySet()) {
                Member item = entry.getKey();
                if (listener != null) {
                    WrapCallback wrapCallback = entry.getValue();
                    // 这里开启指定回调对象的Hook可能会引用Hook相同方法,但是不同回调对象也被开启,
                    // 需要开发者自己避免这种情况
                    if (!listener.equals(wrapCallback.beforeObj) && !listener.equals(wrapCallback.afterObj)) {
                        continue;
                    }
                }
                if (callbackMethodHook.real == null) {
                    callbackMethodHook.real = hook.newCallback(callbackMethodHook);
                }
                errorHandler.info("hook class: " + item.getDeclaringClass() + ", method: " + item + ", result: " + hook.hookMethod(item, callbackMethodHook));
            }
            // 清理掉已经解析了的对象
            staticBinds.clear();
            dynamicBinds.clear();
        } catch (Throwable e) {
            if (errorHandler != null) {
                errorHandler.error("hook class " + (that == null ? className : that.getName()) + " error", e);
            }
        }
    }

    /**
     * Hook回调方式分为两类
     * 1. 包含Hook回调参数如{@code de.robv.android.xposed.XC_MethodHook$MethodHookParam}类型,这种可以调用类似setResult()方法,
     * 这种方式也分多种情况,但第一个参数肯定是固定的 Hook 回调对象, 有且只有一个Hook回调函数
     * Hook回调 + thisObject(静态方法则不需要) + 参数
     * 2. 不包含回调参数,只包含函数的真实参数,无法调用类似的setResult()方法, thisObject(静态方法则不需要) + 参数
     * 3. 这里参数有区分原始参数跟包装参数
     */
    private enum HookCallbackType {
        /**
         * 只包含Hook回调参数
         */
        ONLY_HOOK_WRAP,

        /**
         * 无论静态与非静态方法都只包含参数
         */
        ONLY_PARAM,

        ONLY_THIS,

        HOOK_WRAP_AND_PARAM,

        HOOK_WRAP_AND_THIS,

        THIS_AND_PARAM,

        HOOK_WRAP_THIS_AND_PARAM,
    }

    private static class WrapCallback {
        /**
         * before回调
         */
        private Method before;
        /**
         * before回调是否生效
         */
        private boolean bCall;
        /**
         * 回调对象,无论是否静态方法都有实例对象
         */
        private Object beforeObj;
        private HookCallbackType beforeType;
        /**
         * Hook回调对象又分我们包装的{@link XC_MethodHook.MethodHookParam},和具体框架的回调对象
         * {@link IHookFrame#getParamClass()}
         */
        private boolean beforeHookWarp;
        private Method after;
        private boolean aCall;
        private Object afterObj;
        private HookCallbackType afterType;
        private boolean afterHookWarp;
    }

    private static class WrapStaticMethod {
        private Method method;
        // 一个方法可以绑定多个Hook项,即多个Hook方法可以共用一个回调
        private List<HookMethod> items = new ArrayList<>();
    }
}
