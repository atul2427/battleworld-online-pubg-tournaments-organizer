package com.skycoder.pubg.activity;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.skycoder.pubg.BuildConfig;
import com.skycoder.pubg.R;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.fragment.EarnFragment;
import com.skycoder.pubg.fragment.HomeFragment;
import com.skycoder.pubg.fragment.MyAccountFragment;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;
import com.skycoder.pubg.utils.MySingleton;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Fragment fragment;
    public LinearLayout homeLayout;
    public LinearLayout myContestLayout;
    public LinearLayout myAccountLayout;

    private Toolbar toolbar;
    private LinearLayout toolbalance;
    private FrameLayout newNotiIcon;

    public static LinearLayout bottomView;
    public static TextView toolwallet;
    public static TextView cart_badge;

    private SessionManager session;
    private String force_update, whats_new, update_date, latest_version_name, update_url;
    private int balance,won,bonus;
    private String id, isBlocked;


    public boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initSession();

        initViews();
        initListeners();

        loadUpdate();
        loadProfile();

        if (savedInstanceState ==  null){
            addHomeFragment();
        }


    }

    private void loadUpdate() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.UPDATE_APP_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            force_update = jsonObject1.getString("force_update");
                            whats_new = jsonObject1.getString("whats_new");
                            update_date = jsonObject1.getString("update_date");
                            latest_version_name = jsonObject1.getString("latest_version_name");
                            update_url = jsonObject1.getString("update_url");

                            try {
                                if (BuildConfig.VERSION_CODE < Integer.parseInt(latest_version_name)) {
                                    if (force_update.equals("1")) {
                                        Intent intent = new Intent(MainActivity.this, UpdateAppActivity.class);
                                        intent.putExtra("forceUpdate", force_update);
                                        intent.putExtra("whatsNew", whats_new);
                                        intent.putExtra("updateDate", update_date);
                                        intent.putExtra("latestVersionName", latest_version_name);
                                        intent.putExtra("updateURL", update_url);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else if (force_update.equals("0")) {
                                        Intent intent = new Intent(MainActivity.this, UpdateAppActivity.class);
                                        intent.putExtra("forceUpdate", force_update);
                                        intent.putExtra("whatsNew", whats_new);
                                        intent.putExtra("updateDate", update_date);
                                        intent.putExtra("latestVersionName", latest_version_name);
                                        intent.putExtra("updateURL", update_url);
                                        startActivity(intent);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfile() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.PROFILE_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("id", id);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            balance = jsonObject1.getInt("cur_balance");
                            won = jsonObject1.getInt("won_balance");
                            bonus = jsonObject1.getInt("bonus_balance");
                            isBlocked = jsonObject1.getString("status");
                            toolwallet.setText(String.valueOf(balance));
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.my_wallet_appbar);
        toolbalance = (LinearLayout) toolbar.findViewById(R.id.toolbalance);
        toolwallet = (TextView) toolbar.findViewById(R.id.toolwallet);
        newNotiIcon = (FrameLayout) toolbar.findViewById(R.id.newNotiIcon);
        cart_badge = (TextView) toolbar.findViewById(R.id.cart_badge);

        toolbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyWalletActivity.class);
                startActivity(intent);
            }
        });

        newNotiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AnnouncementActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
    }

    private void initViews() {
        this.bottomView = (LinearLayout) findViewById(R.id.bottom_navigation);
        this.myContestLayout = (LinearLayout) this.bottomView.findViewById(R.id.myContestLayout);
        this.homeLayout = (LinearLayout) this.bottomView.findViewById(R.id.homeLayout);
        this.myAccountLayout = (LinearLayout) this.bottomView.findViewById(R.id.myAccountLayout);
    }

    private void initListeners() {
        this.myContestLayout.setTag("Earn");
        this.homeLayout.setTag("Home");
        this.myAccountLayout.setTag("Me");

        this.myContestLayout.setOnClickListener(this);
        this.homeLayout.setOnClickListener(this);
        this.myAccountLayout.setOnClickListener(this);
    }

    private void addHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, homeFragment)
                .commit();
    }

    public void loadFragment(String str) {
        if (str.equalsIgnoreCase("Earn")) {
            this.fragment = new EarnFragment();
        } else if (str.equalsIgnoreCase("Home")) {
            this.fragment = new HomeFragment();
        } else if (str.equalsIgnoreCase("Me")) {
            this.fragment = new MyAccountFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, this.fragment).commit();
    }

    public void onClick (View view){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Log.e("In HomeAct", "In OnClick");
        ImageView imageView1 = (ImageView) this.bottomView.findViewById(R.id.contestIv);
        ImageView imageView2 = (ImageView) this.bottomView.findViewById(R.id.homeIv);
        ImageView imageView3 = (ImageView) this.bottomView.findViewById(R.id.accountIv);

        TextView textView1 = (TextView) this.bottomView.findViewById(R.id.contestTv);
        TextView textView2 = (TextView) this.bottomView.findViewById(R.id.homeTv);
        TextView textView3 = (TextView) this.bottomView.findViewById(R.id.accountTv);

        switch (view.getTag().toString()) {
            case "Earn":
                imageView1.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationActiveColored));
                imageView2.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                imageView3.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));

                textView1.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationActiveColored));
                textView2.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                textView3.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                break;
            case "Home":
                imageView1.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                imageView2.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationActiveColored));
                imageView3.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));

                textView1.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                textView2.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationActiveColored));
                textView3.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                break;
            case "Me":
                imageView1.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                imageView2.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                imageView3.setColorFilter(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationActiveColored));

                textView1.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                textView2.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationInactiveColored));
                textView3.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorBottomNavigationActiveColored));
                break;
        }
        loadFragment(view.getTag().toString());
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initSession();
        loadProfile();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) { finish(); return; }
        doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() { @Override public void run() { doubleBackToExitPressedOnce = false; }}, 1500);

    }


}
