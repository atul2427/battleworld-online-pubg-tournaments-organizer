package com.skycoder.pubg.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.skycoder.pubg.R;
import com.skycoder.pubg.activity.JoiningMatchActivity;
import com.skycoder.pubg.activity.PlayDetailsActivity;
import com.skycoder.pubg.common.Config;
import com.skycoder.pubg.model.PlayPojo;
import com.skycoder.pubg.utils.ExtraOperations;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.ViewHolder> {

    private Context context;
    private List<PlayPojo> playPojoList;
    private String left_spots;

    public PlayAdapter(List<PlayPojo> playPojoList, Context context){
        super();
        this.playPojoList = playPojoList;
        this.context = context;
    }

    @Override
    public PlayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_play, parent, false);
        PlayAdapter.ViewHolder viewHolder = new PlayAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlayAdapter.ViewHolder holder, int position) {
        final PlayPojo playPojo =  playPojoList.get(position);

        holder.title.setText(playPojo.getTitle());
        TextView textView = holder.timedate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time: ");
        stringBuilder.append(playPojo.getTime());
        textView.setText(stringBuilder.toString());
        holder.prize.setText(String.valueOf(playPojo.getWinPrize()));
        holder.perkill.setText(String.valueOf(playPojo.getPerKill()));
        String entryType = playPojo.getEntryType();
        if (entryType.equals("Free")) {
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
            holder.sponsorTextArea.setVisibility(View.GONE);
        } else if (entryType.equals("Sponsored")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Sponsored by ");
            stringBuilder.append(playPojo.getSponsoredBy());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else if (entryType.equals("Giveaway")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Giveaway by ");
            stringBuilder.append(playPojo.getSponsoredBy());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            holder.fee.setText(String.valueOf(playPojo.getEntryFee()));
            holder.sponsorTextArea.setVisibility(View.GONE);
        }

        if (playPojo.getIsPrivateMatch().equals("yes")) {
            holder.privateTextArea.setVisibility(View.VISIBLE);
        } else {
            holder.privateTextArea.setVisibility(View.GONE);
        }


        holder.type.setText(playPojo.getMatchType());
        holder.version.setText(playPojo.getVersion());
        holder.map.setText(playPojo.getMap());

        if (!playPojo.getImage().isEmpty()) {
            holder.topImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+playPojo.getImage()).resize(720,480).into(holder.topImage);
        }
        else {
            holder.topImage.setVisibility(View.GONE);
        }

        holder.materialProgressBar.setMax(playPojo.getRoom_size());
        holder.materialProgressBar.setProgress(playPojo.getTotal_joined());

        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(playPojo.getTotal_joined());
        stringBuilder1.append("/");
        stringBuilder1.append(playPojo.getRoom_size());
        holder.size.setText(stringBuilder1.toString());

        if (playPojo.getTotal_joined() >= playPojo.getRoom_size()) {
            holder.spot.setTextColor(Color.parseColor("#ff0000"));
            holder.spot.setText("No Spots Left! Match is Full.");
            holder.joinBtn.setText("MATCH FULL");
            holder.joinBtn.setTextColor(Color.parseColor("#ffffff"));
            holder.joinBtn.setBackgroundResource(R.drawable.buttonbackactive);
            holder.joinBtn.setEnabled(false);
            holder.joinBtn.setClickable(false);
        } else {
            left_spots = String.valueOf(playPojo.getRoom_size() - playPojo.getTotal_joined());
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Only ");
            stringBuilder2.append(left_spots);
            stringBuilder2.append(" spots left");
            holder.spot.setText(stringBuilder2.toString());
            holder.joinBtn.setEnabled(true);
            holder.joinBtn.setClickable(true);
        }

        try {
            if (playPojo.getIs_cancel().equals("1")){
                holder.spot.setTextColor(Color.parseColor("#ff0000"));
                holder.spot.setText("Can't Join Match! Match is Canceled.");

                holder.joinBtn.setText("CANCELED");
                holder.joinBtn.setTextColor(Color.parseColor("#ffffff"));
                holder.joinBtn.setBackgroundResource(R.drawable.buttonbackactive);
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else if (playPojo.getUser_joined().equals("0") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (!playPojo.getUser_joined().equals("1") && playPojo.getMatchType().equals("Solo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("+ Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (!playPojo.getUser_joined().equals("2") && playPojo.getMatchType().equals("Duo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("+ Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (!playPojo.getUser_joined().equals("4") && playPojo.getMatchType().equals("Squad") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("+ Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (playPojo.getUser_joined().equals("1") && playPojo.getMatchType().equals("Solo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Joined");
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else if (playPojo.getUser_joined().equals("2") && playPojo.getMatchType().equals("Duo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Joined");
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else if (playPojo.getUser_joined().equals("4") && playPojo.getMatchType().equals("Squad") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Joined");
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else {
                holder.spot.setTextColor(Color.parseColor("#ff0000"));
                holder.spot.setText("No Spots Left! Match is Full.");

                holder.joinBtn.setText("MATCH FULL");
                holder.joinBtn.setTextColor(Color.parseColor("#ffffff"));
                holder.joinBtn.setBackgroundResource(R.drawable.buttonbackactive);
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
             if (new ExtraOperations().haveNetworkConnection(context)) {
                    Intent intent = new Intent(context, PlayDetailsActivity.class);
                    intent.putExtra("EntryFee_KEY", playPojo.getEntryFee());
                    intent.putExtra("ID_KEY", playPojo.getId());
                    intent.putExtra("Map_KEY", playPojo.getMap());
                    intent.putExtra("Rules_KEY", playPojo.getRules());
                    intent.putExtra("PerKill_KEY", playPojo.getPerKill());
                    intent.putExtra("StartTime_KEY", playPojo.getTime());
                    intent.putExtra("Match_Status_KEY", playPojo.getMatch_status());
                    intent.putExtra("Title_KEY", playPojo.getTitle());
                    intent.putExtra("TopImage_KEY", playPojo.getImage());
                    intent.putExtra("Entry_Type_KEY", playPojo.getEntryType());
                    intent.putExtra("Match_Type_KEY", playPojo.getMatchType());
                    intent.putExtra("Version_KEY", playPojo.getVersion());
                    intent.putExtra("WinPrize_KEY", playPojo.getWinPrize());
                    intent.putExtra("Private_Status_KEY", playPojo.getIsPrivateMatch());
                    intent.putExtra("MATCH__KEY", playPojo.getMatch_id());
                    intent.putExtra("ROOM_SIZE_KEY", playPojo.getRoom_size());
                    intent.putExtra("TOTAL_JOINED_KEY", playPojo.getTotal_joined());
                    intent.putExtra("JOINED_STATUS_KEY", playPojo.getJoined_status());
                    intent.putExtra("USER_JOINED_KEY", playPojo.getUser_joined());
                    intent.putExtra("IS_CANCELED_KEY", playPojo.getIs_cancel());
                    intent.putExtra("CANCELED_DESC_KEY", playPojo.getCancel_reason());
                    intent.putExtra("SECRET_CODE_KEY", playPojo.getPrivate_match_code());
                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(context, JoiningMatchActivity.class);
                intent.putExtra("matchType", playPojo.getMatchType());
                intent.putExtra("matchID", playPojo.getId());
                intent.putExtra("matchName", playPojo.getTitle());
                intent.putExtra("entryFee", playPojo.getEntryFee());
                intent.putExtra("entryType", playPojo.getEntryType());
                intent.putExtra("JoinStatus", playPojo.getJoined_status());
                intent.putExtra("isPrivate", playPojo.getIsPrivateMatch());
                intent.putExtra("matchRules", playPojo.getRules());
                intent.putExtra("ROOM_SIZE_KEY", playPojo.getRoom_size());
                intent.putExtra("TOTAL_JOINED_KEY", playPojo.getTotal_joined());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return playPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView fee;
        ImageView img;
        Button joinBtn;
        TextView map;
        ProgressBar materialProgressBar;
        TextView perkill;
        RelativeLayout privateTextArea;
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

        public ViewHolder(View itemView) {
            super(itemView);

            this.cardView = (CardView) itemView.findViewById(R.id.mainCard);
            this.img = (ImageView) itemView.findViewById(R.id.img);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.timedate = (TextView) itemView.findViewById(R.id.timedate);
            this.prize = (TextView) itemView.findViewById(R.id.winPrize);
            this.perkill = (TextView) itemView.findViewById(R.id.perKill);
            this.fee = (TextView) itemView.findViewById(R.id.entryFee);
            this.spot = (TextView) itemView.findViewById(R.id.spots);
            this.size = (TextView) itemView.findViewById(R.id.size);
            this.materialProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            this.joinBtn = (Button) itemView.findViewById(R.id.joinButton);
            this.sponsorTextArea = (RelativeLayout) itemView.findViewById(R.id.sponsorTextArea);
            this.sponsorText = (TextView) itemView.findViewById(R.id.sponsorText);
            this.type = (TextView) itemView.findViewById(R.id.matchType);
            this.version = (TextView) itemView.findViewById(R.id.matchVersion);
            this.map = (TextView) itemView.findViewById(R.id.matchMap);
            this.topImage = (ImageView) itemView.findViewById(R.id.mainTopBanner);
            this.privateTextArea = (RelativeLayout) itemView.findViewById(R.id.privateTextArea);
        }

    }

}
