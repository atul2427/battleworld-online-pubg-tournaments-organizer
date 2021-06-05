package com.skycoder.pubg.model;

public class ResultPojo {

    private String id,title,time,matchType,version,map,isPrivateMatch,entryType,
            sponsoredBy,spectateURL,matchNotes,match_status,joined_status,image;
    private int winPrize,perKill,entryFee;

    public ResultPojo() {
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

    public String getMatchNotes() {
        return matchNotes;
    }

    public void setMatchNotes(String matchNotes) {
        this.matchNotes = matchNotes;
    }

    public String getMatch_status() {
        return match_status;
    }

    public void setMatch_status(String match_status) {
        this.match_status = match_status;
    }

    public String getJoined_status() {
        return joined_status;
    }

    public void setJoined_status(String joined_status) {
        this.joined_status = joined_status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getWinPrize() {
        return winPrize;
    }

    public void setWinPrize(int winPrize) {
        this.winPrize = winPrize;
    }

    public int getPerKill() {
        return perKill;
    }

    public void setPerKill(int perKill) {
        this.perKill = perKill;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }
}
