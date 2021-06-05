package com.skycoder.pubg.model;

public class LivePojo {
    private String id,title,time,matchType,version,map,isPrivateMatch,entryType,sponsoredBy,spectateURL,
            match_status,match_id,room_id,room_pass,joined_status,rules,image,is_cancel,cancel_reason;
    private int entryFee,perKill,winPrize,room_size,total_joined;


    public LivePojo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getIsPrivateMatch() {
        return isPrivateMatch;
    }

    public void setIsPrivateMatch(String isPrivateMatch) {
        this.isPrivateMatch = isPrivateMatch;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getSponsoredBy() {
        return sponsoredBy;
    }

    public void setSponsoredBy(String sponsoredBy) {
        this.sponsoredBy = sponsoredBy;
    }

    public String getSpectateURL() {
        return spectateURL;
    }

    public void setSpectateURL(String spectateURL) {
        this.spectateURL = spectateURL;
    }

    public String getMatch_status() {
        return match_status;
    }

    public void setMatch_status(String match_status) {
        this.match_status = match_status;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_pass() {
        return room_pass;
    }

    public void setRoom_pass(String room_pass) {
        this.room_pass = room_pass;
    }

    public String getJoined_status() {
        return joined_status;
    }

    public void setJoined_status(String joined_status) {
        this.joined_status = joined_status;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIs_cancel() {
        return is_cancel;
    }

    public void setIs_cancel(String is_cancel) {
        this.is_cancel = is_cancel;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public int getPerKill() {
        return perKill;
    }

    public void setPerKill(int perKill) {
        this.perKill = perKill;
    }

    public int getWinPrize() {
        return winPrize;
    }

    public void setWinPrize(int winPrize) {
        this.winPrize = winPrize;
    }

    public int getRoom_size() {
        return room_size;
    }

    public void setRoom_size(int room_size) {
        this.room_size = room_size;
    }

    public int getTotal_joined() {
        return total_joined;
    }

    public void setTotal_joined(int total_joined) {
        this.total_joined = total_joined;
    }
}
