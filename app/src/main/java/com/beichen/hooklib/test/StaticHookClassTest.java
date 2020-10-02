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


import java.util.ArrayList;
import java.util.List;

/**
 * @author beichen
 * @date 2019/11/24
 */
public class StaticHookClassTest {
    public boolean hook = false;

    public StaticHookClassTest() {
    }

    public StaticHookClassTest(int i1, Integer i2, List list1, ArrayList list2) {
    }

    public StaticHookClassTest(int i1, Integer i2, Long j1, long j2) {
    }

    private static Object availedInline(Object... arg) {
        return new Object[]{arg};
    }

    public void instanceOnlyThis() {
        Assert.assertTrue(hook);
        availedInline("test string");
        availedInline(new Object());
    }

    public void instanceOnlyHookWrap(boolean hooked) {
        Assert.assertTrue(hooked);
    }

    public void instanceHookWrapAndThis(boolean hooked) {
        Assert.assertTrue(hooked);
        Assert.assertTrue(hook);
        availedInline(hooked);
        availedInline(hook);
    }

    public void instanceParam(int i1, Integer i2, List list1, ArrayList list2) throws UnsupportedOperationException {
        Assert.assertTrue(!list1.isEmpty());
        Assert.assertTrue(!list2.isEmpty());
        availedInline(hook);
        availedInline(i1, i2, list1);
        availedInline(list2);
    }

    public void instanceHookWrapAndParam(boolean hooked, int i1, List list) throws IllegalArgumentException {
        Assert.assertTrue(hooked);
        Assert.assertTrue(!list.isEmpty());
        availedInline(hooked);
        availedInline(hook);
        availedInline(hooked, i1);
        availedInline(hooked, list);
    }

    public void instanceHookWrapThisAndParam(boolean hooked, int i) throws IllegalArgumentException {
        Assert.assertTrue(hooked);
        Assert.assertTrue(hook);
        availedInline(hooked);
        availedInline(hook);
    }
}
