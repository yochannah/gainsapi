package com.digitalcranberry.gainslapi.model;

import java.util.Date;

public class Report {

	    private String content;
	    private Date date;
	    private String status;
	    private Double latitude;
	    private Double longitude;
	    private String image;
	    
	    public String getImage() {
	        return image;
	    }

	    public void setImage(String image) {
	        this.image = image;
	    }

	    public String getOrgName() {
	        return orgName;
	    }

	    public void setOrgName(String orgName) {
	        this.orgName = orgName;
	    }

	    private String orgName;

	    public Double getLatitude() {
	        return latitude;
	    }

	    public void setLatitude(Double latitude) {
	        this.latitude = latitude;
	    }

	    public Double getLongitude() {
	        return longitude;
	    }

	    public void setLongitude(Double longitude) {
	        this.longitude = longitude;
	    }

	    public Report(String theContent) {
	        this.content = theContent;
	        this.date = new Date();
	    }

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String theContent) {
	        this.content = theContent;
	    }

	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String aStatus) {
	        this.status = aStatus;
	    }

	    public Date getDateCreated() {
	        return date;
	    }

	    public void setDateCreated(Date date) {
	        this.date = date;
	    }

	    public String toQueryParam() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("orgName=" + orgName);
	        sb.append("&content=" + content);
	        sb.append("&latitude=" + latitude);
	        sb.append("&longitude=" + longitude);
	        return sb.toString();
	    }
}
