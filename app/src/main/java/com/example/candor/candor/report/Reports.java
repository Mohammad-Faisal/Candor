package com.example.candor.candor.report;


public class Reports {
    public String reportText;
    public String reportImageUrl;
    public String reportUserID;

    public Reports(String reportText, String reportImageUrl, String reportUserID) {
        this.reportText = reportText;
        this.reportImageUrl = reportImageUrl;
        this.reportUserID = reportUserID;
    }

    public String getReportUserID() {
        return reportUserID;
    }

    public void setReportUserID(String reportUserID) {
        this.reportUserID = reportUserID;
    }

    public Reports() {
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public String getReportImageUrl() {
        return reportImageUrl;
    }

    public void setReportImageUrl(String reportImageUrl) {
        this.reportImageUrl = reportImageUrl;
    }
}
