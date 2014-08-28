package com.kwotabl.web;

import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitClientException;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Session management after login
 */
public class LoginSuccessServlet extends HttpServlet {

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
      IOException {
 // This check prevents the "/" handler from handling all requests by default
    if (!"/".equals(request.getServletPath())) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    try {

      GitkitClient gitkitClient = GitkitClient.createFromJson("gitkit-server-config.json");

      gitkitClient.validateTokenInRequest(request);

      response.sendRedirect("quotebook.html");
    } catch (FileNotFoundException | GitkitClientException | JSONException e) {
      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().print(e.toString());
    }
  }
  

}
