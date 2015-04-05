package com.digitalcranberry.gainslapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class ReportList extends HttpServlet{
	
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private Key reportStoreKey;
    private Query query;
    private List<Entity> reports;


		    public void doGet(HttpServletRequest req, HttpServletResponse resp)
		            throws IOException {

		    	String orgName = req.getParameter("orgName");
		    	
		    	reportStoreKey = KeyFactory.createKey("gainsl", orgName);
		        query = new Query("Report", reportStoreKey).addSort("date", Query.SortDirection.DESCENDING);		        
		        reports  = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(15));

		     /*  
		        report.setProperty("latitude", latitude);
		        report.setProperty("longitude", longitude);
		        report.setProperty("content", content);
		       
		        report.setProperty("date", date);
		        report.setProperty("status", "new");
		       */ 
		        Gson gson = new GsonBuilder()
		        .setExclusionStrategies(new TestExclStrat())
		        .create();
		        
		        String reportString = gson.toJson(reports); 

		        resp.setStatus(HttpServletResponse.SC_OK);
		        resp.setContentType("text/json");

		        PrintWriter out = resp.getWriter();
		        out.print(reportString);
		    }
		    
		    public class TestExclStrat implements ExclusionStrategy {

		        public boolean shouldSkipClass(Class<?> arg0) {
		            return false;
		        }

				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					return (f.getName().equals("key"));
				}

		    }
	}
