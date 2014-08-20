package harmony.android.library.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import harmony.android.library.data.AppConfig;

public class LogManager {

	static private String makeMessageCrashlytics(String tag, String message) {
		return String.format("%s : %s", tag, message);
	}

	static public void logD(String tag, String msg) {
		if (AppConfig.IS_DEBUG) Log.d(tag, msg);
		if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.log(makeMessageCrashlytics(tag, msg));
	}

	static public void logW(String tag, String msg) {
		if (AppConfig.IS_DEBUG) Log.w(tag, msg);
		if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.log(makeMessageCrashlytics(tag, msg));
	}

	static public void logE(String tag, String msg) {
		if (AppConfig.IS_DEBUG) Log.e(tag, msg);
		if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.logException(new Throwable(msg));
	}

	static public void logE(String tag, Throwable throwable) {
		if (AppConfig.IS_DEBUG) Log.e(tag, throwable.getMessage());
		if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.logException(throwable);
	}

	static public void logE(String tag, String msg, Throwable throwable) {
		if (AppConfig.IS_DEBUG) Log.e(tag, msg, throwable);
		if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.logException(throwable);
	}

	static public void logV(String tag, String msg) {
		if (AppConfig.IS_DEBUG) Log.v(tag, msg);
		if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.log(makeMessageCrashlytics(tag, msg));
	}

	static public void logI(String tag, String msg) {
		if (AppConfig.IS_DEBUG) Log.i(tag, msg);
		if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.log(makeMessageCrashlytics(tag, msg));
	}

	//	public static String getTag() {
	//		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
	//		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
	//		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
	//		int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
	//
	//		return className + "." + methodName + "():" + lineNumber;
	//	}

	//	static public void logD(String msg) {
	//		if (AppConfig.IS_DEBUG) Log.d(getTag(), msg);
	//	}
	//
	//	static public void logW(String msg) {
	//		if (AppConfig.IS_DEBUG) Log.w(getTag(), msg);
	//	}
	//
	//	static public void logE(String msg) {
	//		if (AppConfig.IS_DEBUG) Log.e(getTag(), msg);
	//	}
	//
	//	static public void logE(String msg, Throwable throwable) {
	//		if (AppConfig.IS_DEBUG) Log.e(getTag(), msg, throwable);
	//	}
	//
	//	static public void logV(String msg) {
	//		if (AppConfig.IS_DEBUG) Log.v(getTag(), msg);
	//	}
	//
	//	static public void logI(String msg) {
	//		if (AppConfig.IS_DEBUG) Log.i(getTag(), msg);
	//	}
}
