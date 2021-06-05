package com.skycoder.pubg.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
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
import com.skycoder.pubg.utils.ExtraOperations;
import com.skycoder.pubg.utils.MySingleton;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PhoneAuth";
    private ProgressBar progressBar;
    private Dialog dialog;
    private CountDownTimer countDownTimer;

    private String eMail;
    private TextInputEditText email;
    private String firstname;
    private TextInputEditText fname;
    private String lastname;
    private TextInputEditText lname;
    private String message;
    private TextInputEditText cCode;
    private String countryCode;
    private TextInputEditText mnumber;
    private String mobileNumber;
    private String password;
    private String rePassword;
    private TextInputEditText pcode;
    private String promocode;
    private TextInputEditText pword;
    private TextInputEditText rePword;
    private Button register;
    private String result;
    private Button signin;
    private String status = "";
    private String uname;
    private TextInputEditText username;
    private String strCodePhone;
    private TextView txtAgreeTo;
    private CheckBox chkAgreeTo;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;

    private TextInputEditText otpEditText;
    private TextView resendOTP, timerOTP, textError;
    private String strOTP;
    private String strDeviceID;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        strDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        initView();
        initListener();
    }

    private void initListener() {
        this.signin.setOnClickListener(this);
        this.register.setOnClickListener(this);
        this.txtAgreeTo.setOnClickListener(this);
    }

    private void initView() {
        this.signin = (Button) findViewById(R.id.loginFromRegister);
        this.fname = (TextInputEditText) findViewById(R.id.firstname);
        this.lname = (TextInputEditText) findViewById(R.id.lastname);
        this.username = (TextInputEditText) findViewById(R.id.username);
        this.email = (TextInputEditText) findViewById(R.id.email);
        this.cCode = (TextInputEditText) findViewById(R.id.countryCode);
        this.mnumber = (TextInputEditText) findViewById(R.id.mobileNumber);
        this.register = (Button) findViewById(R.id.registerBtn);
        this.pword = (TextInputEditText) findViewById(R.id.password);
        this.rePword = (TextInputEditText) findViewById(R.id.rePassword);
        this.pcode = (TextInputEditText) findViewById(R.id.promocode);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.txtAgreeTo = (TextView) findViewById(R.id.txtAgreeTo);
        this.chkAgreeTo = (CheckBox) findViewById(R.id.chkAgreeTo);
    }

    private void verifyData() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        this.firstname = this.fname.getText().toString().trim();
        this.lastname = this.lname.getText().toString().trim();
        this.uname  = this.username.getText().toString().trim();
        this.eMail = this.email.getText().toString().trim();
        this.mobileNumber = this.mnumber.getText().toString().trim();
        this.countryCode = this.cCode.getText().toString().trim();
        this.password = this.pword.getText().toString().trim();
        this.rePassword = this.rePword.getText().toString().trim();
        this.promocode = this.pcode.getText().toString().trim();
        strCodePhone=countryCode+mobileNumber;

        if (validatefirstname() && validatelastname() && validateusername() && validateemail() && validatecountrycode() && validatemobilenumber() && validatepassword() && validaterepassword()) {
            if (countryCode.length() <= 0) {
                Toast.makeText(RegisterActivity.this,"Please select valid country code!", Toast.LENGTH_LONG).show();
            }
            else if (!password.equals(rePassword)) {
                Toast.makeText(this, "Password does't match", Toast.LENGTH_SHORT).show();
            }
            else if (!chkAgreeTo.isChecked()){
                Toast.makeText(RegisterActivity.this,"Please accept terms & condition.", Toast.LENGTH_LONG).show();
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                setUpVerificationCallbacks();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        strCodePhone,  // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        RegisterActivity.this,               // Activity (for callback binding)
                        verificationCallbacks);
            }
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
        if (new ExtraOperations().haveNetworkConnection(this)) {
            if (promocode.length() >= 2){
                Uri.Builder builder = Uri.parse(Constant.USER_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("fname",firstname);
                builder.appendQueryParameter("lname",lastname);
                builder.appendQueryParameter("username",uname);
                builder.appendQueryParameter("password",password);
                builder.appendQueryParameter("email",eMail);
                builder.appendQueryParameter("code",countryCode);
                builder.appendQueryParameter("mobile",mobileNumber);
                builder.appendQueryParameter("referer",promocode);
                builder.appendQueryParameter("device_id",strDeviceID);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");
                            String msg = jsonObject1.getString("msg");

                            if (success.equals("0")) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                            else if (success.equals("2")){
                                dialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this, ""+msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
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
            }
            else {
                Uri.Builder builder = Uri.parse(Constant.USER_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("fname",firstname);
                builder.appendQueryParameter("lname",lastname);
                builder.appendQueryParameter("username",uname);
                builder.appendQueryParameter("password",password);
                builder.appendQueryParameter("email",eMail);
                builder.appendQueryParameter("code",countryCode);
                builder.appendQueryParameter("mobile",mobileNumber);
                builder.appendQueryParameter("device_id",strDeviceID);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");
                            String msg = jsonObject1.getString("msg");

                            if (success.equals("0")) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                dialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //create.dismiss();
                        error.printStackTrace();
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
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validatefirstname() {
        if (this.firstname.isEmpty()) {
            this.fname.setError("Please enter valid first name.");
            return false;
        } else if (this.firstname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            this.fname.setError("Special character are not allow in first name.");
            return false;
        }
    }

    public boolean validatelastname() {
        if (this.lastname.isEmpty()) {
            this.lname.setError("Please enter valid last name.");
            return false;
        } else if (this.lastname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            this.lname.setError("Special character are not allow in last name.");
            return false;
        }
    }

    public boolean isvalideemail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public boolean validateemail() {
        if (!this.eMail.isEmpty()) {
            if (isvalideemail(this.eMail)) {
                return true;
            }
        }
        this.email.setError("Please enter valid email address.");
        return false;
    }

    public boolean validateusername() {
        if (!this.uname.isEmpty()) {
            return true;
        } else {
            this.username.setError("Please enter valid Username.");
            return false;
        }
    }

    public boolean validatepromocode() {
        if (!this.promocode.isEmpty()) {
            return true;
        }
        this.pcode.setError("Please enter valid promo code.");
        return false;
    }

    public boolean validatepassword() {
        if (this.password.isEmpty()) {
            this.pword.setError("Please enter a valid password.");
        }
        else if (this.password.length() >= 8 && this.password.length() <= 20) {
            return true;
        }
        this.pword.setError("Password length must be 8-20 characters.");
        return false;
    }

    public boolean validaterepassword() {
        if (!this.rePassword.isEmpty()) {
            return true;
        }
        this.rePword.setError("Please re-Enter password.");
        return false;
    }

    public boolean validatemobilenumber() {
        if (this.mobileNumber.isEmpty()) {
            this.mnumber.setError("Please enter a valid mobile number.");
        }
        else if (this.mobileNumber.length() >= 5 && this.mobileNumber.length() <= 15) {
            return true;
        }
        this.mnumber.setError("Mobile number length must be 5-15 digits.");
        return false;
    }

    public boolean validatecountrycode() {
        if (this.countryCode.length() >= 1) {
            return true;
        }
        this.cCode.setError("Please pick your country code.");
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerBtn){
            verifyData();
        }
        else if (v.getId() == R.id.loginFromRegister){
            onBackPressed();
        }
        else if (v.getId() == R.id.txtAgreeTo){
            Intent intent = new Intent(RegisterActivity.this,TermsConditionsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void otpAlertDialog(final Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verified_otp);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        otpEditText = (TextInputEditText) dialog.findViewById(R.id.otpEditText);
        resendOTP = (TextView) dialog.findViewById(R.id.resendOTP);
        timerOTP = (TextView) dialog.findViewById(R.id.timerOTP);
        textError = (TextView) dialog.findViewById(R.id.textError);

        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        Button next = (Button) dialog.findViewById(R.id.next);

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

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                strOTP = otpEditText.getText().toString().trim();
                verifyCode(strOTP);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void setUpVerificationCallbacks() {
        otpAlertDialog(RegisterActivity.this);
        progressBar.setVisibility(View.GONE);
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "SMS Quota exceeded.");
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
                resendToken = token;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    textError.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    submitData();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("The verification code entered was invalid");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void verifyCode(String strOTP) {
        try {
            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (strOTP.length() == 6){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, strOTP);
            signInWithPhoneAuthCredential(credential);
        }
        else {
            textError.setVisibility(View.VISIBLE);
            textError.setText("The verification code entered was invalid");
            progressBar.setVisibility(View.GONE);
        }
    }



}
