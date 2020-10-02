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

import com.beichen.hooklib.BuildConfig;

import org.sfandroid.hooklib.HookEntry;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author beichen
 * @date 2020/09/28
 */
public class XposedMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            return;
        }
        Log.i("Xposed",
                "start hook: " + this.getClass().getClassLoader() + ", target: " + lpparam.classLoader);
        Log.i("Xposed", "equals: " + this.getClass().getClassLoader().equals(lpparam.classLoader));
        Log.i("Xposed", "third classloader: " + lpparam.classLoader.hashCode());
        test(lpparam.classLoader);
    }

    public void test(ClassLoader loader) {
        initEnv();
        HookEntry.getInstance().addHook(new StaticNameHook());
        startHook(loader);
    }

    private void initEnv() {
        HookEntry.getInstance().setHookFrame(new XposedFrame());
    }

    private void startHook(ClassLoader loader) {
        HookEntry.getInstance().openActivityListener();
        HookEntry.getInstance().initHookAll();
        HookEntry.getInstance().openAllHook(loader, BuildConfig.APPLICATION_ID, true, 1L);
    }
}
