package com.kwotabl.web;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Download quotes for user
 */
public class DownloadQuoteServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    
    String user = req.getParameter("id_token");
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Filter userFilter = new FilterPredicate("user", FilterOperator.EQUAL, user);
    
    Query q = new Query("Quote").setFilter(userFilter);
    
    PreparedQuery pq = datastore.prepare(q);

    List<JsonObject> result = new ArrayList<JsonObject>();
    for (Entity en : pq.asIterable()) {
      JsonObject currentQuote = new JsonObject();
      
      currentQuote.addProperty("quote", (String) en.getProperty("quote"));
      currentQuote.addProperty("author", (String) en.getProperty("author"));
      result.add(currentQuote);
    }
 
  }

}
