package com.example.calenderapp.tips.model;

public class TipModel {
    private String TipTitle;
    private String TipDescription;
    private String TipType;
    private String imageUrl;
    private String TipState;

    private String TipId;



    public TipModel(String tipTitle,String tipId,String tipState,String tipDescription, String tipType, String imageUrl) {
        TipId = tipId;
        TipTitle = tipTitle;
        TipState = tipState;
        TipDescription = tipDescription;
        TipType = tipType;
        this.imageUrl = imageUrl;
    }

    public TipModel() {
    }

    public String getTipTitle() {
        return TipTitle;
    }

    public void setTipTitle(String tipTitle) {
        TipTitle = tipTitle;
    }

    public String getTipDescription() {
        return TipDescription;
    }

    public void setTipDescription(String tipDescription) {
        TipDescription = tipDescription;
    }

    public String getTipType() {
        return TipType;
    }

    public void setTipType(String tipType) {
        TipType = tipType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTipState() {
        return TipState;
    }

    public void setTipState(String tipState) {
        TipState = tipState;
    }

    public String getTipId() {
        return TipId;
    }

    public void setTipId(String tipId) {
        TipId = tipId;
    }
}
