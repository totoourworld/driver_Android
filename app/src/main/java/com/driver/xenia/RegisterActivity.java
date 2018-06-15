package com.driver.xenia;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.com.driver.webservice.Constants;
import com.com.driver.webservice.SingleObject;
import com.custom.CustomProgressDialog;
import com.grepix.grepixutils.CloudResponse;
import com.grepix.grepixutils.ErrorJsonParsing;
import com.grepix.grepixutils.Validations;
import com.grepix.grepixutils.WebServiceUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NewApi")
public class RegisterActivity extends Activity  {

    CustomProgressDialog dialog;

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.mobile)
    EditText etmobile;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.repassword)
    EditText repassword;
    @BindView(R.id.first_name)
    EditText etfirst_name;
    @BindView(R.id.last_name)
    EditText etlast_name;

    protected static final String TAG = "RegisterActivity";
    String regId;
    int count = 1;
    Controller controller;

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        controller = (Controller) getApplicationContext();
        dialog = new CustomProgressDialog(RegisterActivity.this);
        regId = controller.pref.getD_DEVICE_TOKEN();

        if (Build.VERSION.SDK_INT >= 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        etfirst_name.requestFocus();
    }

    @OnClick(R.id.next)
   public void nextButton(){
        if(Validations.isValidateRegister(RegisterActivity.this,etfirst_name,etlast_name,email,etmobile,password,repassword)){
            signUpDriver();
        }
    }

    @OnClick(R.id.cancel)
    public void backButton(){
        finish();
    }

    private void signUpDriver() {
        dialog.showDialog();

        Map<String, String> params = new HashMap<String, String>();
        params.put("d_email", email.getText().toString());
        params.put("d_phone", etmobile.getText().toString());
        params.put("d_password", password.getText().toString());

        if (regId != null) {
            params.put("d_device_type", "Android");
            params.put("d_device_token", regId);
        }
        params.put("d_fname", etfirst_name.getText().toString());
        params.put("d_lname", etlast_name.getText().toString());
        //params.put("d_is_verified", String.valueOf(1));

        WebServiceUtil.excuteRequest(RegisterActivity.this, params, Constants.Urls.URL_DRIVER_REGISTER, new WebServiceUtil.DeviceTokenServiceListener() {
            @Override
            public void onUpdateDeviceTokenOnServer(Object data, boolean isUpdate, VolleyError error) {
                dialog.dismiss();
                if (isUpdate) {
                    String response = data.toString();
                    ErrorJsonParsing parser = new ErrorJsonParsing();
                    CloudResponse res = parser.getCloudResponse("" + response);
                    dialog.dismiss();
                    if (res.isStatus()) {
                        controller.pref.setPASSWORD(password.getText().toString());
                        SingleObject object = SingleObject.getInstance();
                        object.driverUpdateProfileParseApi(response);
                        controller.pref.setAPI_KEY(object.getApiKey());
                        controller.pref.setDRIVER_ID(object.getDriverId());
                        controller.pref.setIsLogin(true);
                        Intent main = new Intent(getApplicationContext(), DocUploadActivity1.class);
                        main.putExtra("driver_id", object.getDriverId());
                        main.putExtra("api_key", object.getApiKey());
                        startActivity(main);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getError(), Toast.LENGTH_LONG).show();
                    }
                } else {
                 //   Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
