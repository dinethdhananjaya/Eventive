package com.madgroup.evantive.User.Model;

public class BookingModel {


    public BookingModel(){}

    private String bookingKey;
    private String companyId;
    private String  userID;
    private String  usersName;
    private String usersImagePath;
    private String  usersEmail;
    private String  date;

    public BookingModel(String companyId, String userID, String usersName, String usersImagePath, String usersEmail, String date) {
        this.companyId = companyId;
        this.userID = userID;
        this.usersName = usersName;
        this.usersImagePath = usersImagePath;
        this.usersEmail = usersEmail;
        this.date = date;
    }

    public String getBookingKey() {
        return bookingKey;
    }

    public void setBookingKey(String bookingKey) {
        this.bookingKey = bookingKey;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsersName() {
        return usersName;
    }

    public void setUsersName(String usersName) {
        this.usersName = usersName;
    }

    public String getUsersImagePath() {
        return usersImagePath;
    }

    public void setUsersImagePath(String usersImagePath) {
        this.usersImagePath = usersImagePath;
    }

    public String getUsersEmail() {
        return usersEmail;
    }

    public void setUsersEmail(String usersEmail) {
        this.usersEmail = usersEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
