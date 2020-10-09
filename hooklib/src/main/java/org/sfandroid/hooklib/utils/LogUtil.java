package org.sfandroid.hooklib.utils;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author beichen
 * @date 2020/10/07
 */
public class LogUtil {
    private static final int MAX_LENGTH = 3 * 1024;
    private static final int STATE_NONE = -1;
    public static boolean OPEN_LOG = true;
    public static String HEAD = "HookLog_";
    public static boolean ADD_HEAD = true;
    public static String DEFAULT_HEAD = "HookLog";
    private static LogCallback[] callbacks = new LogCallback[5];

    public static void v(String format, Object... msg) {
        print(STATE_NONE, Log.VERBOSE, DEFAULT_HEAD, format, msg);
    }

    public static void v(String tag, String format, Object... msg) {
        print(STATE_NONE, Log.VERBOSE, tag, format, msg);
    }

    public static void v(int state, String format, Object... msg) {
        print(state, Log.VERBOSE, DEFAULT_HEAD, format, msg);
    }

    public static void v(int state, String tag, String format, Object... msg) {
        print(state, Log.VERBOSE, tag, format, msg);
    }

    public static void d(String format, Object... msg) {
        print(STATE_NONE, Log.DEBUG, DEFAULT_HEAD, format, msg);
    }

    public static void d(String tag, String format, Object... msg) {
        print(STATE_NONE, Log.DEBUG, tag, format, msg);
    }

    public static void d(int state, String format, Object... msg) {
        print(state, Log.DEBUG, DEFAULT_HEAD, format, msg);
    }

    public static void d(int state, String tag, String format, Object... msg) {
        print(state, Log.DEBUG, tag, format, msg);
    }

    public static void i(String format, Object... msg) {
        print(STATE_NONE, Log.INFO, DEFAULT_HEAD, format, msg);
    }

    public static void i(String tag, String format, Object... msg) {
        print(STATE_NONE, Log.INFO, tag, format, msg);
    }

    public static void i(int state, String format, Object... msg) {
        print(state, Log.INFO, DEFAULT_HEAD, format, msg);
    }

    public static void i(int state, String tag, String format, Object... msg) {
        print(state, Log.INFO, tag, format, msg);
    }

    public static void w(String format, Object... msg) {
        print(STATE_NONE, Log.WARN, DEFAULT_HEAD, format, msg);
    }

    public static void w(String tag, String format, Object... msg) {
        print(STATE_NONE, Log.WARN, tag, format, msg);
    }

    public static void w(int state, String format, Object... msg) {
        print(state, Log.WARN, DEFAULT_HEAD, format, msg);
    }

    public static void w(int state, String tag, String format, Object... msg) {
        print(state, Log.WARN, tag, format, msg);
    }

    public static void e(String format, Object... msg) {
        print(STATE_NONE, Log.ERROR, DEFAULT_HEAD, format, msg);
    }

    public static void e(String tag, String format, Object... msg) {
        print(STATE_NONE, Log.ERROR, tag, format, msg);
    }

    public static void e(int state, String format, Object... msg) {
        print(state, Log.ERROR, DEFAULT_HEAD, format, msg);
    }

    public static void e(int state, String tag, String format, Object... msg) {
        print(state, Log.ERROR, tag, format, msg);
    }

    private static void print(int state, int level, String tag, String format, Object... msg) {
        Throwable throwable = null;
        if (callbacks[level - Log.VERBOSE] != null) {
            String str = formatText(format, msg);
            if (level >= Log.WARN) {
                if (msg.length > 0 && msg[msg.length - 1] instanceof Throwable) {
                    throwable = (Throwable) msg[msg.length - 1];
                }
            }
            if (throwable != null) {
                try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                    throwable.printStackTrace(pw);
                    callbacks[level - Log.VERBOSE].visit(state, level, tag, str + '\n' + sw.toString());
                } catch (IOException ignore) {
                    callbacks[level - Log.VERBOSE].visit(state, level, tag, str);
                }
            } else {
                callbacks[level - Log.VERBOSE].visit(state, level, tag, str);
            }
            return;
        }
        if (!OPEN_LOG) {
            return;
        }
        String str = formatText(format, msg);
        if (level >= Log.WARN) {
            if (msg.length > 0 && msg[msg.length - 1] instanceof Throwable) {
                throwable = (Throwable) msg[msg.length - 1];
            }
        }
        String[] ret = splitMsg(str);
        for (int i = 0; i < ret.length; i++) {
            String head;
            if (i == 0) {
                head = ADD_HEAD ? HEAD + tag : tag;
            } else {
                head = ADD_HEAD ? HEAD + tag + i : tag + i;
            }
            switch (level) {
                case Log.VERBOSE:
                    Log.v(head, ret[i]);
                    break;
                case Log.DEBUG:
                    Log.d(head, ret[i]);
                    break;
                case Log.INFO:
                    Log.i(head, ret[i]);
                    break;
                case Log.WARN:
                    if (i == 0) {
                        Log.w(head, ret[i], throwable);
                    } else {
                        Log.w(head, ret[i]);
                    }
                    break;
                case Log.ERROR:
                    if (i == 0) {
                        Log.e(head, ret[i], throwable);
                    } else {
                        Log.e(head, ret[i]);
                    }
                    break;
                case Log.ASSERT:
                    break;
                default:
                    break;
            }
        }
    }

    public static void println(int priority, String tag, String format, Object... msg) {
        if (OPEN_LOG) {
            Log.println(priority, ADD_HEAD ? HEAD + tag : tag, formatText(format, msg));
        }
    }

    private static String formatText(String format, Object... msg) {
        try {
            if (msg != null) {
                return String.format(format, msg);
            }
        } catch (Throwable ignored) {
        }
        return format;
    }

    private static String[] splitMsg(String msg) {
        if (msg.length() <= MAX_LENGTH) {
            return new String[]{msg};
        }
        int len = msg.length() / MAX_LENGTH + 1;
        String[] ret = new String[len];
        int index = 0;
        for (int i = 0; index < msg.length(); i++) {
            if (msg.length() <= index + MAX_LENGTH) {
                ret[i] = msg.substring(index);
            } else {
                ret[i] = msg.substring(index, index + MAX_LENGTH);
            }
            index += MAX_LENGTH;
        }
        return ret;
    }

    public static void addCallback(int level, LogCallback callback) {
        if (level >= Log.VERBOSE && level <= Log.ERROR) {
            callbacks[level - Log.VERBOSE] = callback;
        }
    }


    public interface LogCallback {
        /**
         * @param state 状态标记,自行区分,默认-1
         * @param level 日志等级
         * @param tag   日志标签
         * @param msg   日志内容,如果有异常也包含在其中
         */
        void visit(int state, int level, String tag, String msg);
    }
}
