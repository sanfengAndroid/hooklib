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

package com.beichen.hooklib;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.beichen.hooklib.test.StaticHookClassTest;

import org.sfandroid.hooklib.annotation.HookFieldConfigure;
import org.sfandroid.hooklib.annotation.HookMethod;
import org.sfandroid.hooklib.annotation.HookMethods;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @HookMethod(versions = 10025)
    @HookMethod(versions = 10026)
    @Override
    protected void onCreate(@HookFieldConfigure("123") Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            HookMethods ms = MainActivity.class.getDeclaredMethod("onCreate", Bundle.class).getAnnotation(HookMethods.class);
            Log.w("beichen", "" + ms);
            HookMethod m = MainActivity.class.getDeclaredMethod("onCreate", Bundle.class).getAnnotation(HookMethod.class);
            Log.w("beichen", "" + m);
            StaticHookClassTest test = new StaticHookClassTest();
            test.instanceOnlyThis();
            test.instanceOnlyHookWrap(false);
            test.instanceHookWrapAndThis(false);

            test.instanceParam(0, 1, new ArrayList<>(), new ArrayList<>());
            test.instanceHookWrapAndParam(false, 0, new ArrayList<>());
            test.instanceHookWrapThisAndParam(false, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }


}
