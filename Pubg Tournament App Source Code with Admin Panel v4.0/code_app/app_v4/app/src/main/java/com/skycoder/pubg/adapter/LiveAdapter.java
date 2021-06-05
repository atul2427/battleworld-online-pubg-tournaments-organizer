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
import com.skycoder.pubg.activity.LiveDetailsActivity;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.model.LivePojo;
import com.skycoder.pubg.utils.ExtraOperations;


public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

    private Context context;
    private List<LivePojo> livePojoList;
    private String left_spots;

    public LiveAdapter(List<LivePojo> livePojoList, Context context){
        super();
        this.livePojoList = livePojoList;
        this.context = context;
    }

    @Override
    public LiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_live, parent, false);
        LiveAdapter.ViewHolder viewHolder = new LiveAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LiveAdapter.ViewHolder holder, int position) {
        final LivePojo livePojo =  livePojoList.get(position);

        holder.title.setText(livePojo.getTitle());
        TextView textView = holder.timedate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time: ");
        stringBuilder.append(livePojo.getTime());
        textView.setText(stringBuilder.toString());
        holder.prize.setText(String.valueOf(livePojo.getWinPrize()));
        holder.perkill.setText(String.valueOf(livePojo.getPerKill()));
        String matchType = livePojo.getEntryType();
        if (matchType.equals("Free")) {
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
            holder.sponsorTextArea.setVisibility(View.GONE);
        } else if (matchType.equals("Sponsored")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Sponsored by ");
            stringBuilder.append(livePojo.getSponsoredBy());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else if (matchType.equals("Giveaway")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Giveaway by ");
            stringBuilder.append(livePojo.getSponsoredBy());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            holder.fee.setText(String.valueOf(livePojo.getEntryFee()));
            holder.sponsorTextArea.setVisibility(View.GONE);
        }

        holder.type.setText(livePojo.getMatchType());
        holder.version.setText(livePojo.getVersion());
        holder.map.setText(livePojo.getMap());

        if (!livePojo.getImage().isEmpty()) {
            holder.topImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+livePojo.getImage()).resize(720,480).into(holder.topImage);
    }
        else {
            holder.topImage.setVisibility(View.GONE);
        }


        try{
            if (livePojo.getIs_cancel().equals("1")){
                holder.playbtnLL.setVisibility(View.VISIBLE);
                holder.playBtn.setText("CANCELED");
                holder.playBtn.setEnabled(false);
                holder.playBtn.setClickable(false);
            }
            else if (livePojo.getJoined_status().equals("null")) {
                holder.playbtnLL.setVisibility(View.GONE);
            }
            else {
                holder.playbtnLL.setVisibility(View.VISIBLE);
                holder.playBtn.setText("PLAY NOW");
                holder.playBtn.setEnabled(true);
                holder.playBtn.setClickable(true);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }


        holder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExtraOperations().launchPUBGMobile(context);
            }
        });
        
        holder.spectateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (!livePojo.getSpectateURL().startsWith("http://") && !livePojo.getSpectateURL().startsWith("https://")){
                        context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://"+livePojo.getSpectateURL())));
                    }
                    else {
                        context. startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(livePojo.getSpectateURL())));
                    }
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (new ExtraOperations().haveNetworkConnection(context)) {
                    Intent intent = new Intent(context, LiveDetailsActivity.class);
                    intent.putExtra("EntryFee_KEY", livePojo.getEntryFee());
                    intent.putExtra("ID_KEY", livePojo.getId());
                    intent.putExtra("Map_KEY", livePojo.getMap());
                    intent.putExtra("Rules_KEY", livePojo.getRules());
                    intent.putExtra("PerKill_KEY", livePojo.getPerKill());
                    intent.putExtra("StartTime_KEY", livePojo.getTime());
                    intent.putExtra("Match_Status_KEY", livePojo.getMatch_status());
                    intent.putExtra("Title_KEY", livePojo.getTitle());
                    intent.putExtra("TopImage_KEY", livePojo.getImage());
                    intent.putExtra("Entry_Type_KEY", livePojo.getEntryType());
                    intent.putExtra("Match_Type_KEY", livePojo.getMatchType());
                    intent.putExtra("Version_KEY", livePojo.getVersion());
                    intent.putExtra("WinPrize_KEY", livePojo.getWinPrize());
                    intent.putExtra("Private_Status_KEY", livePojo.getIsPrivateMatch());
                    intent.putExtra("SpectateURL_KEY", livePojo.getSpectateURL());
                    intent.putExtra("MATCH__KEY", livePojo.getMatch_id());
                    intent.putExtra("ROOM_ID_KEY", livePojo.getRoom_id());
                    intent.putExtra("ROOM_PASS_KEY", livePojo.getRoom_pass());
                    intent.putExtra("ROOM_SIZE_KEY", livePojo.getRoom_size());
                    intent.putExtra("TOTAL_JOINED_KEY", livePojo.getTotal_joined());
                    intent.putExtra("JOINED_STATUS_KEY", livePojo.getJoined_status());
                    intent.putExtra("IS_CANCELED_KEY", livePojo.getIs_cancel());
                    intent.putExtra("CANCELED_DESC_KEY", livePojo.getCancel_reason());
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
        return livePojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView fee;
        ImageView img;
        TextView map;
        TextView perkill;
        Button playBtn;
        LinearLayout playbtnLL;
        TextView prize;
        TextView size;
        Button spectateBtn;
        TextView sponsorText;
        RelativeLayout sponsorTextArea;
        TextView spot;
        TextView timedate;
        TextView title;
        ImageView topImage;
        TextView type;
        TextView version;

        public ViewHolder(View itemView) {
            super(itemView);

            this.cardView = (CardView) itemView.findViewById(R.id.mainCard);
            this.img = (ImageView) itemView.findViewById(R.id.img);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.timedate = (TextView) itemView.findViewById(R.id.timedate);
            this.prize = (TextView) itemView.findViewById(R.id.winPrize);
            this.perkill = (TextView) itemView.findViewById(R.id.perKill);
            this.fee = (TextView) itemView.findViewById(R.id.entryFee);
            this.playBtn = (Button) itemView.findViewById(R.id.playButton);
            this.spectateBtn = (Button) itemView.findViewById(R.id.spectateButton);
            this.sponsorTextArea = (RelativeLayout) itemView.findViewById(R.id.sponsorTextArea);
            this.sponsorText = (TextView) itemView.findViewById(R.id.sponsorText);
            this.type = (TextView) itemView.findViewById(R.id.matchType);
            this.version = (TextView) itemView.findViewById(R.id.matchVersion);
            this.map = (TextView) itemView.findViewById(R.id.matchMap);
            this.playbtnLL = (LinearLayout) itemView.findViewById(R.id.playButtonLL);
            this.topImage = (ImageView) itemView.findViewById(R.id.mainTopBanner);
        }

    }
}
