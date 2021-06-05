package com.skycoder.pubg.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.skycoder.pubg.R;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;
import com.skycoder.pubg.utils.MySingleton;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText fname;
    private TextInputEditText lname;
    private TextInputEditText uname;
    private TextInputEditText mNumber;
    private TextInputEditText newPass;
    private TextInputEditText oldPass;
    private TextInputEditText retypeNewPass;
    private TextInputEditText birthdate;
    private TextInputEditText eMail;

    private RadioGroup Gender;
    private RadioButton female;
    private RadioButton male;

    private Button saveButton;
    private Button resetPassButton;

    private TextView successText;
    private TextView successTextPassword;
    private TextView verifyNumber;

    private String id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String oldPassword;
    private String newPassword;
    private String retypeNewPassword;
    private String mnumber;
    private  String otpValue = "";
    private String email;
    private String gender;
    private String dob;
    private String isVerified;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initToolbar();
        initView();
        initListener();
        initPreference();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initListener() {
        this.verifyNumber.setOnClickListener(this);
        this.birthdate.setOnClickListener(this);
        this.saveButton.setOnClickListener(this);
        this.resetPassButton.setOnClickListener(this);
    }

    private void initPreference() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);

        this.fname.setText(this.firstname);
        this.lname.setText(this.lastname);
        this.uname.setText(this.username);
        this.eMail.setText(this.email);
        this.mNumber.setText(this.mnumber);
        this.birthdate.setText(this.dob);

        try {
              if (this.gender.equals("m")) {
                  this.male.setChecked(true);
              }
              else if (this.gender.equals("f")) {
                  this.female.setChecked(true);
              }
        }catch (NullPointerException e){
              e.printStackTrace();
        }
        try {
            if (this.isVerified.equals("yes")) {
                this.mNumber.setEnabled(false);
                this.verifyNumber.setClickable(false);
                this.verifyNumber.setTextColor(ContextCompat.getColor(this, R.color.successGreen));
                this.verifyNumber.setText("VERIFIED");
            } else if (this.isVerified.equals("no")) {
                this.mNumber.setEnabled(true);
                this.verifyNumber.setClickable(true);
                this.verifyNumber.setTextColor(ContextCompat.getColor(this, R.color.errorRed));
                this.verifyNumber.setText("VERIFY");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void initView() {
        this.fname = (TextInputEditText) findViewById(R.id.firstname);
        this.lname = (TextInputEditText) findViewById(R.id.lastname);
        this.uname = (TextInputEditText) findViewById(R.id.username);
        this.eMail = (TextInputEditText) findViewById(R.id.email);
        this.mNumber = (TextInputEditText) findViewById(R.id.mobileNumber);
        this.birthdate = (TextInputEditText) findViewById(R.id.dob);
        this.Gender = (RadioGroup) findViewById(R.id.gender);
        this.male = (RadioButton) findViewById(R.id.male);
        this.female = (RadioButton) findViewById(R.id.female);
        this.saveButton = (Button) findViewById(R.id.saveBtn);
        this.oldPass = (TextInputEditText) findViewById(R.id.oldpass);
        this.newPass = (TextInputEditText) findViewById(R.id.newpass);
        this.retypeNewPass = (TextInputEditText) findViewById(R.id.retypeNewPass);
        this.resetPassButton = (Button) findViewById(R.id.changePassBtn);
        this.successText = (TextView) findViewById(R.id.messageView);
        this.successTextPassword = (TextView) findViewById(R.id.passwordMessageView);
        this.verifyNumber = (TextView) findViewById(R.id.verifyNo);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.dob){
            Calendar calendar = Calendar.getInstance();
            int date = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(dayOfMonth);
                    stringBuilder.append("/");
                    stringBuilder.append(month + 1);
                    stringBuilder.append("/");
                    stringBuilder.append(year);
                    birthdate.setText(stringBuilder.toString());
                }
            }, calendar.get(Calendar.YEAR), month, date);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
        else if (id==R.id.saveBtn){
            this.firstname = this.fname.getText().toString();
            this.lastname = this.lname.getText().toString();
            this.mnumber = this.mNumber.getText().toString();
            this.email = this.eMail.getText().toString();
            this.dob = this.birthdate.getText().toString();
            if (this.male.isChecked()) {
                this.gender = "m";
            } else if (this.female.isChecked()) {
                this.gender = "f";
            }
            submitProfileUpdateData();
        }
        else if (id==R.id.changePassBtn){
            this.oldPassword = this.oldPass.getText().toString();
            this.newPassword = this.newPass.getText().toString();
            this.retypeNewPassword = this.retypeNewPass.getText().toString();
            submitUpdatePasswordData();
        }
        else if (id==R.id.verifyNo){

        }
    }


    public void submitProfileUpdateData() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (validatefirstname() && validatelastname() && validateemail()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                Uri.Builder builder = Uri.parse(Constant.UPDATE_PROFILE_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("id", id);
                builder.appendQueryParameter("fname", firstname);
                builder.appendQueryParameter("lname", lastname);
                builder.appendQueryParameter("email", email);
                builder.appendQueryParameter("id", id);
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
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                session.createLoginSession(id,firstname,lastname,username,password,email,mnumber);
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                                try {
                                    Intent intent = new Intent(MyProfileActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }
                            else if (success.equals("2")) {
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validatefirstname() {
        if (this.firstname.isEmpty()) {
            this.fname.setError("Enter First Name");
            return false;
        } else if (this.firstname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            this.fname.setError("Enter valid first name");
            return false;
        }
    }

    public boolean validatelastname() {
        if (this.lastname.isEmpty()) {
            this.lname.setError("Enter Last Name");
            return false;
        } else if (this.lastname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            this.lname.setError("Enter valid last name");
            return false;
        }
    }

    public boolean validatemobilenumber() {
        if (this.mnumber.length() >= 5 && this.mnumber.length() <= 15) {
            return true;
        }
        this.mNumber.setError("Mobile Number should be of 5 to 15 Digits");
        return false;
    }

    public boolean isvalideemail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public boolean validateemail() {
        email = this.eMail.getText().toString();
        if (!email.isEmpty()) {
            if (isvalideemail(email)) {
                return true;
            }
        }
        this.eMail.setError("Enter Valid Email Address");
        return false;
    }

    public void submitUpdatePasswordData() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (validatePassword()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                Uri.Builder builder = Uri.parse(Constant.UPDATE_PROFILE_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("id", id);
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

                            if (success.equals("0")) {
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                session.createLoginSession(id,firstname,lastname,username,newPassword,email,mnumber);
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                                try {
                                    Intent intent = new Intent(MyProfileActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }
                            else if (success.equals("2")) {
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validatePassword() {
        if (this.oldPassword.isEmpty()) {
            this.oldPass.setError("Enter Password");
            return false;
        } else if (this.newPassword.length() <= 8 || this.newPassword.length() >= 20) {
            this.newPass.setError("Password should be of 8 to 20 Digits/Characters");
            return false;
        } else if (this.retypeNewPassword.isEmpty()) {
            this.retypeNewPass.setError("Re-enter New Password");
            return false;
        } else if (!this.oldPassword.equals(this.password)) {
            this.successTextPassword.setText("Old Password is Incorrect.");
            this.successTextPassword.setTextColor(SupportMenu.CATEGORY_MASK);
            this.successTextPassword.setVisibility(View.VISIBLE);
            return false;
        } else if (this.retypeNewPassword.equals(this.newPassword)) {
            return true;
        } else {
            this.successTextPassword.setText("New Passwords don't match. Retry!");
            this.successTextPassword.setTextColor(SupportMenu.CATEGORY_MASK);
            this.successTextPassword.setVisibility(View.VISIBLE);
            return false;
        }
    }


}
