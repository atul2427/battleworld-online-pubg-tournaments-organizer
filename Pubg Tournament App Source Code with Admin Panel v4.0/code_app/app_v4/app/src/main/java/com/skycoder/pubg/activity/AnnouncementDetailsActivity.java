package com.skycoder.pubg.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.skycoder.pubg.views.Tools;
import com.squareup.picasso.Picasso;

import com.skycoder.pubg.R;
import com.skycoder.pubg.common.Config;

public class AnnouncementDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing_toolbar;

    private TextView titleTv, dateTv;
    private Button buttonBt;
    private ImageView imageIv;
    private WebView webView;

    private String id,title,message,image,url,created;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_details);

        initToolbar();
        initView();
        initBundel();

        if (!url.isEmpty()) {
            buttonBt.setVisibility(View.VISIBLE);
        }
        else {
            buttonBt.setVisibility(View.GONE);
        }

        buttonBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(url);
            }
        });

    }

    private void initBundel() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            title = extras.getString("title");
            message = extras.getString("message");
            image = extras.getString("image");
            url = extras.getString("url");
            created = extras.getString("created");

            if (!image.isEmpty()) {
                Picasso.get().load(Config.FILE_PATH_URL+image).resize(720,480).placeholder(R.drawable.pubg_banner).into(imageIv);
            }

            toolbar.setTitle((CharSequence) title);
            titleTv.setText(title);
            dateTv.setText(created);
            this.webView.setBackgroundColor(0);
            this.webView.loadData(message,"text/html", "UTF-8");
        }
    }

    private void initView() {
        this.titleTv = (TextView) findViewById(R.id.titleTv);
        this.dateTv = (TextView) findViewById(R.id.dateTv);
        this.buttonBt = (Button) findViewById(R.id.buttonBt);
        this.imageIv = (ImageView) findViewById(R.id.imageIv);
        this.webView = (WebView) findViewById(R.id.webView);
    }

    private void initToolbar() {
        Tools.setSystemBarColor(this, R.color.statusBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setCollapsedTitleTextAppearance(R.style.personal_collapsed_title);
        collapsing_toolbar.setExpandedTitleTextAppearance(R.style.personal_expanded_title);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://" + url);
            }
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
