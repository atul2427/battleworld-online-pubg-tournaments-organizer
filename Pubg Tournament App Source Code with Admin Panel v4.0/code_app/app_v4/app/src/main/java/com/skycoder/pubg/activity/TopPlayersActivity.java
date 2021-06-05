package com.skycoder.pubg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
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
import com.skycoder.pubg.adapter.TopPlayersAdapter;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.model.TopPlayersPojo;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;

public class TopPlayersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<TopPlayersPojo> topPlayersPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private String id;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);

        initToolbar();
        initView();
        initSession();

        topPlayersPojoList = new ArrayList<>();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadTopPlayers();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadTopPlayers() {
        Uri.Builder builder = Uri.parse(Constant.TOP_PLAYERS_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(JSONArray response) {
        topPlayersPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            TopPlayersPojo topPlayersPojo = new TopPlayersPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                //topPlayersPojo.setId(json.getString("id"));
                topPlayersPojo.setPubg_id(json.getString("pubg_id"));
                topPlayersPojo.setPrize(json.getInt("prize"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            topPlayersPojoList.add(topPlayersPojo);
        }
        if (!topPlayersPojoList.isEmpty()){
            adapter = new TopPlayersAdapter(topPlayersPojoList,getApplicationContext());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }
    }


    private void initView() {
        this.recyclerView = (RecyclerView) findViewById(R.id.topPlayersListRecyclerView);
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "Top Players");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}
