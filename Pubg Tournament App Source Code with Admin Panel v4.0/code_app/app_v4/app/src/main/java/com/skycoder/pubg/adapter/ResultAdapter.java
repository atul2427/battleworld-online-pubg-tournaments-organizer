package com.skycoder.pubg.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.skycoder.pubg.R;
import com.skycoder.pubg.activity.ResultDetailsActivity;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.model.ResultPojo;
import com.skycoder.pubg.utils.ExtraOperations;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private Context context;
    private List<ResultPojo> resultPojoList;
    private String left_spots;

    public ResultAdapter(List<ResultPojo> resultPojoList, Context context){
        super();
        this.resultPojoList = resultPojoList;
        this.context = context;
    }

    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result, parent, false);
        ResultAdapter.ViewHolder viewHolder = new ResultAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ResultAdapter.ViewHolder holder, int position) {
        final ResultPojo resultPojo =  resultPojoList.get(position);

        holder.title.setText(resultPojo.getTitle());
        TextView textView = holder.timedate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time: ");
        stringBuilder.append(resultPojo.getTime());
        textView.setText(stringBuilder.toString());
        holder.prize.setText(String.valueOf(resultPojo.getWinPrize()));
        holder.perkill.setText(String.valueOf(resultPojo.getPerKill()));
        String matchType = resultPojo.getEntryType();
        if (matchType.equals("Free")) {
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
            holder.sponsorTextArea.setVisibility(View.GONE);
        } else if (matchType.equals("Sponsored")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Sponsored by ");
            stringBuilder.append(resultPojo.getSponsoredBy());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else if (matchType.equals("Giveaway")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Giveaway by ");
            stringBuilder.append(resultPojo.getSponsoredBy());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            holder.fee.setText(String.valueOf(resultPojo.getEntryFee()));
            holder.sponsorTextArea.setVisibility(View.GONE);
        }
        holder.type.setText(resultPojo.getMatchType());
        holder.version.setText(resultPojo.getVersion());
        holder.map.setText(resultPojo.getMap());

        if (!resultPojo.getImage().isEmpty()) {
            holder.topImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+resultPojo.getImage()).resize(720,480).into(holder.topImage);
        }
        else {
            holder.topImage.setVisibility(View.GONE);
        }

        try {
            if (!resultPojo.getJoined_status().equals("null")) {
                holder.joinBtn.setText("Joined");
            } else {
                holder.joinBtn.setText("Not Joined");
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.watchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (!resultPojo.getSpectateURL().startsWith("http://") && !resultPojo.getSpectateURL().startsWith("https://")){
                        context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://"+resultPojo.getSpectateURL())));
                    }
                    else {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(resultPojo.getSpectateURL())));
                    }
                }
                catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        });
        
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (new ExtraOperations().haveNetworkConnection(context)) {
                    Intent intent = new Intent(context, ResultDetailsActivity.class);
                    intent.putExtra("EntryFee_KEY", String.valueOf(resultPojo.getEntryFee()));
                    intent.putExtra("ID_KEY", resultPojo.getId());
                    intent.putExtra("Map_KEY", resultPojo.getMap());
                    intent.putExtra("Notes_KEY", resultPojo.getMatchNotes());
                    intent.putExtra("PerKill_KEY", String.valueOf(resultPojo.getPerKill()));
                    intent.putExtra("StartTime_KEY", resultPojo.getTime());
                    intent.putExtra("Match_Status_KEY", resultPojo.getMatch_status());
                    intent.putExtra("Title_KEY", resultPojo.getTitle());
                    intent.putExtra("TopImage_KEY", resultPojo.getImage());
                    intent.putExtra("Entry_Type_KEY", resultPojo.getEntryType());
                    intent.putExtra("Match_Type_KEY", resultPojo.getMatchType());
                    intent.putExtra("Version_KEY", resultPojo.getVersion());
                    intent.putExtra("WinPrize_KEY", String.valueOf(resultPojo.getWinPrize()));
                    intent.putExtra("Private_Status_KEY", resultPojo.getIsPrivateMatch());
                    intent.putExtra("SpectateURL_KEY", resultPojo.getSpectateURL());
                    intent.putExtra("JOINED_STATUS_KEY", resultPojo.getJoined_status());
                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return resultPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView fee;
        ImageView img;
        Button joinBtn;
        TextView map;
        TextView perkill;
        LinearLayout playbtnLL;
        TextView prize;
        TextView size;
        TextView sponsorText;
        RelativeLayout sponsorTextArea;
        TextView spot;
        TextView timedate;
        TextView title;
        ImageView topImage;
        TextView type;
        TextView version;
        Button watchBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            this.cardView = (CardView) itemView.findViewById(R.id.mainCard);
            this.img = (ImageView) itemView.findViewById(R.id.img);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.timedate = (TextView) itemView.findViewById(R.id.timedate);
            this.prize = (TextView) itemView.findViewById(R.id.winPrize);
            this.perkill = (TextView) itemView.findViewById(R.id.perKill);
            this.fee = (TextView) itemView.findViewById(R.id.entryFee);
            this.sponsorTextArea = (RelativeLayout) itemView.findViewById(R.id.sponsorTextArea);
            this.sponsorText = (TextView) itemView.findViewById(R.id.sponsorText);
            this.type = (TextView) itemView.findViewById(R.id.matchType);
            this.version = (TextView) itemView.findViewById(R.id.matchVersion);
            this.map = (TextView) itemView.findViewById(R.id.matchMap);
            this.topImage = (ImageView) itemView.findViewById(R.id.mainTopBanner);
            this.watchBtn = (Button) itemView.findViewById(R.id.watchMatchButton);
            this.joinBtn = (Button) itemView.findViewById(R.id.joinedButton);
        }

    }
}
