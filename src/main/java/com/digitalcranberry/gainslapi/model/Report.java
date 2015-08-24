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
	    private String assignee;
	    private String lastUpdatedBy;
	    private String orgName;
	    
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
	        return dateCaptured;
	    }

	    public void setDateCreated(Date date) {
	        this.dateCaptured = date;
	    }

	    public String toQueryParam() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("orgName=" + orgName);
	        sb.append("lastUpdatedBy=" + lastUpdatedBy);
	        sb.append("reporter=" + reporter);
	        sb.append("assignee=" + assignee);
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
		
		public void setReporter(String reporter){
			this.reporter = reporter;
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
			  this.setReporter(req.getParameter("reporter"));
			  this.setAssignee(req.getParameter("assignee"));
			  this.setLastUpdatedBy(req.getParameter("lastUpdatedBy"));
			  
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

		  private void setLastUpdatedBy(String lastUpdatedBy) {
			  this.lastUpdatedBy = lastUpdatedBy;
		  }

		  private void setAssignee(String assignee) {
			  this.assignee = assignee;
		  }

		  public Entity toEntity(){  
			//set report ID to match the ID sent by the user.
			Key reportStoreKey = KeyFactory.createKey("gainsl", getReportid());
			    
			Entity reportEntity = new Entity(REPORT_ENTITY, reportStoreKey);

		    UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
		    
		    final Logger log = Logger.getLogger(StoreReport.class.getName());    
		    
		    
		    if (user != null) {
		        reportEntity.setProperty("author_id", user.getUserId());
		        reportEntity.setProperty("author_email", user.getEmail());
		      }


		    Date date = new Date();

		    reportEntity.setProperty("latitude", getLatitude());
		    reportEntity.setProperty("orgName", getOrgName());
		    reportEntity.setProperty("longitude", getLongitude());
		    reportEntity.setProperty("content", getContent());
		    reportEntity.setProperty("reportid", getReportid());
		    reportEntity.setProperty("reporter", getReporter());
		    reportEntity.setProperty("assignee", getAssignee());
		    reportEntity.setProperty("dateCreated", getDateCreated());
		    reportEntity.setProperty("lastUpdated", getLastUpdated());
		    reportEntity.setProperty("lastUpdatedBy", getLastUpdatedBy());
		    reportEntity.setProperty("date", date);
		    reportEntity.setProperty("status", getStatus());
		    reportEntity.setProperty("previousStates", new ArrayList<EmbeddedEntity>());
		    
		    return reportEntity;
		  }
		  
		  private String getAssignee() {
			  return assignee;
		  }

		  private String getReporter() {
			return reporter;
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

		public String getLastUpdatedBy() {
			return this.lastUpdatedBy;
		}

}
