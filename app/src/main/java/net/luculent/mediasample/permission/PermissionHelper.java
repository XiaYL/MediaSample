package net.luculent.mediasample.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 应用权限申请类
 *
 * @author xiayanlei
 * @time 2019/3/20 17:28
 */
public class PermissionHelper {

    private static final String TAG = "PermissionHelper";

    public static final int PERMISSION_REQUEST = 0X01;

    private static PermissionHelper instance;

    private List<String> appPermissions = new ArrayList<>();

    private static List<String> REGISTERED_PERMISSIONS;

    private String intentAction = "";//权限申请action

    private PermissionResultCallback callback;

    public static PermissionHelper getInstance() {
        if (instance == null) {
            instance = new PermissionHelper();
        }
        return instance;
    }

    private PermissionHelper() {
    }

    public PermissionHelper permissions(@PermissionConstants.Permission String... permissions) {
        for (String permission : permissions) {
            if (!appPermissions.contains(permission)) {
                appPermissions.add(permission);
            }
        }
        return this;
    }

    public PermissionHelper intent(String action) {
        this.intentAction = action;
        return this;
    }

    public PermissionHelper callback(PermissionResultCallback callback) {
        this.callback = callback;
        return this;
    }

    public boolean check(Context context) {
        if (callback == null) {
            callback = new SimplePermissionCallback();
        }
        return intentCheck(context) || manifestPermissionCheck(context);
    }

    boolean intentCheck(Context context) {
        if (!TextUtils.isEmpty(intentAction)) {
            PermissionProxyActivity.requestPermissions(context);
            return true;
        }
        return false;
    }

    boolean manifestPermissionCheck(Context context) {
        if (REGISTERED_PERMISSIONS == null) {
            REGISTERED_PERMISSIONS = Arrays.asList(getRequestedPermissions(context));
        }
        Set<String> permissions = new LinkedHashSet<>();
        for (String permission : appPermissions) {//同一个权限组的权限同时申请
            for (String aPermission : PermissionConstants.getPermissions(permission)) {
                if (REGISTERED_PERMISSIONS.contains(aPermission) && !isPermissionGranted(context, aPermission)) {
                    permissions.add(aPermission);
                }
            }
        }
        appPermissions = new ArrayList<>(permissions);
        boolean shouldRequest = appPermissions.size() > 0;
        if (shouldRequest) {
            PermissionProxyActivity.requestPermissions(context);
        } else {//不需要申请任何权限
            callback.onPermissionDenied(new String[]{}, new Boolean[]{});
        }
        return shouldRequest;
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission(Activity activity) {
        if (TextUtils.isEmpty(intentAction)) {
            ActivityCompat.requestPermissions(activity, appPermissions.toArray(new String[]{}),
                    PERMISSION_REQUEST);
        } else {
            ActivityCompat.startActivityForResult(activity, new Intent(intentAction),
                    PERMISSION_REQUEST, null);
        }
    }

    void postResult(Activity activity, String[] permissions, int[] grantResults) {
        Set<String> allowed = new LinkedHashSet<>();
        Map<String, Boolean> denied = new LinkedHashMap<>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                allowed.add(permission);
            } else {
                denied.put(permission, !ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        permission));
            }
        }
        callback.onPermissionDenied(denied.keySet().toArray(new String[]{}),
                denied.values().toArray(new Boolean[]{}));
        callback.onPermissionAllowed(allowed.toArray(new String[]{}));
        callback = null;
        appPermissions.clear();
        activity.finish();
    }

    /**
     * intent申请权限结果
     *
     * @param activity
     * @param resultCode
     */
    void postResult(Activity activity, int resultCode) {
        activity.finish();
        if (callback != null) {
            callback.onActivityResult(resultCode, new Intent(intentAction));
        }
        intentAction = "";
    }

    /**
     * 获取已请求的权限
     *
     * @param context
     * @return
     */
    private static String[] getRequestedPermissions(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            return packageInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    public interface PermissionResultCallback {

        void onPermissionDenied(String[] permissions, Boolean[] always);//禁止权限,always:true,不再询问,false,重新弹出请求框

        void onPermissionAllowed(String[] permissions);//允许权限

        void onActivityResult(int resultCode, Intent data);
    }

    public static class SimplePermissionCallback implements PermissionResultCallback {

        @Override
        public void onPermissionDenied(String[] permissions, Boolean[] always) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i(TAG, "onPermissionDenied: " + permissions[i] + " forbidden forever " + always[i]);
            }
        }

        @Override
        public void onPermissionAllowed(String[] permissions) {
            Log.i(TAG, "onPermissionAllowed: " + Arrays.toString(permissions));
        }

        @Override
        public void onActivityResult(int resultCode, Intent data) {
            Log.i(TAG, "onActivityResult: " + resultCode + "***" + data.getAction());
        }
    }
}
