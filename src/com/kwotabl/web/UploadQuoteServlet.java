package com.kwotabl.web;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Saves uploaded quotes
 */
public class UploadQuoteServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    
    StringBuffer sb = new StringBuffer();
    String line = null;
    try {
      BufferedReader reader = req.getReader();
      while ((line = reader.readLine()) != null)
        sb.append(line);
    } catch (Exception e) { /*report an error*/ }

    JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
    
    String user = jsonObject.get("id_token").getAsString();
    String quote = jsonObject.get("quote").getAsString();
    String author = jsonObject.get("author").getAsString();
    
    Entity e = new Entity("Quote");
    e.setProperty("user", user);
    e.setProperty("quote", quote);
    e.setProperty("author", author);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(e);
  }
  
}
