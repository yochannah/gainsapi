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
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StoreReport extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    String orgName = req.getParameter("orgName");
    Key reportStoreKey = KeyFactory.createKey("gainsl", orgName);
    String content = req.getParameter("content");
    Date date = new Date();
    Entity report = new Entity("Report", reportStoreKey);
    if (user != null) {
      report.setProperty("author_id", user.getUserId());
      report.setProperty("author_email", user.getEmail());
    }
    report.setProperty("date", date);
    report.setProperty("content", content);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(report);

    resp.sendRedirect("/gainslapi.jsp?orgName=" + orgName);
  }
}