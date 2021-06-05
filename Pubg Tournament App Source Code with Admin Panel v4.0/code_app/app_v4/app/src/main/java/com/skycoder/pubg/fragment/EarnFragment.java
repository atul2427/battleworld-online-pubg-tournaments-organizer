package com.skycoder.pubg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import com.skycoder.pubg.R;
import com.skycoder.pubg.activity.MainActivity;
import com.skycoder.pubg.activity.RefereEarnActivity;
import com.skycoder.pubg.activity.RewardEarnActivity;

public class EarnFragment extends Fragment implements View.OnClickListener {

    private View view;

    private LinearLayout referandearncard;
    private LinearLayout watchandearncard;
    private LinearLayout playandearncard;

    public EarnFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_earn, container, false);

        referandearncard = view.findViewById(R.id.referandearncard);
        watchandearncard = view.findViewById(R.id.watchandearncard);
        playandearncard = view.findViewById(R.id.playandearncard);

        referandearncard.setOnClickListener(this);
        watchandearncard.setOnClickListener(this);
        playandearncard.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.referandearncard){
            this.startActivity(new Intent(this.getActivity(), RefereEarnActivity.class));
        }
        else if (v.getId()==R.id.watchandearncard){
            this.startActivity(new Intent(this.getActivity(), RewardEarnActivity.class));
        }
        else if (v.getId()==R.id.playandearncard){
            getActivity().recreate();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new HomeFragment()).commit();
        }
    }

}
