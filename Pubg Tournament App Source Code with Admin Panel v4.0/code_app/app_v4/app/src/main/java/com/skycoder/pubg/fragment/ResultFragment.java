package com.skycoder.pubg.fragment;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import androidx.core.widget.NestedScrollView;
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
import com.skycoder.pubg.adapter.ResultAdapter;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.model.ResultPojo;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;

import static com.skycoder.pubg.activity.MainActivity.bottomView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends androidx.fragment.app.Fragment {

    private View view;

    private NestedScrollView nestedScrollView;
    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout noResults;
    

    private RecyclerView recyclerView;
    private LinearLayout upcomingLinearLayout;
    private RecyclerView.Adapter adapter;

    private ArrayList<ResultPojo> resultPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;
    
    private SessionManager session;
    private String id;
    private String username;
    private String password;


    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_result, container, false);
        session = new SessionManager(getActivity());

        initView();
        initListener();
        initSession();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            loadMatch();
        }else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
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

    private void initListener() {
        resultPojoList = new ArrayList<>();
    }

    private void initView() {
        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.upcomingLinearLayout = (LinearLayout) view.findViewById(R.id.upcomingLL);
        this.noResults = (LinearLayout) view.findViewById(R.id.noResultsLL);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
    }

    private void initSession() {
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
    }


    private void loadMatch() {
        recyclerView.setVisibility(View.GONE);
        noResults.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Uri.Builder builder = Uri.parse(Constant.RESULT_MATCH_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("user_id",id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noResults.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        resultPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            ResultPojo resultPojo = new ResultPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                resultPojo.setId(json.getString("id"));
                resultPojo.setTitle(json.getString("title"));
                resultPojo.setTime(json.getString("time"));
                resultPojo.setWinPrize(json.getInt("winPrize"));
                resultPojo.setImage(json.getString("image"));
                resultPojo.setPerKill(json.getInt("perKill"));
                resultPojo.setEntryFee(json.getInt("entryFee"));
                resultPojo.setEntryType(json.getString("entryType"));
                resultPojo.setVersion(json.getString("version"));
                resultPojo.setMap(json.getString("map"));
                resultPojo.setIsPrivateMatch(json.getString("isPrivateMatch"));
                resultPojo.setMatchType(json.getString("matchType"));
                resultPojo.setSponsoredBy(json.getString("sponsoredBy"));
                resultPojo.setSpectateURL(json.getString("spectateURL"));
                resultPojo.setMatchNotes(json.getString("matchNotes"));
                resultPojo.setMatch_status(json.getString("match_status"));
                resultPojo.setJoined_status(json.getString("joined_status"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resultPojoList.add(resultPojo);
        }
        if (!resultPojoList.isEmpty()){
            adapter = new ResultAdapter(resultPojoList,getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        initSession();
        loadMatch();
        super.onResume();
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            mShimmerViewContainer.startShimmer();
        }
    }

    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }
}
