package com.skycoder.pubg.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.skycoder.pubg.R;
import com.skycoder.pubg.adapter.RedeemCoinsAdapter;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.model.PayoutPojo;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedeemCoinsFragment extends androidx.fragment.app.Fragment{

    private View view;

    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout noContentLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<PayoutPojo> payoutPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private String user_id,username,password,firstname,lastname,email,mnumber;

    public RedeemCoinsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_redeem_coins, container, false);

        initSession();

        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_container);
        this.noContentLayout = (LinearLayout) view.findViewById(R.id.noContentLayout);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        this.payoutPojoList = new ArrayList();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            loadRedeemCoins();
        }else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void initSession() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);
    }

    private void loadRedeemCoins() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noContentLayout.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.GET_REDEEM_COINS_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //mShimmerViewContainer.stopShimmer();
                        //mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_REDEEM_COINS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noContentLayout.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_REDEEM_COINS(JSONArray response) {
        payoutPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            PayoutPojo payoutPojo = new PayoutPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                payoutPojo.setId(json.getString("id"));
                payoutPojo.setName(json.getString("name"));
                payoutPojo.setSubtitle(json.getString("subtitle"));
                payoutPojo.setMessage(json.getString("message"));
                payoutPojo.setAmount(json.getString("amount"));
                payoutPojo.setCoins(json.getString("coins"));
                payoutPojo.setImage(json.getString("image"));
                payoutPojo.setStatus(json.getString("status"));
                payoutPojo.setMode(json.getString("mode"));
                payoutPojo.setCurrency(json.getString("currency"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            payoutPojoList.add(payoutPojo);
        }
        if (!payoutPojoList.isEmpty()){
            adapter = new RedeemCoinsAdapter(payoutPojoList,getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noContentLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noContentLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        initSession();
        loadRedeemCoins();
        super.onResume();
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            this.mShimmerViewContainer.startShimmer();
        }
    }

    public void onPause() {
        this.mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

}
