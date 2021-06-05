package com.skycoder.pubg.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.skycoder.pubg.activity.AboutUsActivity;
import com.skycoder.pubg.activity.ContactUsActivity;
import com.skycoder.pubg.activity.MyProfileActivity;
import com.skycoder.pubg.activity.MyStatisticsActivity;
import com.skycoder.pubg.activity.MyWalletActivity;
import com.skycoder.pubg.activity.AnnouncementActivity;
import com.skycoder.pubg.activity.PrivacyPolicyActivity;
import com.skycoder.pubg.activity.TopPlayersActivity;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;
import com.skycoder.pubg.utils.MySingleton;

import static com.skycoder.pubg.activity.MainActivity.bottomView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private View view;
    private NestedScrollView nestedScrollView;

    private CardView aboutUs;
    private CardView customerSupport;
    private CardView privacyPolicy;
    private CardView importantUpdates;
    private CardView logOut;
    private CardView myProfile;
    private CardView myStatistics;
    private CardView myWallet;
    private CardView topPlayers;
    private CardView rateapp;
    private  CardView shareApp;

    private LinearLayout amountWonLayout;
    private LinearLayout totalKillsLayout;
    private LinearLayout matchesPlayedLayout;

    private TextView appVersion;
    private TextView myAmountWon;
    private TextView myBalance;
    private TextView myKills;
    private TextView myMatchesNumber;
    private TextView myname;
    private TextView myusername;

    private String id;
    private String firstname;
    private String lastname;
    private String name;
    private String email;
    private String mnumber;
    private String username;
    private String password;

    private SessionManager session;
    private String matches_played;
    private String total_amount_won;
    private String total_kills;

    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_account, container, false);

        initView();
        initListener();
        initSession();

        loadSummery();

        try {
            this.appVersion.setText("App Version : v"+BuildConfig.VERSION_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    // ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
                    if (scrollY > oldScrollY) {
                        //shadow_view.setVisibility(View.GONE);
                        bottomView.setVisibility(View.GONE);

                    }
                    if (scrollY < oldScrollY) {
                        //shadow_view.setVisibility(View.VISIBLE);
                        bottomView.setVisibility(View.VISIBLE);
                        //  Log.i(TAG, "Scroll UP");
                    }
                    if (scrollY == 0) {
                        //shadow_view.setVisibility(View.VISIBLE);
                        bottomView.setVisibility(View.VISIBLE);
                    }
                }


            });
        }

        return view;
    }


    private void initSession() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);

        name = firstname+" "+lastname;
        this.myname.setText(this.name);
        this.myusername.setText(this.username);
    }

    private void initListener() {
        this.matchesPlayedLayout.setOnClickListener(this);
        this.totalKillsLayout.setOnClickListener(this);
        this.amountWonLayout.setOnClickListener(this);
        this.myProfile.setOnClickListener(this);
        this.myWallet.setOnClickListener(this);
        this.myStatistics.setOnClickListener(this);
        this.topPlayers.setOnClickListener(this);
        this.importantUpdates.setOnClickListener(this);
        this.privacyPolicy.setOnClickListener(this);
        this.shareApp.setOnClickListener(this);
        this.logOut.setOnClickListener(this);
        this.aboutUs.setOnClickListener(this);
        this.customerSupport.setOnClickListener(this);
    }

    private void initView() {
        this.myname = (TextView) view.findViewById(R.id.name);
        this.myusername = (TextView) view.findViewById(R.id.myusername);
        this.myBalance = (TextView) view.findViewById(R.id.myBalance);
        this.myMatchesNumber = (TextView) view.findViewById(R.id.matchesPlayed);
        this.myKills = (TextView) view.findViewById(R.id.myKills);
        this.myAmountWon = (TextView) view.findViewById(R.id.amountWon);
        this.myProfile = (CardView) view.findViewById(R.id.profileCard);
        this.myWallet = (CardView) view.findViewById(R.id.myWalletCard);
        this.myStatistics = (CardView) view.findViewById(R.id.statsCard);
        this.topPlayers = (CardView) view.findViewById(R.id.topPlayersCard);
        this.importantUpdates = (CardView) view.findViewById(R.id.impUpdates);
        this.privacyPolicy = (CardView) view.findViewById(R.id.impPrivacy);
        this.aboutUs = (CardView) view.findViewById(R.id.aboutUsCard);
        this.shareApp = (CardView) view.findViewById(R.id.shareCard);
        this.logOut = (CardView) view.findViewById(R.id.logOutCard);
        this.appVersion = (TextView) view.findViewById(R.id.appVersion);
        this.customerSupport = (CardView) view.findViewById(R.id.customerSupportCard);
        this.matchesPlayedLayout = (LinearLayout) view.findViewById(R.id.matchesPlayedLL);
        this.totalKillsLayout = (LinearLayout) view.findViewById(R.id.totalKillsLL);
        this.amountWonLayout = (LinearLayout) view.findViewById(R.id.amountWonLL);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
    }

    private void loadSummery() {
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            Uri.Builder builder = Uri.parse(Constant.MY_SUMMARY_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("user_id",id);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            matches_played = jsonObject1.getString("maches_played");
                            total_kills = jsonObject1.getString("total_kills");
                            total_amount_won = jsonObject1.getString("amount_won");

                            if (matches_played == null|| matches_played.equals("null") || matches_played.equals("")){
                                myMatchesNumber.setText("0");
                            }
                            else {
                                myMatchesNumber.setText(matches_played);
                            }
                            if (total_kills == null || total_kills.equals("null") || total_kills.equals("")){
                                myKills.setText("0");
                            }
                            else {
                                myKills.setText(total_kills);
                            }
                            if (total_amount_won == null || total_amount_won.equals("null") || total_amount_won.equals("")){
                                myAmountWon.setText("0");
                            }
                            else {
                                myAmountWon.setText(total_amount_won);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(),"Something went wrong", Toast.LENGTH_LONG).show();
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
            MySingleton.getInstance(getActivity()).addToRequestque(request);
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id==R.id.profileCard){
            this.startActivity(new Intent(this.getActivity(), MyProfileActivity.class));
        }
        else if (id==R.id.myWalletCard){
            this.startActivity(new Intent(this.getActivity(), MyWalletActivity.class));
        }
        else if (id==R.id.statsCard){
            this.startActivity(new Intent(this.getActivity(), MyStatisticsActivity.class));
        }
        else if (id==R.id.topPlayersCard){
            this.startActivity(new Intent(this.getActivity(), TopPlayersActivity.class));
        }
        else if (id==R.id.impUpdates){
            this.startActivity(new Intent(this.getActivity(), AnnouncementActivity.class));
        }
        else if (id==R.id.impPrivacy){
            this.startActivity(new Intent(this.getActivity(), PrivacyPolicyActivity.class));
        }
        else if (id==R.id.aboutUsCard){
            this.startActivity(new Intent(this.getActivity(), AboutUsActivity.class));
        }
        else if (id==R.id.shareCard){
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType("text/plain");
            String string = this.getString(R.string.shareContent)+username;
            shareIntent.putExtra("android.intent.extra.SUBJECT", this.getString(R.string.shareSub));
            shareIntent.putExtra("android.intent.extra.TEXT", string);
            this.startActivity(Intent.createChooser(shareIntent, "Share using"));
        }
        else if (id==R.id.customerSupportCard){
            this.startActivity(new Intent(this.getActivity(), ContactUsActivity.class));
        }
        else if (id==R.id.logOutCard){
            session.logoutUser();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        initSession();
        loadSummery();
    }


}
