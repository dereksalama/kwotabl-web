package com.kwotabl.web;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gwt.json.client.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    JSONArray jArray = new JSONArray();
    for (Entity result : pq.asIterable()) {
      JSONObject jObj = new JSONObject();
      
      try {
        jObj.put("quote", result.getProperty("quote"));
        jObj.put("author", result.getProperty("author"));

      } catch (JSONException e) {
        // TODO(dereksalama): Auto-generated catch block
        e.printStackTrace();
      }
    }
 
  }

}
