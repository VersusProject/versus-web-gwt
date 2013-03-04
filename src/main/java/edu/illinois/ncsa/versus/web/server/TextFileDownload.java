/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.server;

import java.io.IOException;
import edu.illinois.ncsa.mmdb.web.rest.AuthenticatedServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author antoinev
 */
public class TextFileDownload extends AuthenticatedServlet {

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!authenticate(request, response)) {
            return;
        }

        String fileName = request.getParameter("fileName");
        String fileExtension = request.getParameter("fileExtension");
        String fileContent = request.getParameter("fileContent");

        response.setContentType("txt/plain");
        response.addHeader("Content-Disposition", "attachment; filename="
                + fileName + '.' + fileExtension);

        ServletOutputStream outputStream = response.getOutputStream();
        try {
            outputStream.print(fileContent);
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }
}
