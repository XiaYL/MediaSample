package net.luculent.mediasample.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * 权限申请弹框
 *
 * @author xiayanlei
 * @time 2019/3/20 17:28
 */
public class PermissionProxyActivity extends Activity {

    public static void requestPermissions(Context context) {
        Intent intent = new Intent(context, PermissionProxyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionHelper.getInstance().requestPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.getInstance().postResult(this, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionHelper.getInstance().postResult(this, resultCode);
    }
}
