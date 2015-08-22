package com.digitalcranberry.gainslapi.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.digitalcranberry.gainslapi.StoreReport;
import static com.digitalcranberry.gainslapi.Constants.*;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Report {
	
		private String reportid;

	    private String content;
	    private Date date;  //this field is indexed upon and changing its name makes the index fail
	    					//consequently I don't use it. Sigh. 
	    private Date dateCaptured;
	    private Date dateReceived;
	    private Date lastUpdated;
	    private String status;
	    private Double latitude;
	    private Double longitude;
	    private String image;
	    private String reporter;
	    
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

	    public Report() {
			// TODO Auto-generated constructor stub
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
	        sb.append("reportid=" + reportid);
	        sb.append("&content=" + content);
	        sb.append("&latitude=" + latitude);
	        sb.append("&longitude=" + longitude);
	        return sb.toString();
	    }

		public String getReportid() {
			return reportid;
		}

		public void setReportid(String reportid) {
			this.reportid = reportid;
		}

		public Date getDateReceived() {
			return dateReceived;
		}

		public void setDateReceived(Date dateReceived) {
			this.dateReceived = dateReceived;
		}

		public Date getLastUpdated() {
			return lastUpdated;
		}

		public void setLastUpdated(Date lastUpdated) {
			this.lastUpdated = lastUpdated;
		}
		
		  public Report(HttpServletRequest req) {
			  
			  this.setContent(req.getParameter("content"));
			  this.setReportid(req.getParameter(REPORT_ID));
			  this.setLatitude(Double.parseDouble(req.getParameter("latitude")));
			  this.setLongitude(Double.parseDouble(req.getParameter("longitude")));
			  this.setOrgName(propertyOrDefault(req, "orgName", "OU"));
			  this.setStatus(propertyOrDefault(req, "status", "new"));
			  
			  //the dates need to be parsed: 
			  String dateFirstCapturedStr = req.getParameter("dateFirstCaptured");
			  String lastUpdateStr = req.getParameter("lastUpdated");
			  
			  Date date = new Date();
			  Date dateFirstCaptured = dateFromString(dateFirstCapturedStr);
			  Date dateReceived = new Date(); //we've received it now :)
			  Date lastUpdated = dateFromString(lastUpdateStr);
			  
			  this.setDateCreated(dateFirstCaptured);
			  this.setDateReceived(dateReceived);
			  this.setLastUpdated(lastUpdated);	
		  }

		  public Entity toEntity(Report report){  
			//set report ID to match the ID sent by the user.
			Key reportStoreKey = KeyFactory.createKey("gainsl", report.getReportid());
			    
			Entity reportEntity = new Entity(REPORT_ENTITY, reportStoreKey);

		    UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
		    
		    final Logger log = Logger.getLogger(StoreReport.class.getName());    
		    
		    
		    if (user != null) {
		        reportEntity.setProperty("author_id", user.getUserId());
		        reportEntity.setProperty("author_email", user.getEmail());
		      }


		    Date date = new Date();

		    reportEntity.setProperty("latitude", report.getLatitude());
		    reportEntity.setProperty("orgName", report.getOrgName());
		    reportEntity.setProperty("longitude", report.getLongitude());
		    reportEntity.setProperty("content", report.getContent());
		    reportEntity.setProperty("reportid", report.getReportid());
		    reportEntity.setProperty("dateCreated", report.getDateCreated());
		    reportEntity.setProperty("lastUpdated", report.getLastUpdated());
		    reportEntity.setProperty("date", date);
		    reportEntity.setProperty("status", report.getStatus());
		    reportEntity.setProperty("previousStates", new ArrayList<EmbeddedEntity>());
		    
		    return reportEntity;
		  }
		  
		  private String propertyOrDefault(HttpServletRequest req, String propName, String defaultValue) {
			  String value = req.getParameter(propName);
			  if (value == null) {
				  value = defaultValue;
			  }
			  return value;
		  }
		  
		  private Date dateFromString(String dateString){
			 return new Date(Long.parseLong(dateString));
		  }

}
