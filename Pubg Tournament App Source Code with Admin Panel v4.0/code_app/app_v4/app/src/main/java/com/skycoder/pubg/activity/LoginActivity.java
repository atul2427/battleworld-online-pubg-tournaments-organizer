package com.skycoder.pubg.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.skycoder.pubg.R;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;
import com.skycoder.pubg.utils.MySingleton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhoneAuth";
    private ProgressBar progressBar;
    private Dialog dialog;
    private CountDownTimer countDownTimer;

    private String id;
    private String balance;
    private String dateOfbirth;
    private String email;
    private String firstname;
    private String fullname;
    private String gender;
    private String lastname;
    private String message;
    private String mobilenumber;
    private String password;
    private TextInputEditText cCode;
    private String countryCode;
    private String strCodePhone;
    private String uname;

    private TextInputEditText username;
    private TextInputEditText pword;
    private TextView regSuccess;
    private TextView resetText;
    private Button register;
    private Button signin;

    private RelativeLayout phoneRL, codeRL, passRL;
    private SessionManager session;

    private String forgetPhone;
    private String newPassword;
    private String retypeNewPassword;

    private TextView phoneVerifyResponse, codeVerifyResponse, passwordResetResponse, resendOTP, timerOTP;
    private TextInputEditText phoneNumber;
    private TextInputEditText otpCode;
    private TextInputEditText newPassEt;
    private TextInputEditText cnfmPassEt;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());

        initView();
        initListener();
    }

    private void initListener() {
        this.signin.setOnClickListener(this);
        this.register.setOnClickListener(this);
        this.resetText.setOnClickListener(this);
    }

    private void initView() {
        this.register = (Button) findViewById(R.id.registerFromLogin);
        this.signin = (Button) findViewById(R.id.signinbtn);
        this.username = (TextInputEditText) findViewById(R.id.username);
        this.pword = (TextInputEditText) findViewById(R.id.password);
        this.regSuccess = (TextView) findViewById(R.id.regSuccessMessage);
        this.resetText = (TextView) findViewById(R.id.resetnow);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void submitdata() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        this.uname = this.username.getText().toString().trim();
        this.password = this.pword.getText().toString().trim();

        if (validateusername() && validatepassword()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_LOGIN_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("username", uname);
                builder.appendQueryParameter("password", password);
                StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");

                            if (success.equals("0")) {
                                progressBar.setVisibility(View.GONE);
                                String msg = jsonObject1.getString("msg");
                                Toast.makeText(getApplicationContext(),""+msg, Toast.LENGTH_LONG).show();
                            }
                            else if (success.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                id = jsonObject1.getString("id");
                                firstname = jsonObject1.getString("fname");
                                lastname = jsonObject1.getString("lname");
                                email = jsonObject1.getString("email");
                                mobilenumber = jsonObject1.getString("mobile");
                                session.createLoginSession(id,firstname,lastname,uname,password,email,mobilenumber);

                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validateusername() {
        this.uname = this.username.getText().toString().trim();
        if (!this.uname.isEmpty()) {
            return true;
        } else {
            this.username.setError("Please enter valid username or mobile number.");
            return false;
        }
    }

    public boolean validatepassword() {
        if (!this.password.isEmpty()) {
            return true;
        }
        this.pword.setError("Please enter valid Password.");
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signinbtn){
            submitdata();
        }
        else if (v.getId() == R.id.registerFromLogin){
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.resetnow){
            showForgotPasswordPrompt(LoginActivity.this);
        }
    }

    public void showForgotPasswordPrompt(final Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        phoneRL = (RelativeLayout) dialog.findViewById(R.id.phoneRL);
        codeRL = (RelativeLayout) dialog.findViewById(R.id.codeRL);
        passRL = (RelativeLayout) dialog.findViewById(R.id.passRL);

        cCode = (TextInputEditText) dialog.findViewById(R.id.countryCode);
        phoneNumber = (TextInputEditText) dialog.findViewById(R.id.phoneNumber);
        phoneVerifyResponse = (TextView) dialog.findViewById(R.id.phoneVerifyResponse);
        Button reset = (Button) dialog.findViewById(R.id.reset);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        otpCode = (TextInputEditText) dialog.findViewById(R.id.otpCode);
        resendOTP = (TextView) dialog.findViewById(R.id.resendOTP);
        timerOTP = (TextView) dialog.findViewById(R.id.timerOTP);
        codeVerifyResponse = (TextView) dialog.findViewById(R.id.codeVerifyResponse);
        Button reset1 = (Button) dialog.findViewById(R.id.reset1);
        Button cancel1 = (Button) dialog.findViewById(R.id.cancel1);

        newPassEt = (TextInputEditText) dialog.findViewById(R.id.newPassEt);
        cnfmPassEt = (TextInputEditText) dialog.findViewById(R.id.cnfmPassEt);
        passwordResetResponse = (TextView) dialog.findViewById(R.id.passwordResetResponse);
        Button reset2 = (Button) dialog.findViewById(R.id.reset2);
        Button cancel2 = (Button) dialog.findViewById(R.id.cancel2);

        if (countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long mins = millisUntilFinished / 60000;
                long secs = millisUntilFinished % 60000 / 1000;
                final String display = String.format("%02d:%02d", mins, secs);

                timerOTP.setText(display);
                resendOTP.setEnabled(false);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                timerOTP.setVisibility(View.GONE);
                resendOTP.setEnabled(true);
            }
        }.start();

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(resendToken);

                if (countDownTimer != null){
                    countDownTimer.cancel();
                }
                new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long mins = millisUntilFinished / 60000;
                        long secs = millisUntilFinished % 60000 / 1000;
                        final String display = String.format("%02d:%02d", mins, secs);

                        timerOTP.setText(display);
                        resendOTP.setEnabled(false);
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        timerOTP.setVisibility(View.GONE);
                        resendOTP.setEnabled(true);
                    }

                }.start();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        cancel1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        cancel2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                forgetPhone = phoneNumber.getText().toString().trim();
                countryCode = cCode.getText().toString().trim();
                strCodePhone=countryCode+forgetPhone;
                if (!forgetPhone.isEmpty()) {
                    if (Patterns.PHONE.matcher(forgetPhone).matches()) {
                        if (forgetPhone.length() >= 5 && forgetPhone.length() <= 15 && countryCode.length() >= 1){
                            phoneVerifyResponse.setText("Please wait few seconds...");
                            phoneVerifyResponse.setTextColor(Color.parseColor("#000000"));
                            phoneVerifyResponse.setVisibility(View.VISIBLE);
                            setUpVerificationCallbacks();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    strCodePhone,  // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    LoginActivity.this,               // Activity (for callback binding)
                                    verificationCallbacks);
                        }
                        else {
                            phoneVerifyResponse.setText("Enter Valid Phone Number or Country Code");
                            phoneVerifyResponse.setTextColor(Color.parseColor("#ff0000"));
                            phoneVerifyResponse.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        phoneVerifyResponse.setText("Enter Valid Phone Number");
                        phoneVerifyResponse.setTextColor(Color.parseColor("#ff0000"));
                        phoneVerifyResponse.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    phoneVerifyResponse.setText("Enter Valid Phone Number");
                    phoneVerifyResponse.setTextColor(Color.parseColor("#ff0000"));
                    phoneVerifyResponse.setVisibility(View.VISIBLE);
                }
            }
        });

        reset1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                verifyCode();
            }
        });

        reset2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                submitData();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void setUpVerificationCallbacks() {
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                phoneRL.setVisibility(View.GONE);
                codeRL.setVisibility(View.VISIBLE);
                passRL.setVisibility(View.GONE);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                phoneRL.setVisibility(View.VISIBLE);
                codeRL.setVisibility(View.GONE);
                passRL.setVisibility(View.GONE);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    Log.d(TAG, "SMS Quota exceeded.");
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
                resendToken = token;

                phoneRL.setVisibility(View.GONE);
                codeRL.setVisibility(View.VISIBLE);
                passRL.setVisibility(View.GONE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    codeVerifyResponse.setVisibility(View.GONE);
                    phoneRL.setVisibility(View.GONE);
                    codeRL.setVisibility(View.GONE);
                    passRL.setVisibility(View.VISIBLE);
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        codeVerifyResponse.setVisibility(View.VISIBLE);
                        codeVerifyResponse.setTextColor(Color.parseColor("#ff0000"));
                        codeVerifyResponse.setText("The verification code entered was invalid");
                        phoneRL.setVisibility(View.GONE);
                        codeRL.setVisibility(View.VISIBLE);
                        passRL.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void verifyCode() {
        try {
            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String code1 = otpCode.getText().toString().trim();
        if (code1.length() == 6){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code1);
            signInWithPhoneAuthCredential(credential);
        }
        else {
            codeVerifyResponse.setVisibility(View.VISIBLE);
            codeVerifyResponse.setTextColor(Color.parseColor("#ff0000"));
            codeVerifyResponse.setText("The verification code entered was invalid");
            phoneRL.setVisibility(View.GONE);
            codeRL.setVisibility(View.VISIBLE);
            passRL.setVisibility(View.GONE);
        }
    }

    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                strCodePhone,          // Phone number to verify
                60,                  // Timeout duration
                TimeUnit.SECONDS,       // Unit of timeout
                this,            // Activity (for callback binding)
                verificationCallbacks,  // OnVerificationStateChangedCallbacks
                token);                 // ForceResendingToken from callbacks
    }

    public void submitData() {
        newPassword = newPassEt.getText().toString().trim().trim();
        retypeNewPassword = cnfmPassEt.getText().toString().trim();
        if (validatePassword()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                passwordResetResponse.setText("Please wait few seconds...");
                passwordResetResponse.setTextColor(Color.parseColor("#000000"));
                passwordResetResponse.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.RESET_PASSWORD_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("mobile", forgetPhone);
                builder.appendQueryParameter("password", newPassword);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");
                            String msg = jsonObject1.getString("msg");

                            if (success.equals("1")) {
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                            else  {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"This phone number is not registered", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            passwordResetResponse.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        passwordResetResponse.setVisibility(View.GONE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validatePassword() {
        if (this.newPassword.length() < 4 || this.newPassword.length() > 15) {
            this.newPassEt.setError("Password should be of 4 to 15 Digits/Characters");
            passwordResetResponse.setText("Enter New Password");
            passwordResetResponse.setTextColor(Color.parseColor("#ff0000"));
            passwordResetResponse.setVisibility(View.VISIBLE);
            return false;
        } else if (this.retypeNewPassword.isEmpty()) {
            this.cnfmPassEt.setError("Re-enter New Password");
            passwordResetResponse.setText("Re-enter New Password");
            passwordResetResponse.setTextColor(Color.parseColor("#ff0000"));
            passwordResetResponse.setVisibility(View.VISIBLE);
            return false;
        } else if (this.retypeNewPassword.equals(this.newPassword)) {
            passwordResetResponse.setVisibility(View.GONE);
            return true;
        } else {
            passwordResetResponse.setText("New Passwords don't match. Retry!");
            passwordResetResponse.setTextColor(Color.parseColor("#ff0000"));
            passwordResetResponse.setVisibility(View.VISIBLE);
            Toast.makeText(this, "New Passwords don't match. Retry!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
