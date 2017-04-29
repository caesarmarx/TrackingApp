package hitec.com.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hitec.com.ApplicationContext;
import hitec.com.R;
import hitec.com.event.RegisterEvent;
import hitec.com.notification.MyFirebaseInstanceIDService;
import hitec.com.notification.TrackingService;
import hitec.com.proxy.BaseProxy;
import hitec.com.task.RegisterTask;
import hitec.com.util.SharedPrefManager;
import hitec.com.util.StringUtil;
import hitec.com.vo.RegisterTokenResponseVO;

public class MainActivity extends AppCompatActivity {

    //defining views
    private ProgressDialog progressDialog;

    @Bind(R.id.edt_user_name)
    TextInputEditText edtUserName;
    @Bind(R.id.edt_customer_id)
    TextInputEditText edtCustomerId;
    @Bind(R.id.edt_password)
    TextInputEditText edtPassword;

    private Animation shake;

    private String username;
    private String customerID;
    private String password;

    private static final int REQUEST_CODE_PERMISSION = 2;

    private String[] mPermission = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if(!SharedPrefManager.getInstance(this).getFirstRun()) {
            startHomeActivity();
        }

        progressDialog = new ProgressDialog(this);
        shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.edittext_shake);

        if (ActivityCompat.checkSelfPermission(this, mPermission[0])
                != MockPackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    mPermission, REQUEST_CODE_PERMISSION);
        }

    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onRegisterEvent(RegisterEvent event) {
        hideProgressDialog();
        RegisterTokenResponseVO responseVo = event.getResponse();

        if (responseVo != null) {
            if(responseVo.success == BaseProxy.RESPONSE_SUCCESS) {
                int usertype = responseVo.usertype;
                SharedPrefManager.getInstance(this).saveUserName(username);
                SharedPrefManager.getInstance(this).saveCustomerID(customerID);
                SharedPrefManager.getInstance(this).saveFirstRun(false);
                SharedPrefManager.getInstance(this).saveUserType(usertype);

                startHomeActivity();
            } else {
                ApplicationContext.showToastMessage(MainActivity.this, getResources().getStringArray(R.array.register_result)[responseVo.error_code]);
            }
        } else {
            networkError();
        }
    }

    @OnClick(R.id.btn_sign_in)
    void onClickBtnSignIn() {
        username = edtUserName.getText().toString();
        customerID = edtCustomerId.getText().toString();
        password = edtPassword.getText().toString();

        if (!checkUserName()) return;
        if (!checkCustomerID()) return;
        if (!checkPassword()) return;

        startSignIn();
    }

    private boolean checkUserName() {
        if (StringUtil.isEmpty(username)) {
            showInfoNotice(edtUserName);
            return false;
        }

        return true;
    }

    private boolean checkCustomerID() {
        if (StringUtil.isEmpty(username)) {
            showInfoNotice(edtUserName);
            return false;
        }

        return true;
    }

    private boolean checkPassword() {
        if (StringUtil.isEmpty(username)) {
            showInfoNotice(edtUserName);
            return false;
        }

        return true;
    }

    private void showInfoNotice(TextInputEditText target) {
        target.startAnimation(shake);
        if (target.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //storing token to mysql server
    private void startSignIn() {
        progressDialog.setMessage(getResources().getString(R.string.signing_in));
        progressDialog.show();

        String token = SharedPrefManager.getInstance(this).getDeviceToken();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        RegisterTask task = new RegisterTask();
        task.execute(username, customerID, password, token);
    }

    //start Home Activity
    private void startHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideProgressDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void networkError() {
        ApplicationContext.showToastMessage(MainActivity.this, getResources().getString(R.string.network_error));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == MockPackageManager.PERMISSION_GRANTED) {
            }
        }

    }

}