package com.digitalcranberry.gainslapi;

import com.digitalcranberry.gainslapi.model.Report;
import static com.digitalcranberry.gainslapi.Constants.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class StoreReport extends HttpServlet {
	

	
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
	  
	Report newReport = new Report(req);
	  final Logger log = Logger.getLogger(StoreReport.class.getName());    
	  log.info(req.getParameterMap().toString());
	  log.info(newReport.toQueryParam());
  
    checkForExisting(newReport);

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    
    if(user != null) {
    	resp.sendRedirect("/gainslapi.jsp?orgName=" + newReport.getOrgName());
    } else {
    	resp.sendRedirect("/reportList?orgName=" + newReport.getOrgName());
    }
  }

  private void pushPreviousState(Entity existingEntity, EmbeddedEntity oldEntity) {
	  final Logger log = Logger.getLogger(StoreReport.class.getName());    
	  log.info("DEBUGGING NULLPOINTER" +  oldEntity.toString() + existingEntity.toString());
	  ArrayList<EmbeddedEntity> previousStates;
	  if (existingEntity.hasProperty(PREVIOUS_STATES) && 
			  (existingEntity.getProperty(PREVIOUS_STATES) != null)) {
		  previousStates = (ArrayList<EmbeddedEntity>) existingEntity.getProperty(PREVIOUS_STATES);
	  } else {
		  previousStates = new ArrayList<EmbeddedEntity>();
	  }
	  previousStates.add(oldEntity);
	  existingEntity.setProperty(PREVIOUS_STATES, previousStates);
  }



  /*
   * Checks for report with this ID and returns it if it exists
   * Or null, otherwise.
   */
  private void checkForExisting(Report newReport){
	  final Logger log = Logger.getLogger(StoreReport.class.getName());    
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	  
	  Entity existingReportEntity = checkForEntity(newReport);
	  EmbeddedEntity oldState = new EmbeddedEntity();
	  
	  if(existingReportEntity != null) {
	      //this is an update to a report. We need to check if it's appropriate to over-write.
		  
	      Date existingLastUpdated = (Date) existingReportEntity.getProperty("lastUpdated");
	      Date newLastUpdated = newReport.getLastUpdated();
	      if (existingLastUpdated.after(newLastUpdated)) {
	    	  //this means that a newer report arrived before
	    	  //the report we've just received, so we want to
	    	  //store the report we've just received as a previous state, and -not-
	    	  //overwrite anything.
	    	  log.info("We've received a report, but there's a newer one than this" +
	    			  "in the Datastore already. Archiving the report we just received.");
	    	  oldState.setPropertiesFrom(newReport.toEntity());

	      	} else {
      			//this means that the report we're just received is newer than the
	      		//current report state. We'll save the existing state in the previous state list
	      		//and then overwrite the existing state with the values we just received.
	      		log.info("We've received a report that is an update. Updating report state now.");
	    	  
	      		//create set previous state entity properties from current state.
	      		//this is a shallow value copy, not a reference to the existing entity
	      		oldState.setPropertiesFrom(existingReportEntity);
	      		
	      		//no point storing duplicates of properties that will never change, e.g. dateCreated.
	      		removeDuplicateProperties(oldState);
	    	  	      		
	      		//update the current report state to match the new info we've just obtained
	      		//this updates the status, last updated, comments, and date received
	      		updateReportEntity(newReport,existingReportEntity);
	      	}
      		
	      	//store the previous state entity in a list of previous states.
	      	pushPreviousState(existingReportEntity,oldState);

      		//finally, save the entity that we've just been manipulating.
	      	//it'll over-write the old one.
	      	datastore.put(existingReportEntity);	      
	      	
	  	}  else {
		  //this is a brand new report.
	  		Entity newReportEntity = newReport.toEntity();
	  		datastore.put(newReportEntity);
	  	}
	  

  }
  
  public Entity checkForEntity(Report newReport) {
	  
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  String oldid;
	  String newid = newReport.getReportid();

	  Filter filter =
	    new FilterPredicate(REPORT_ID,
	                        FilterOperator.EQUAL,
	                        newid);
	  
	  Query betterQuery = new Query(REPORT_ENTITY).setFilter(filter);

	  PreparedQuery betterpq = datastore.prepare(betterQuery);

	  final Logger log = Logger.getLogger(StoreReport.class.getName());    
	  log.info("checking to see if this is an update or not");
	  log.info(newReport.toQueryParam());
	  
	  for (Entity result : betterpq.asIterable(FetchOptions.Builder.withLimit(1))) {
		  //compares report IDs.
		  oldid = (String) result.getProperty(REPORT_ID);
		  if(oldid.equals(newid)) {
			  log.info("****************" + newid + "***********");
		  }
		  
		 return result; //what happens if there is more than one? Who knows. 
	  }
	  
	  return null;
  }
  
  private EmbeddedEntity removeDuplicateProperties(EmbeddedEntity result){	  
		//remove this property before we store it, because otherwise each 
		//consecutive update contains all previous states. This would be an exponential
		//storage growth problem. No thanks. 
		result.removeProperty(PREVIOUS_STATES);
		
		//these properties stay the same every time. No point storing multiple times.
		result.removeProperty("dateCreated");
		result.removeProperty("latitude");
		result.removeProperty("longitude");
		result.removeProperty("reporter");
		result.removeProperty("date");
		result.removeProperty("assignee");
		result.removeProperty("orgName");
		return result;
  };
  
  private void updateReportEntity(Report newReport, Entity oldEntity) {
	  oldEntity.setProperty("status", newReport.getStatus());
	  oldEntity.setProperty("lastUpdated", newReport.getLastUpdated());
	  oldEntity.setProperty("lastUpdatedBy", newReport.getLastUpdatedBy());
	  oldEntity.setProperty("dateReceived", newReport.getDateReceived());
	  oldEntity.setProperty("content", newReport.getContent());
  }
 
}
