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
      return;
    }

    JsonObject jsonObject;
    GitkitUser gitkitUser;
    try {
      jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
      String idToken = jsonObject.get("id_token").getAsString();
      GitkitClient gitkitClient = 
          GitkitClient.createFromJson("gitkit-server-config.json");
      gitkitUser = gitkitClient.validateToken(idToken);
    } catch (JSONException e) {
      resp.sendError(HttpServletResponse.INTERNAL_SERVER_ERROR);
      return;
    } catch (GitkitClientException e) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
      return;
    }

    String quote = jsonObject.get("quote").getAsString();
    String author = jsonObject.get("author").getAsString();
    
    Entity e = new Entity("Quote");
    e.setProperty("user", gitkitUser.getLocalId());
    e.setProperty("quote", quote);
    e.setProperty("author", author);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(e);
    
    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
  }
  
}
