package com.madgroup.evantive.EventPlanner.Model;

public class GalleryModel {


    private String mCompanyId;
    private String mImageUrl;
    private String mKey;

    public GalleryModel() {
        //empty constructor needed
    }

    public String getmCompanyId() {
        return mCompanyId;
    }

    public void setmCompanyId(String mCompanyId) {
        this.mCompanyId = mCompanyId;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }


    public GalleryModel(String mCompanyId, String mImageUrl) {
        this.mCompanyId = mCompanyId;
        this.mImageUrl = mImageUrl;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
