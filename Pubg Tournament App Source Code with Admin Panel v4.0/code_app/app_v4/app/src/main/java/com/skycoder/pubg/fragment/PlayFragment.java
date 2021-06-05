package com.skycoder.pubg.fragment;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.skycoder.pubg.R;
import com.skycoder.pubg.adapter.AnnouncementAdapter;
import com.skycoder.pubg.adapter.PlayAdapter;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.common.Constant;
import com.skycoder.pubg.model.NotificationPojo;
import com.skycoder.pubg.model.PlayPojo;
import com.skycoder.pubg.session.SessionManager;
import com.skycoder.pubg.utils.ExtraOperations;
import com.skycoder.pubg.views.NonSwipeableViewPager;

import static com.skycoder.pubg.activity.MainActivity.bottomView;
import static com.skycoder.pubg.activity.MainActivity.cart_badge;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends androidx.fragment.app.Fragment {

    private View view;

    private ShimmerFrameLayout mShimmerViewContainer;
    private LinearLayout noMatchesLL;

    private NestedScrollView nestedScrollView;
    private CardView notificationCard;

    private RecyclerView recyclerView;
    private LinearLayout upcomingLinearLayout;
    private LinearLayout participatedLinearLayout;
    private RecyclerView participatedRecyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<PlayPojo> playPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private String id;
    private String username;
    private String password;

    private RequestQueue announceRequestQueue;
    private JsonArrayRequest announceJsonArrayRequest ;

    private Timer timer;
    private int page = 0;
    private NonSwipeableViewPager viewPager;
    private AnnouncementAdapter announceAdapter;
    private List<NotificationPojo> announceList;

    public PlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play, container, false);
        session = new SessionManager(getActivity());

        initView();
        initSession();
        initListener();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.participatedRecyclerView.setHasFixedSize(true);
        this.participatedRecyclerView.setNestedScrollingEnabled(false);
        this.participatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            loadAnnouncement();
        }else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

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
        playPojoList = new ArrayList<>();
        announceList= new ArrayList<>();
    }

    private void loadAnnouncement() {
        announceList.clear();
        Uri.Builder builder = Uri.parse(Constant.NOTIFICATION_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        announceJsonArrayRequest = new JsonArrayRequest(Request.Method.GET, builder.toString(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        NotificationPojo notificationPojo = new NotificationPojo();

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            notificationPojo.setTitle(jsonObject.getString("title"));

                            notificationCard.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            notificationCard.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            notificationCard.setVisibility(View.GONE);
                        }
                        announceList.add(notificationPojo);
                    }
                    announceAdapter = new AnnouncementAdapter(announceList, getActivity());
                    announceAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(announceAdapter);
                    cart_badge.setText(String.valueOf(announceAdapter.getCount()));

                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }
                        @Override
                        public void onPageSelected(int position) {
                            try {
                                page = position;
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                    try {
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 5000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                viewPager.setVisibility(View.GONE);
                notificationCard.setVisibility(View.GONE);
                cart_badge.setText("0");
                error.printStackTrace();
            }
        });
        announceRequestQueue = Volley.newRequestQueue(getActivity());
        announceJsonArrayRequest.setShouldCache(false);
        announceRequestQueue.getCache().clear();
        announceRequestQueue.add(announceJsonArrayRequest);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (announceAdapter.getCount() == page) {
                            page = 0;
                        } else {
                            page++;
                        }
                        viewPager.setCurrentItem(page, true);
                    }
                });
            }
        }
    }

    private void loadMatch() {
        recyclerView.setVisibility(View.GONE);
        noMatchesLL.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Uri.Builder builder = Uri.parse(Constant.PLAY_MATCH_URL).buildUpon();
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
                        noMatchesLL.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        playPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            PlayPojo playPojo = new PlayPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                playPojo.setId(json.getString("id"));
                playPojo.setTitle(json.getString("title"));
                playPojo.setTime(json.getString("time"));
                playPojo.setWinPrize(json.getInt("winPrize"));
                playPojo.setImage(json.getString("image"));
                playPojo.setPerKill(json.getInt("perKill"));
                playPojo.setEntryFee(json.getInt("entryFee"));
                playPojo.setEntryType(json.getString("entryType"));
                playPojo.setVersion(json.getString("version"));
                playPojo.setMap(json.getString("map"));
                playPojo.setIsPrivateMatch(json.getString("isPrivateMatch"));
                playPojo.setMatchType(json.getString("matchType"));
                playPojo.setSponsoredBy(json.getString("sponsoredBy"));
                playPojo.setRules(json.getString("rules"));
                playPojo.setMatch_status(json.getString("match_status"));
                playPojo.setMatch_id(json.getString("match_id"));
                playPojo.setRoom_size(json.getInt("room_size"));
                playPojo.setTotal_joined(json.getInt("total_joined"));
                playPojo.setJoined_status(json.getString("joined_status"));
                playPojo.setUser_joined(json.getString("user_joined"));
                playPojo.setIs_cancel(json.getString("is_cancel"));
                playPojo.setCancel_reason(json.getString("cancel_reason"));
                playPojo.setPrivate_match_code(json.getString("private_match_code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            playPojoList.add(playPojo);
        }
        if (!playPojoList.isEmpty()){
            adapter = new PlayAdapter(playPojoList,getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noMatchesLL.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noMatchesLL.setVisibility(View.VISIBLE);
        }
    }


    private void initView() {
        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.participatedRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewParticipated);
        this.upcomingLinearLayout = (LinearLayout) view.findViewById(R.id.upcomingLL);
        this.participatedLinearLayout = (LinearLayout) view.findViewById(R.id.participatedLL);
        this.viewPager = (NonSwipeableViewPager) view.findViewById(R.id.viewPager);
        this.noMatchesLL = (LinearLayout) view.findViewById(R.id.noMatchesLL);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        notificationCard = (CardView) view.findViewById(R.id.notificationCard);
    }

    private void initSession() {
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
    }

    public void onResume() {
        super.onResume();
        initSession();
        loadMatch();
        try {
            if (timer != null) {
                timer.cancel();
                timer = new Timer();
                timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 5000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            mShimmerViewContainer.startShimmer();
        }
    }

    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
