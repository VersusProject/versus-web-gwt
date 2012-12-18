package edu.illinois.ncsa.versus.web.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;
import org.tupeloproject.rdf.Resource;

import edu.illinois.ncsa.mmdb.web.rest.AuthenticatedServlet;
import edu.illinois.ncsa.mmdb.web.server.BatchDownload;
import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.uiuc.ncsa.cet.bean.tupelo.DatasetBeanUtil;

/**
 * @author David Nimorwicz
 *
 */
public class SampleBatchDownload extends AuthenticatedServlet {
    private static final long serialVersionUID = 8540356072366902770L;
    Log                       log              = LogFactory.getLog(SampleBatchDownload.class);
    private boolean has_duplicate_files = false;

    /**
     * Handle POST request.<br>
     */
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (!authenticate(request, response)) {
            return;
        }

        String uris = request.getParameter("uri");
        String filename = request.getParameter("filename");
        
        //log.info("Bulk Download: downloading " + uri);
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment; filename=" + filename + ".zip");

        String[] uri_list = uris.split("&");
                
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        BeanSession beanSession = TupeloStore.getInstance().getBeanSession();
        DatasetBeanUtil dbu = new DatasetBeanUtil(beanSession);
       
        // HashMap<Key = URI, value = # of occurrences>
        HashMap<String, Integer> file_frequency_map = new HashMap<String, Integer>();
        
        for(String uri : uri_list){
        	if(file_frequency_map.containsKey(uri)){
        		has_duplicate_files = true;
        		file_frequency_map.put(uri, file_frequency_map.get(uri) + 1);
        	} else {
        		file_frequency_map.put(uri, 1);
        	}
        }
        
        for (Entry<String, Integer> uri : file_frequency_map.entrySet())
        {
        	try {
        		String file = dbu.get(uri.getKey()).getFilename();

        		log.info("Adding file: " + file + " URI: " + uri);
        		InputStream in = beanSession.fetchBlob(Resource.uriRef(uri.getKey()));

        		// Add ZIP entry to output stream.
        		out.putNextEntry(new ZipEntry(file));

        		// Transfer bytes from the file to the ZIP file
        		IOUtils.copy(in, out);

        		// Complete the entry
        		out.closeEntry();
        		in.close();
        		
        		// Handle duplicate image uri's with batch script
        		if(uri.getValue() > 1){
        			SampleBatchFileGenerator.addBatchAction(file, uri.getValue());
        		}

        	} catch (IOException e) {
        		log.info("failed reading file stream");
        	} catch (Exception e) {
        		log.info("fetch filename failed");
        	}

        }
        
        if(has_duplicate_files){
        	String batch_filename = "Win_DuplicateImages.bat";
        	String batch_file_contents = SampleBatchFileGenerator.WinBuildBatchFile();
        	
        	String lin_batch_filename = "Linux_DuplicateImages.sh";
        	String lin_batch_file_contents = SampleBatchFileGenerator.LinuxBuildBatchFile();

        	String read_me_filename = "ReadMe.txt";
        	String read_me_file_contents = SampleBatchFileGenerator.buildReadMeFile();
        	
        	// Add windows batch file
        	log.info("Adding file: " + batch_filename);
        	InputStream in = new ByteArrayInputStream(batch_file_contents.getBytes());
        	
        	out.putNextEntry(new ZipEntry(batch_filename));
        	IOUtils.copy(in, out);

        	// Add Linux batch File
        	log.info("Adding file: " + lin_batch_filename);
        	InputStream lin_in = new ByteArrayInputStream(lin_batch_file_contents.getBytes());
        	
        	out.putNextEntry(new ZipEntry(lin_batch_filename));
        	IOUtils.copy(lin_in, out);
        	
        	// Add readme file
        	log.info("Adding file: " + read_me_filename);
        	InputStream readme_in = new ByteArrayInputStream(read_me_file_contents.getBytes());
        	
        	out.putNextEntry(new ZipEntry(read_me_filename));
        	IOUtils.copy(readme_in, out);
    		
    		out.closeEntry();
    		in.close();
    		lin_in.close();
    		readme_in.close();
    		has_duplicate_files = false;
        }
        
        // Complete the ZIP file
        out.flush();
        out.close();

    }
}
