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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.skycoder.pubg.R;
import com.skycoder.pubg.adapter.TransactionAdapter;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.model.TransactionPojo;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends androidx.fragment.app.Fragment {

    private View view;
    private ShimmerFrameLayout mShimmerViewContainer;

    private LinearLayout noTxnsLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<TransactionPojo> transactionPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private String id,name,firstname,lastname,email,mnumber,username,password;

    public TransactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transactions, container, false);

        initView();
        initPreference();

        this.transactionPojoList = new ArrayList();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            loadMyTransactions();
        }else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadMyTransactions() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noTxnsLayout.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.MY_TRANSACTIONS_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("user_id",id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //mShimmerViewContainer.stopShimmer();
                        //mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_TRANSACTIONS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noTxnsLayout.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_TRANSACTIONS(JSONArray response) {
        transactionPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            TransactionPojo transactionPojo = new TransactionPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                transactionPojo.setId(json.getString("id"));
                transactionPojo.setUser_id(json.getString("user_id"));
                transactionPojo.setOrder_id(json.getString("order_id"));
                transactionPojo.setPayment_id(json.getString("payment_id"));
                transactionPojo.setAmount(json.getString("amount"));
                transactionPojo.setRemark(json.getString("remark"));
                transactionPojo.setType(json.getString("type"));
                transactionPojo.setDate(json.getString("date"));
                transactionPojo.setWallet(json.getString("wallet"));
                transactionPojo.setCoins(json.getString("coins"));
                transactionPojo.setStatus(json.getString("status"));
                transactionPojo.setAccount_holder_name(json.getString("account_holder_name"));
                transactionPojo.setAccount_holder_id(json.getString("account_holder_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            transactionPojoList.add(transactionPojo);
        }
        if (!transactionPojoList.isEmpty()){
            adapter = new TransactionAdapter(transactionPojoList,getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noTxnsLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noTxnsLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initPreference() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);
    }

    private void initView() {
        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_container);
        this.noTxnsLayout = (LinearLayout) view.findViewById(R.id.noTxnLayout);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.txnListRecyclerView);
    }

    public void onResume() {
        initPreference();
        loadMyTransactions();
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
