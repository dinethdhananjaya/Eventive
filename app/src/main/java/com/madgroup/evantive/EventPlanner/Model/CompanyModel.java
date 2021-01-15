package com.madgroup.evantive.EventPlanner.Model;

public class CompanyModel {

    private  String CompanyId;
    private  String CompanyName;
    private  String CompanyPath;
    private  String CompanyContact;

    public String getCompanyContact() {
        return CompanyContact;
    }

    public void setCompanyContact(String companyContact) {
        CompanyContact = companyContact;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(String companyId) {
        CompanyId = companyId;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getCompanyPath() {
        return CompanyPath;
    }

    public void setCompanyPath(String companyPath) {
        CompanyPath = companyPath;
    }
}
