package com.digitalcranberry.gainslapi;

import com.digitalcranberry.gainslapi.model.Report;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class StoreReport extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    final Logger log = Logger.getLogger(StoreReport.class.getName());

    Entity newReport = setReportDetails(req);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
    Entity existingReport = checkForExisting(newReport, datastore);
    if(existingReport == null) {
        //this is a new report, just add it to the store
    	datastore.put(newReport);
    } else {
        //this is an update to a report. We need to check if it's appropriate to over-write.

    	Date existingLastUpdated = (Date) existingReport.getProperty("lastUpdated");
    	Date newLastUpdated = (Date) newReport.getProperty("lastUpdated");
    	if (existingLastUpdated.after(newLastUpdated)) {
    		//this means that a newer report arrived before
    		//the report we've just received, so we want to
    		//store the report we've just received as a previous state, and -not-
    		//overwrite anything.
    		
    		//TODO: Push to report.previousStates;
    		log.info("We've received a report, but there's a newer one than this" +
         "in the Datastore already. Archiving the report we just received.");

    	} else {
    		//this means that the report we're just received is newer than the
    		//current report state. We'll save the existing state in the previous state list
    		//and then overwrite the existing state with the values we just received.
    		log.info("We've received a report that is an update. Updating now.//TODO");
    		//TODO: Push to report.previousStates;

    	}

    }

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    
    if(user != null) {
    	resp.sendRedirect("/gainslapi.jsp?orgName=" + newReport.getProperty("orgName"));
    } else {
    	resp.sendRedirect("/reportList?orgName=" + newReport.getProperty("orgName"));
    }
  }

  private String propertyOrDefault(HttpServletRequest req, String propName, String defaultValue) {
	  String value = req.getParameter(propName);
	  if (value == null) {
		  value = defaultValue;
	  }
	  return value;
  }
  
  private void addPreviousState(Entity report) {
	  
  }

  private Date dateFromString(String dateString){
	 return new Date(Long.parseLong(dateString));
  }

  /*
   * Checks for report with this ID and returns it if it exists
   * Or null, otherwise.
   */
  private Entity checkForExisting(Entity newReport, DatastoreService datastore){
	  Entity report = null;
	  try {
		  Key theKey = newReport.getKey();
		  report = datastore.get(theKey);
	  } catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	  }
	  return report;
  }

  private Entity setReportDetails(HttpServletRequest req){
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    String orgName = 	propertyOrDefault(req, "orgName", "OU");
    String status = 	propertyOrDefault(req, "status", "new");
    String reportid = 	req.getParameter("reportid");
    String latitude = 	req.getParameter("latitude");
    String longitude = 	req.getParameter("longitude");
    String content = 	req.getParameter("content");

    String dateFirstCapturedStr = req.getParameter("dateFirstCaptured");
    String lastUpdateStr = req.getParameter("lastUpdated");

    Date date = new Date();
    Date dateFirstCaptured = dateFromString(dateFirstCapturedStr);
    Date dateReceived = new Date(); //we've received it now :)
    Date lastUpdated = dateFromString(lastUpdateStr); //we've received it now :)

    //set report ID to match the ID sent by the user.
    Key reportStoreKey = KeyFactory.createKey("gainsl", reportid);

    Entity report = new Entity("Report", reportStoreKey);
    if (user != null) {
      report.setProperty("author_id", user.getUserId());
      report.setProperty("author_email", user.getEmail());
    }

    report.setProperty("latitude", latitude);
    report.setProperty("longitude", longitude);
    report.setProperty("content", content);
    report.setProperty("reportid", reportid);
    report.setProperty("dateCreated", dateFirstCaptured);
    report.setProperty("lastUpdated", lastUpdated);
    report.setProperty("date", date);
    report.setProperty("status", status);
    return report;
  }

}
