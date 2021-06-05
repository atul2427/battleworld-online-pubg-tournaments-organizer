package com.skycoder.pubg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.skycoder.pubg.R;
import com.skycoder.pubg.adapter.MyStatisticsAdapter;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.model.StatisticsPojo;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;

public class MyStatisticsActivity extends AppCompatActivity {

    private LinearLayout noStats;
    private LinearLayout stats;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<StatisticsPojo> statisticsPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private String id;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics);
        session = new SessionManager(getApplicationContext());

        initToolbar();
        initView();
        initSession();

        statisticsPojoList = new ArrayList<>();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadMyStatistics();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSession() {
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
    }

    private void initView() {
        this.recyclerView = (RecyclerView) findViewById(R.id.matchListRecyclerView);
        this.stats = (LinearLayout) findViewById(R.id.statsLL);
        this.noStats = (LinearLayout) findViewById(R.id.noStatsLL);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "My Statistics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadMyStatistics() {
        stats.setVisibility(View.VISIBLE);
        noStats.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.MY_STATISTICS_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("user_id", id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_STATISTICS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        stats.setVisibility(View.GONE);
                        noStats.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_STATISTICS(JSONArray response) {
        statisticsPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            StatisticsPojo statisticsPojo = new StatisticsPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                statisticsPojo.setId(json.getString("id"));
                statisticsPojo.setTitle(json.getString("title"));
                statisticsPojo.setTime(json.getString("time"));
                statisticsPojo.setEntryFee(json.getInt("entryFee"));
                statisticsPojo.setPrize(json.getInt("prize"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            statisticsPojoList.add(statisticsPojo);
        }
        if (!statisticsPojoList.isEmpty()){
            adapter = new MyStatisticsAdapter(statisticsPojoList,getApplicationContext());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            stats.setVisibility(View.VISIBLE);
            noStats.setVisibility(View.GONE);
        }
        else {
            stats.setVisibility(View.GONE);
            noStats.setVisibility(View.VISIBLE);
        }
    }



}
