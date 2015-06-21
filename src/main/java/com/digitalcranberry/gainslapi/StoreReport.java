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
    
    String orgName = req.getParameter("orgName");
    
    if(orgName == null) {
    	orgName = "OU";
    }
    
    log.info(orgName);
    
    String latitude = req.getParameter("latitude");
    String longitude = req.getParameter("longitude");
    String content = req.getParameter("content");

    Date date = new Date();

    
    Key reportStoreKey = KeyFactory.createKey("gainsl", orgName);
    
    Entity report = new Entity("Report", reportStoreKey);
    if (user != null) {
      report.setProperty("author_id", user.getUserId());
      report.setProperty("author_email", user.getEmail());
    }
    
    report.setProperty("latitude", latitude);
    report.setProperty("longitude", longitude);
    report.setProperty("content", content);
   
    report.setProperty("date", date);
    report.setProperty("status", "new");
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(report);

    if(user != null) {
    	resp.sendRedirect("/gainslapi.jsp?orgName=" + orgName);
    } else {
    	resp.sendRedirect("/reportList?orgName=" + orgName);
    }
  }
}