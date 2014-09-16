package com.kwotabl.web;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
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
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    
    /*
    String checksumInput = req.getHeader("X-CHECKSUM");
    try {
      String reqUrl = ChecksumUtil.getFullURL(req);
      String checksum = ChecksumUtil.makeCheck(reqUrl.getBytes());
      if (!checksum.equals(checksumInput)) {
        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
    */
    
    StringBuffer sb = new StringBuffer();
    String line = null;
    try {
      BufferedReader reader = req.getReader();
      while ((line = reader.readLine()) != null)
        sb.append(line);
    } catch (Exception e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
        e.getMessage());
    }

    try {
      JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
      String idToken = jsonObject.get("id_token").getAsString();
      GitkitClient gitkitClient = 
          GitkitClient.createFromJson("gitkit-server-config.json");
      GitkitUser gitkitUser = gitkitClient.validateToken(idToken);
    } catch (JSONException e) {
      resp.sendError(HttpServletResponse.INTERNAL_SERVER_ERROR);
    } catch (GitkitClientException e) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }

    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Filter userFilter = new FilterPredicate(gitkitUser.getLocalId(),
        FilterOperator.EQUAL, user);
    
    Query q = new Query("Quote").setFilter(userFilter);
    
    PreparedQuery pq = datastore.prepare(q);

    List<QuoteResponseData> result = new ArrayList<>();
    for (Entity en : pq.asIterable()) {
      String quote = (String) en.getProperty("quote");
      String author = (String) en.getProperty("author");
      
      QuoteResponseData currentQuote = new QuoteResponseData(author, quote);
      result.add(currentQuote);
    }
    
    resp.setContentType("application/json");
    
    Gson gson = new Gson();
    resp.getWriter().print(gson.toJson(result));
    
    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    resp.getWriter().flush();
  }
}
