package com.tesis.yudith.showmethepast.configuration;

public class AppBlocker {
    public interface IOnBlockListener {
        void onAppBlockerChange(boolean visible);
    }

    private static IOnBlockListener currentListener;

    public static void setListener(IOnBlockListener listener) {
        currentListener = listener;
    }

    public static void loading() {
        if (currentListener != null) {
            currentListener.onAppBlockerChange(true);
        }
    }
    public static void finished() {
        if (currentListener != null) {
            currentListener.onAppBlockerChange(false);
        }
    }
}
