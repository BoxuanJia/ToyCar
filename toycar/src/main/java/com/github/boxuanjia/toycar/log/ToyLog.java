package com.github.boxuanjia.toycar.log;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ToyLog {

    private static final int DOMAIN = 0x546f79;

    private static final String TAG = "Toy";

    private static HiLogLabel hiLogLabel;

    public static void initialize() {
        hiLogLabel = new HiLogLabel(HiLog.LOG_APP, DOMAIN, TAG);
    }

    public static void i(String string) {
        if (HiLog.isLoggable(DOMAIN, TAG, HiLog.INFO)) {
            HiLog.info(hiLogLabel, string);
        }
    }

    public static void w(String string) {
        if (HiLog.isLoggable(DOMAIN, TAG, HiLog.WARN)) {
            HiLog.warn(hiLogLabel, string);
        }
    }

    public static void e(String string) {
        if (HiLog.isLoggable(DOMAIN, TAG, HiLog.ERROR)) {
            HiLog.error(hiLogLabel, string);
        }
    }

    public static void wtf(String string) {
        if (HiLog.isLoggable(DOMAIN, TAG, HiLog.FATAL)) {
            HiLog.fatal(hiLogLabel, string);
        }
    }
}
