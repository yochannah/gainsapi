package com.digitalcranberry.gainslapi;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    final Logger log = Logger.getLogger(StoreReport.class.getName());
    
    String orgName = 	propertyOrDefault(req, "orgName", "OU");
    String status = 	propertyOrDefault(req, "status", "new");
    String reportid = 	req.getParameter("reportid");    
    String latitude = 	req.getParameter("latitude");
    //String dc = 		req.getParameter("dc");
    String longitude = 	req.getParameter("longitude");
    String content = 	req.getParameter("content");
    String dateCaptured = req.getParameter("dateCaptured");

    Date date = new Date();  
    System.out.println("date captured: " + dateCaptured);
    //log.info("dc: " + dc);
    Date dateCreated = dateFromReport(dateCaptured);
    Date dateReceived = new Date(); //we've received it now :) 
    
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
    report.setProperty("dateCreated", dateCreated);
    report.setProperty("date", date);
    report.setProperty("status", status);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(report);

    if(user != null) {
    	resp.sendRedirect("/gainslapi.jsp?orgName=" + orgName);
    } else {
    	resp.sendRedirect("/reportList?orgName=" + orgName);
    }
  }
  
  private String propertyOrDefault(HttpServletRequest req, String propName, String defaultValue) {
	  String value = req.getParameter(propName);
	  if (value == null) {
		  value = defaultValue;
	  }
	  return value;
  }
  
  private Date dateFromReport(String dateString){
	 return new Date(Long.parseLong(dateString)); 
  }
}