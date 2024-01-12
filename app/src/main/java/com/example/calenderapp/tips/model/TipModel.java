package com.example.calenderapp.tips.model;

public class TipModel {
    private String TipTitle;
    private String TipDescription;
    private String TipType;
    private String imageUrl;



    public TipModel(String tipTitle, String tipDescription, String tipType, String imageUrl) {
        TipTitle = tipTitle;
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
}
