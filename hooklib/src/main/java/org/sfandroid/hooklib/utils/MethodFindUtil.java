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

package org.sfandroid.hooklib.utils;

import org.sfandroid.hooklib.annotation.HookFieldConfigure;
import org.sfandroid.hooklib.enums.HookCompatibleType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author beichen
 * @date 2019/10/08
 */
public class MethodFindUtil {

    public static Method findMethodByReturnType(Class<?> cls, Class<?> returnType, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, returnType, null, null, findSuper, autoBox, compatible, FindType.RETURN.getValue());
    }

    public static Method[] findMethodsByReturnType(Class<?> cls, Class<?> returnType, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethods(cls, returnType, null, null, findSuper, autoBox, compatible, FindType.RETURN.getValue());
    }

    public static Method findMethodByExceptionType(Class<?> cls, Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, null, null, exceptionTypes, findSuper, autoBox, compatible, FindType.EXCEPTION.getValue());
    }

    public static Method[] findMethodsByExceptionType(Class<?> cls, Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethods(cls, null, null, exceptionTypes, findSuper, autoBox, compatible, FindType.EXCEPTION.getValue());
    }

    /**
     * @param cls            方法所在类
     * @param parameterTypes 参数的具体类型数组,可以为空
     * @return 参数匹配的方法
     */
    public static Method findMethodByParameter(Class<?> cls, Class<?>[] parameterTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, null, parameterTypes, null, findSuper, autoBox, compatible, FindType.PARAMS.getValue());
    }

    public static Method[] findMethodsByParameter(Class<?> cls, Class<?>[] parameterTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethods(cls, null, parameterTypes, null, findSuper, autoBox, compatible, FindType.PARAMS.getValue());
    }

    public static Method findMethodByParameterAndReturnType(Class<?> cls, Class<?> returnType,
                                                            Class<?>[] parameterTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, returnType, parameterTypes, null, findSuper, autoBox, compatible, FindType.RETURN.getValue() | FindType.PARAMS.getValue());
    }

    public static Method[] findMethodsByParameterAndReturnType(Class<?> cls, Class<?> returnType,
                                                               Class<?>[] parameterTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethods(cls, returnType, parameterTypes, null, findSuper, autoBox, compatible, FindType.RETURN.getValue() | FindType.PARAMS.getValue());
    }

    public static Method findMethodByParameterReturnTypeAndException(Class<?> cls, Class<?> returnType,
                                                                     Class<?>[] parameterTypes, Class<?>[] exceptionTypes,
                                                                     final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, returnType, parameterTypes, exceptionTypes, findSuper, autoBox, compatible, FindType.RETURN.getValue() | FindType.PARAMS.getValue() | FindType.EXCEPTION.getValue());
    }

    public static Method findMethodByParameterReturnTypeAndException(Class<?> cls, Class<?> returnType,
                                                                     Class<?>[] parameterTypes, HookFieldConfigure[] phs, Class<?>[] exceptionTypes,
                                                                     final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, returnType, parameterTypes, phs, exceptionTypes, findSuper, autoBox, compatible, FindType.RETURN.getValue() | FindType.PARAMS.getValue() | FindType.EXCEPTION.getValue());
    }

    public static Method[] findMethodsByParameterReturnTypeAndException(Class<?> cls, Class<?> returnType,
                                                                        Class<?>[] parameterTypes, Class<?>[] exceptionTypes,
                                                                        final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethods(cls, returnType, parameterTypes, exceptionTypes, findSuper, autoBox, compatible, FindType.RETURN.getValue() | FindType.PARAMS.getValue() | FindType.EXCEPTION.getValue());
    }

    public static Method[] findMethodsByParameterReturnTypeAndException(Class<?> cls, Class<?> returnType,
                                                                        Class<?>[] parameterTypes, HookFieldConfigure[] phs, Class<?>[] exceptionTypes,
                                                                        final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethods(cls, returnType, parameterTypes, phs, exceptionTypes, findSuper, autoBox, compatible, FindType.RETURN.getValue() | FindType.PARAMS.getValue() | FindType.EXCEPTION.getValue());
    }

    /**
     * @param cls            查找类
     * @param returnType     方法返回值类型,为null则忽略查找
     * @param parameterTypes 方法签名的类型,为null则忽略查找
     * @param exceptionTypes 方法的异常类型,为null则忽略查找
     * @param findSuper      是否查找超类
     * @param autoBox        是否转换基本类型与包装类型
     * @param compatible     类型是否是兼容匹配,为null则完全匹配
     * @param type           开启匹配的类型,与{@link FindType}关联
     * @return 匹配方法或 {@code null}
     */
    public static Method findMethod(Class<?> cls, Class<?> returnType, Class<?>[] parameterTypes, Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible, int type) {
        return findMethod(cls, null, returnType, parameterTypes, exceptionTypes, findSuper, autoBox, compatible, type);
    }

    public static Method findMethod(Class<?> cls, Class<?> returnType, Class<?>[] parameterTypes, HookFieldConfigure[] phs, Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible, int type) {
        return findMethod(cls, null, returnType, parameterTypes, phs, exceptionTypes, findSuper, autoBox, compatible, type);
    }

    public static Method[] findMethods(Class<?> cls, Class<?> returnType, Class<?>[] parameterTypes, Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible, int type) {
        return findMethods(cls, returnType, parameterTypes, null, exceptionTypes, findSuper, autoBox, compatible, type);
    }

    /**
     * 根据方法的签名查找匹配方法
     *
     * @return 所有匹配的方法或空数组
     * @see #findMethod(Class, Class, Class[], Class[], boolean, boolean, HookCompatibleType, int)
     */
    public static Method[] findMethods(Class<?> cls, Class<?> returnType, Class<?>[] parameterTypes, HookFieldConfigure[] phs, Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible, int type) {
        if (cls == null) {
            return null;
        }
        Method[] methodArray = getAllMethod(cls, findSuper);
        if (compatible == null) {
            compatible = HookCompatibleType.EQUAL;
        }
        if (ObjectUtil.isNullClass(returnType)) {
            type = type & ~FindType.RETURN.getValue();
        }
        if (ObjectUtil.isNullClasses(parameterTypes)) {
            type = type & ~FindType.PARAMS.getValue();
        }
        if (ObjectUtil.isNullClasses(exceptionTypes)) {
            type = type & ~FindType.EXCEPTION.getValue();
        }
        if ((type & FindType.PARAMS.getValue()) != 0) {
            if (phs == null) {
                phs = new HookFieldConfigure[parameterTypes.length];
            }
        }
        List<Method> list = new ArrayList<>();
        for (Method method : methodArray) {
            if ((type & FindType.RETURN.getValue()) != 0) {
                if (!equalClass(returnType, method.getReturnType(), autoBox, compatible)) {
                    continue;
                }
            }
            if ((type & FindType.PARAMS.getValue()) != 0) {
                if (!equalClasses(parameterTypes, phs, method.getParameterTypes(), autoBox, compatible)) {
                    continue;
                }
            }
            if ((type & FindType.EXCEPTION.getValue()) != 0) {
                if (!equalClasses(exceptionTypes, null, method.getExceptionTypes(), autoBox, compatible)) {
                    continue;
                }
            }
            list.add(method);
        }
        return list.isEmpty() ? null : list.toArray(new Method[0]);
    }

    /**
     * 查找具有指定名称的所有方法,不查找构造函数
     *
     * @param cls        类
     * @param methodName 方法名
     * @param findSuper  查找超类为true
     * @return 匹配的方法集合或空集合
     */
    public static Method[] findAllMethod(Class<?> cls, String methodName, final boolean findSuper) {
        if (cls == null) {
            return null;
        }
        Method[] methodArray = getAllMethod(cls, findSuper);
        List<Method> list = new ArrayList<>();
        for (Method method : methodArray) {
            if (StringUtil.equals(method.getName(), methodName)) {
                list.add(method);
            }
        }
        return list.isEmpty() ? null : list.toArray(new Method[0]);
    }

    public static Method findMethod(Class<?> cls, String methodName, Class<?> returnType, Class<?>[] parameterTypes,
                                    Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, methodName, returnType, parameterTypes, exceptionTypes, findSuper, autoBox, compatible,
                FindType.NAME.getValue() | FindType.RETURN.getValue() | FindType.PARAMS.getValue() | FindType.EXCEPTION.getValue());
    }

    public static Method findMethod(Class<?> cls, String methodName, Class<?> returnType, Class<?>[] parameterTypes, HookFieldConfigure[] phs,
                                    Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible) {
        return findMethod(cls, methodName, returnType, parameterTypes, phs, exceptionTypes, findSuper, autoBox, compatible,
                FindType.NAME.getValue() | FindType.RETURN.getValue() | FindType.PARAMS.getValue() | FindType.EXCEPTION.getValue());
    }

    public static Method findMethod(Class<?> cls, String methodName, Class<?> returnType, Class<?>[] parameterTypes,
                                    Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible, int type) {
        return findMethod(cls, methodName, returnType, parameterTypes, null, exceptionTypes, findSuper, autoBox, compatible, type);
    }

    /**
     * @param cls            查找类
     * @param methodName     方法名,可null
     * @param returnType     方法返回类型, 可null
     * @param parameterTypes 方法参数类型, 可null
     * @param phs            方法参数对应的兼容情况,可null
     * @param exceptionTypes 方法抛出异常类型
     * @param findSuper      是否查找父类
     * @param autoBox        基本类型与包装类型是否可以转换,且兼容匹配
     * @param compatible     所有类型是否兼容查找,当{@param phs} 不为null时对参数匹配使用pts
     * @param type           所要匹配的所有类型
     * @return 返回匹配项或null
     */
    public static Method findMethod(Class<?> cls, String methodName, Class<?> returnType, Class<?>[] parameterTypes,
                                    HookFieldConfigure[] phs, Class<?>[] exceptionTypes, final boolean findSuper, final boolean autoBox, HookCompatibleType compatible, int type) {
        if (cls == null) {
            return null;
        }
        Method[] methodArray = getAllMethod(cls, findSuper);
        if (compatible == null) {
            compatible = HookCompatibleType.EQUAL;
        }
        if (StringUtil.isEmpty(methodName)) {
            type = type & ~FindType.NAME.getValue();
        }
        if (ObjectUtil.isNullClass(returnType)) {
            type = type & ~FindType.RETURN.getValue();
        }
        if (ObjectUtil.isNullClasses(parameterTypes)) {
            type = type & ~FindType.PARAMS.getValue();
        }
        if (ObjectUtil.isNullClasses(exceptionTypes)) {
            type = type & ~FindType.EXCEPTION.getValue();
        }
        if ((type & FindType.PARAMS.getValue()) != 0) {
            if (phs == null) {
                phs = new HookFieldConfigure[parameterTypes.length];
            }
        }
        for (Method method : methodArray) {
            if ((type & FindType.NAME.getValue()) != 0) {
                if (!StringUtil.equals(methodName, method.getName())) {
                    continue;
                }
            }
            if ((type & FindType.RETURN.getValue()) != 0) {
                if (!equalClass(returnType, method.getReturnType(), autoBox, compatible)) {
                    continue;
                }
            }
            if ((type & FindType.PARAMS.getValue()) != 0) {
                if (!equalClasses(parameterTypes, phs, method.getParameterTypes(), autoBox, compatible)) {
                    continue;
                }
            }
            if ((type & FindType.EXCEPTION.getValue()) != 0) {
                if (!equalClasses(exceptionTypes, null, method.getExceptionTypes(), autoBox, compatible)) {
                    continue;
                }
            }
            return method;
        }
        return null;
    }

    /**
     * 查找类的所有构造函数,查找超类没有意义
     *
     * @param cls 类
     * @return 所有构造函数
     */
    public static Constructor<?>[] findAllConstructor(Class<?> cls) {
        return cls == null ? null : cls.getConstructors();
    }

    public static Constructor<?> findConstructor(Class<?> cls, Class<?>[] parameterTypes, final boolean autoBox, HookCompatibleType compatible) {
        return findConstructor(cls, parameterTypes, null, autoBox, compatible);
    }

    /**
     * 构造函数查找忽略异常签名,因为绝大多数情况下不会抛出异常
     *
     * @param cls            查找类
     * @param parameterTypes 构造函数的参数签名
     * @param autoBox        是否转换基本类型与包装类型
     * @param compatible     兼容查找条件
     * @return 匹配的构造函数或null
     */
    public static Constructor<?> findConstructor(Class<?> cls, Class<?>[] parameterTypes, HookFieldConfigure[] phs, final boolean autoBox, HookCompatibleType compatible) {
        if (cls == null) {
            return null;
        }
        if (compatible == null) {
            compatible = HookCompatibleType.EQUAL;
        }
        if (phs == null) {
            phs = new HookFieldConfigure[parameterTypes.length];
        }
        for (Constructor ctor : cls.getDeclaredConstructors()) {
            if (equalClasses(parameterTypes, phs, ctor.getParameterTypes(), autoBox, compatible)) {
                return ctor;
            }
        }
        return null;
    }

    public static Constructor<?>[] findConstructors(Class<?> cls, Class<?>[] parameterTypes, final boolean autoBox, HookCompatibleType compatible) {
        return findConstructors(cls, parameterTypes, null, autoBox, compatible);
    }

    public static Constructor<?>[] findConstructors(Class<?> cls, Class<?>[] parameterTypes, HookFieldConfigure[] phs, final boolean autoBox, HookCompatibleType compatible) {
        if (cls == null) {
            return null;
        }
        if (compatible == null) {
            compatible = HookCompatibleType.EQUAL;
        }
        List<Constructor> list = new ArrayList<>();
        if (phs == null) {
            phs = new HookFieldConfigure[parameterTypes.length];
        }
        for (Constructor ctor : cls.getConstructors()) {
            if (equalClasses(parameterTypes, phs, ctor.getParameterTypes(), autoBox, compatible)) {
                list.add(ctor);
            }
        }
        return list.isEmpty() ? null : list.toArray(new Constructor[0]);
    }

    public static List<Method> getMethodsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls, final boolean searchSupers) {
        Validate.isTrue(cls != null, "The class must not be null");
        Validate.isTrue(annotationCls != null, "The annotation class must not be null");
        final List<Class<?>> classes = (searchSupers ? ClassUtil.getAllSuperclasses(cls) : new ArrayList<>());
        classes.add(0, cls);
        final List<Method> annotatedMethods = new ArrayList<>();
        for (final Class<?> acls : classes) {
            for (final Method method : acls.getDeclaredMethods()) {
                if (method.getAnnotation(annotationCls) != null) {
                    boolean z = false;
                    for (Method match : annotatedMethods) {
                        if (isExtendOrEqual(match, method)) {
                            z = true;
                            break;
                        }
                    }
                    if (!z) {
                        annotatedMethods.add(method);
                    }
                }
            }
        }
        return annotatedMethods;
    }

    public static boolean isExtendOrEqual(Method child, Method parent) {
        Validate.notNull(child, "Null method not allowed.");
        Validate.notNull(parent, "Null method not allowed.");
        if (parent.getDeclaringClass().isAssignableFrom(child.getDeclaringClass())) {
            if (!StringUtil.equals(child.getName(), parent.getName())) {
                return false;
            }
            if (!parent.getReturnType().isAssignableFrom(child.getReturnType())) {
                return false;
            }
            Class[] params1 = child.getParameterTypes();
            Class[] params2 = parent.getParameterTypes();
            if (params1.length == params2.length) {
                for (int i = 0; i < params1.length; i++) {
                    if (params1[i] != params2[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @param classArray
     * @param conf         当不为null时则取它作为约束条件
     * @param toClassArray
     * @param autoBox
     * @param compatible
     * @return
     */
    public static boolean equalClasses(Class<?>[] classArray, HookFieldConfigure[] conf, Class<?>[] toClassArray, boolean autoBox, HookCompatibleType compatible) {
        if (!ArrayUtil.isSameLength(classArray, toClassArray)) {
            return false;
        }
        if (classArray == null) {
            classArray = ArrayUtil.EMPTY_CLASS_ARRAY;
        }
        if (toClassArray == null) {
            toClassArray = ArrayUtil.EMPTY_CLASS_ARRAY;
        }
        if (conf == null) {
            conf = new HookFieldConfigure[classArray.length];
        }
        if (compatible == null) {
            compatible = HookCompatibleType.EQUAL;
        }
        for (int i = 0; i < classArray.length; i++) {
            boolean boxing = conf[i] == null ? autoBox : conf[i].autoBox();
            HookCompatibleType type = conf[i] == null ? compatible : conf[i].compatible();
            switch (type) {
                case EQUAL:
                    if (!ClassUtil.equal(classArray[i], toClassArray[i], boxing, false)) {
                        return false;
                    }
                    break;
                case UP:
                    if (!ClassUtil.equal(classArray[i], toClassArray[i], boxing, true)) {
                        return false;
                    }
                    break;
                case DOWN:
                    if (!ClassUtil.equal(toClassArray[i], classArray[i], boxing, true)) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    public static boolean equalClass(Class<?> cls, Class<?> toClass, boolean autoBox, HookCompatibleType type) {
        if (type == null) {
            type = HookCompatibleType.EQUAL;
        }
        boolean z = true;
        switch (type) {
            case EQUAL:
                z = ClassUtil.equal(cls, toClass, autoBox);
                break;
            case UP:
                z = ClassUtil.equal(cls, toClass, autoBox, true);
                break;
            case DOWN:
                z = ClassUtil.equal(toClass, cls, autoBox, true);
                break;
            default:
                break;
        }
        return z;
    }

    public static Method[] getAllMethod(Class<?> cls, boolean findSuper) {
        if (cls == null) {
            return new Method[0];
        }
        List<Method> methodArray = new ArrayList<>(Arrays.asList(cls.getDeclaredMethods()));
        // 这里有个隐形条件,如果子类找到了这个方法则忽略父类的方法,否者在Hook的过程中会导致Hook多余的父类函数
        if (findSuper) {
            List<Class<?>> superclassList = ClassUtil.getAllSuperclasses(cls);
            if (superclassList != null) {
                for (Class<?> klass : superclassList) {
                    for (Method parent : klass.getDeclaredMethods()) {
                        boolean extend = false;
                        for (Method child : methodArray) {
                            if (isExtendOrEqual(child, parent)) {
                                extend = true;
                                break;
                            }
                        }
                        if (!extend) {
                            methodArray.add(parent);
                        }
                    }
                }
            }
        }
        return methodArray.toArray(new Method[0]);
    }

    public enum FindType {
        /**
         * 返回值
         */
        RETURN(1),
        /**
         * 参数
         */
        PARAMS(2),
        /**
         * 异常
         */
        EXCEPTION(4),
        NAME(8);
        int value;

        FindType(int i) {
            this.value = i;
        }

        public int getValue() {
            return value;
        }
    }
}
