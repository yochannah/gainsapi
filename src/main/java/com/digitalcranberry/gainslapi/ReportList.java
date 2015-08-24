package com.digitalcranberry.gainslapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;



public class ReportList extends HttpServlet{
	
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private Key reportStoreKey;
    private Query query;
    private List<Entity> reports;


		    public void doGet(HttpServletRequest req, HttpServletResponse resp)
		            throws IOException {
		  	  final Logger log = Logger.getLogger(ReportList.class.getName());    


		    	String orgName = req.getParameter("orgName");
		    	String resultsNum = req.getParameter("resultNum");
		    	String status = req.getParameter("status");
		    	int limit;

		    	reportStoreKey = KeyFactory.createKey("gainsl", orgName);
		        query = new Query("Report", reportStoreKey).addSort("lastUpdated", Query.SortDirection.DESCENDING);		        

		        Filter filter;
		    	
		    	if(resultsNum != null) {
		    		limit = Integer.parseInt(resultsNum);
		    	} else {
		    		limit = 35;
		    	}
		    	
		    	if(status != null) {
		    		log.info("status: " + status);
		    		filter = new FilterPredicate("status", FilterOperator.EQUAL, status);
		    		query.setFilter(filter);
		    	}
		    			    	
		        reports  = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(limit));

		        //the phone only stores current and unsent snapshots. no need to send all old states. 
		    	//saves loads of data transfer this way, too.
		        for(Entity rep : reports) {
		        	rep.removeProperty("previousStates");
		        }
		        
		        //convert it to Json
		        Gson gson = new GsonBuilder()
		        .setExclusionStrategies(new TestExclStrat())
		        .create();
		        
		        String reportString = gson.toJson(reports); 
		        
		        //send it to the user
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
