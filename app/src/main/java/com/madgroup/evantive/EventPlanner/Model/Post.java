package com.madgroup.evantive.EventPlanner.Model;
import com.google.firebase.database.ServerValue;

public class Post {

    private String postKey;
    private String companyId;
    private String companyName;
    private String companyContactNumber;
    private String companyLogopath;
    private String title;
    private String description;
    private String location;
    private String imagepath;
    private String category;
    private String date;

    public String getCompanyContactNumber() {
        return companyContactNumber;
    }

    public void setCompanyContactNumber(String companyContactNumber) {
        this.companyContactNumber = companyContactNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getCompanyLogopath() {
        return companyLogopath;
    }

    public void setCompanyLogopath(String companyLogopath) {
        this.companyLogopath = companyLogopath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    private Object timeStamp ;


    public Post(){}


    public Post(String companyId, String companyName, String companyContactNumber, String companyLogopath, String title, String description, String location, String imagepath, String category, String date) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyContactNumber = companyContactNumber;
        this.companyLogopath = companyLogopath;
        this.title = title;
        this.description = description;
        this.location = location;
        this.imagepath = imagepath;
        this.category = category;
        this.date = date;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
