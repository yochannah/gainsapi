package com.digitalcranberry.gainslapi;

import com.digitalcranberry.gainslapi.model.Report;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
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
	  
	Report newReport = new Report(req);
  
    checkForExisting(newReport);

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    
    if(user != null) {
    	resp.sendRedirect("/gainslapi.jsp?orgName=" + newReport.getOrgName());
    } else {
    	resp.sendRedirect("/reportList?orgName=" + newReport.getOrgName());
    }
  }


  
  private void addPreviousState(Entity report) {
	  
  }



  /*
   * Checks for report with this ID and returns it if it exists
   * Or null, otherwise.
   */
  private void checkForExisting(Report newReport){
	  final Logger log = Logger.getLogger(StoreReport.class.getName());    
	  boolean isNewReport = false;
	  
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  Transaction txn = datastore.beginTransaction();
	  try {
		  Key reportKey = KeyFactory.createKey("gainsl", newReport.getReportid());
	      Entity existingReportEntity = datastore.get(reportKey);
	      
	      //this is an update to a report. We need to check if it's appropriate to over-write.
	      
	      Date existingLastUpdated = (Date) existingReportEntity.getProperty("lastUpdated");
	      Date newLastUpdated = (Date) newReport.getLastUpdated();
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

//	      existingReport.setProperty("vacationDays", 10);

//	      datastore.put(existingReport);

	      txn.commit();
	  } catch (EntityNotFoundException e) {
		  log.info("Entity not found");
		  isNewReport = true;
		  e.printStackTrace();
	  } finally {
		  if (txn.isActive()) {
			  txn.rollback();
		  }
	  }
	  if(isNewReport) {
		    Entity newReportEntity = newReport.toEntity(newReport);
	    	datastore.put(newReportEntity);
	  }

  }
 
}
