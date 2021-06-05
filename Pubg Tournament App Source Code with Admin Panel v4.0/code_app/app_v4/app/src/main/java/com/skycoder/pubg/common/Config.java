package com.skycoder.pubg.common;

import com.skycoder.pubg.BuildConfig;

public class Config {

    //put your admin panel url here
    public static final String ADMIN_PANEL_URL = BuildConfig.My_api;
    public static final String FILE_PATH_URL = BuildConfig.My_file;
    public static final String PAYTM_URL = BuildConfig.My_paytm;
    public static final String PAYPAL_URL = BuildConfig.My_paypal;
    public static final String PURCHASE_CODE = BuildConfig.My_code;

    // Paytm Production API Details
    public static final String M_ID = "YlVxKU64867400290398";   //Paytm Merchand Id we got it in button_paytm credentials
    public static final String CHANNEL_ID = "WAP";              //Paytm Channel Id, got it in button_paytm credentials
    public static final String INDUSTRY_TYPE_ID = "Retail";     //Paytm industry type got it in button_paytm credential
    public static final String WEBSITE = "DEFAULT";
    public static final String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

    // UPI Payment Id
    public static final String UPI_ID = "skyforcoding@okhdfc";

    // How To Join Room YouTube Link
    public static final String YOUTUBE_CHANNEL_ID = "https://www.youtube.com/channel/skyforcoding";

    // Discord Channel Link
    public static final String DISCORD_CHANNEL_ID = "https://discord.gg/ukn7nN";

    // Refer & Reward Offer
    //set true to enable or set false to disable
    public static final boolean REFER_EARN = true;
    public static final boolean WATCH_EARN = true;

    // AdMob Keys
    //set admob app id and ad unit id
    public static final String AD_APP_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String AD_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";

    // Reward Ads Setup
    //set next reward time, watch video, pay rewars
    public static final String WATCH_COUNT = "3";       // Set minimum watch video
    public static final String PAY_REWARD = "1";        // Set amount after rewarded

    // Customer Support Details
    //set true to enable or set false to disable
    public static final boolean ENABLE_EMAIL_SUPPORT = true;
    public static final boolean ENABLE_PHONE_SUPPORT = true;
    public static final boolean ENABLE_WHATSAPP_SUPPORT = true;
    public static final boolean ENABLE_MESSENGER_SUPPORT = true;
    public static final boolean ENABLE_DISCORD_SUPPORT = true;

    // Follow Us Link
    //set true to enable or set false to disable
    public static final boolean ENABLE_TWITTER_FOLLOW = true;
    public static final boolean ENABLE_YOUTUBE_FOLLOW = true;
    public static final boolean ENABLE_FACEBOOK_FOLLOW = true;
    public static final boolean ENABLE_INSTAGRAM_FOLLOW = true;

}
