package harmony.android.library.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import harmony.android.library.base.MyApplication;
import harmony.android.library.utils.LogManager;


public class LocalData {
    private static final String TAG = LocalData.class.getSimpleName();
    public static final int INT_UNKNOWN = -1;

    // Custom store
    public static boolean clear(String sharePrefKey) {
        LogManager.logI(TAG, String.format("clear with prefKey = %s", sharePrefKey));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(sharePrefKey);
        return editor.commit();
    }

    public final static int TYPE_INT = 1;
    public final static int TYPE_STRING = 2;
    public final static int TYPE_BOOLEAN = 3;

    public static boolean save(String prefKey, Object data, int type) {
        LogManager.logI(TAG, String.format("save with prefKey = %s data = %s type = %d", prefKey, data, type));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (type) {
            case TYPE_INT:
                editor.putInt(prefKey, (Integer) data);
                break;
            case TYPE_STRING:
                editor.putString(prefKey, (String) data);
                break;
            case TYPE_BOOLEAN:
                editor.putBoolean(prefKey, (Boolean) data);
                break;
            default:
                break;
        }
        return editor.commit();
    }

    public static Object load(String prefKey, int type) {
        LogManager.logI(TAG, String.format("load with prefKey = %s type = %d", prefKey, type));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        switch (type) {
            case TYPE_INT:
                return sharedPreferences.getInt(prefKey, INT_UNKNOWN);
            case TYPE_STRING:
                return sharedPreferences.getString(prefKey, null);
            case TYPE_BOOLEAN:
                return sharedPreferences.getBoolean(prefKey, false);
            default:
                break;
        }
        return null;
    }

    public static void removeAll() {
        LogManager.logI(TAG, "removeAll");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        boolean success = editor.commit();
        if (success) LogManager.logD(TAG, "removeAll success");
        else {
            LogManager.logD(TAG, "removeAll fail");
        }
    }

}