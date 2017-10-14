package com.acekabs.driverapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.SignIn;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.pojo.ApiResultData;
import com.acekabs.driverapp.restclient.RestApiService;
import com.acekabs.driverapp.utils.ApiUtils;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    private Button btnChangePwd;
    private EditText edtOldPassword;
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private ProgressDialog dialog;
    private Context context;
    private SharePreferanceWrapperSingleton prefrences;
    private RestApiService mAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        context = this;
        dialog = new ProgressDialog(context);
        edtOldPassword = (EditText) findViewById(R.id.userPassword);
        edtNewPassword = (EditText) findViewById(R.id.userNewPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.userConfirmPassword);
        btnChangePwd = (Button) findViewById(R.id.btnChangePwd);
        btnChangePwd.setOnClickListener(this);
        prefrences = SharePreferanceWrapperSingleton.getSingletonInstance();
        prefrences.setPref(context);
        prefrences.setEditor();
        mAPIService = ApiUtils.getAPIService(ApplicationConstants.BASEURL);
    }

    @Override
    public void onClick(View v) {
        if (v == btnChangePwd) {
            try {
                boolean isValid = true;
                if (edtOldPassword.getText().toString().trim().length() == 0) {
                    isValid = false;
                    Toast.makeText(context, "Old Password can't be left blank", Toast.LENGTH_SHORT).show();
                }
                if (edtOldPassword.getText().toString().trim().length() > 0 && !edtOldPassword.getText().toString().trim().equals(prefrences.getStringValueFromSharedPref(Constants.password))) {
                    isValid = false;
                    Toast.makeText(context, "Old Password not matched with Current Password", Toast.LENGTH_SHORT).show();
                }
                if (edtNewPassword.getText().toString().trim().length() == 0) {
                    isValid = false;
                    Toast.makeText(context, "New Password can't be left blank", Toast.LENGTH_SHORT).show();
                }
                if (edtConfirmPassword.getText().toString().trim().length() == 0) {
                    isValid = false;
                    Toast.makeText(context, "Confirm Password can't be left blank", Toast.LENGTH_SHORT).show();
                }
                if (!edtNewPassword.getText().toString().trim().equals(edtNewPassword.getText().toString().trim())) {
                    isValid = false;
                    Toast.makeText(context, "New Password doesn't match with Confirm Password.", Toast.LENGTH_SHORT).show();
                }
                if (isValid) {
                    if (isNetworkAvailable()) {
                        dialog.show();
                        mAPIService.changePassword(prefrences.getStringValueFromSharedPref(Constants.userId), edtOldPassword.getText().toString().trim(), edtNewPassword.getText().toString().trim()).enqueue(new Callback<ApiResultData>() {
                            @Override
                            public void onResponse(Call<ApiResultData> call, Response<ApiResultData> response) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        ApiResultData resultData = response.body();
                                        if (resultData != null) {
                                            if (resultData.getCode().equals("100")) {
                                                Toast.makeText(context, resultData.getInfo(), Toast.LENGTH_SHORT).show();
                                                prefrences.setValueToSharedPref(Constants.password, "");
                                                Intent intent = new Intent(context, SignIn.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                ShowAlert1("ERROR", resultData.getInfo());
                                            }
                                        }
                                    }
                                } else {
                                    if (response.errorBody() != null) {
                                        ShowAlert1("ERROR", response.errorBody().toString());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResultData> call, Throwable t) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Log.e("Error", "Unable to submit post to API.");
                            }
                        });
                    } else {
                        Toast.makeText(context, "Please connect with Active Internet Connection!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void ShowAlert1(String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.show();

        TextView tit = (TextView) dialog.findViewById(R.id.dialog_title);
        tit.setText(title);
        TextView mes = (TextView) dialog.findViewById(R.id.dialog_message);
        mes.setText(message);
        Button ok = (Button) dialog.findViewById(R.id.dialog_submit);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                dialog.cancel();
               /* Intent li = new Intent(MainHomeScreen.this, SignIn.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);*/

            }
        });

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
