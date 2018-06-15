package com.driver.xenia;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.com.driver.webservice.Constants;
import com.custom.CustomProgressDialog;
import com.grepix.grepixutils.CloudResponse;
import com.grepix.grepixutils.ErrorJsonParsing;
import com.grepix.grepixutils.Validations;
import com.grepix.grepixutils.WebServiceUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NewApi")
public class Resetpassword extends Activity {

      CustomProgressDialog progressDialog;
      @BindView(R.id.reemail)
      public EditText Email;
	  String Get_email;
      protected static final String TAG = "ResetPassword Activity";
      public    static String User_id;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        getActionBar().hide();
        progressDialog=new CustomProgressDialog(Resetpassword.this);

}

    @OnClick(R.id.reset)
    public void onResetPasswordClick(){
        Get_email = Email.getText().toString();
        if(Validations.isValidateRecoverPassword(Resetpassword.this,Email)){
            recoverPasswordNow() ;
        }

    }

    @OnClick(R.id.recancel)
    public void onBackClick(){
        finish();

    }

    private void recoverPasswordNow() {
        progressDialog.showDialog();
        Map<String, String> params = new HashMap<String, String>();
        params.put("d_email", Get_email);
        WebServiceUtil.excuteRequest(Resetpassword.this, params, Constants.FORGET_PASSWORD, new WebServiceUtil.DeviceTokenServiceListener() {
            @Override
            public void onUpdateDeviceTokenOnServer(Object data, boolean isUpdate, VolleyError error) {
                progressDialog.dismiss();
                if (isUpdate) {
                    String response = data.toString();
                    ErrorJsonParsing parser = new ErrorJsonParsing();
                    CloudResponse res = parser.getCloudResponse("" + response);
                    if (res.isStatus()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Please check your email Id", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    } else {
                        Toast.makeText(getApplicationContext(), res.getError(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.internet_error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }




    
}
