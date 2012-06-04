/**
 *
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.versus.core.adapter.AdapterDescriptor;
import edu.illinois.ncsa.versus.core.adapter.AdaptersClient;
import edu.illinois.ncsa.versus.core.extractor.ExtractorDescriptor;
import edu.illinois.ncsa.versus.core.extractor.ExtractorsClient;
import edu.illinois.ncsa.versus.core.measure.MeasureDescriptor;
import edu.illinois.ncsa.versus.core.measure.MeasuresClient;
import edu.illinois.ncsa.versus.web.client.RegistryService;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class RegistryServiceImpl extends RemoteServiceServlet implements
        RegistryService {

    private static final String serviceUrl = PropertiesManager.getWebServicesUrl();
    
    @Override
    public List<ComponentMetadata> getAdapters() {
        List<ComponentMetadata> adapters = new ArrayList<ComponentMetadata>();

        AdaptersClient adc = new AdaptersClient(serviceUrl);
        HashSet<String> adaptersId = adc.getAdapters();
        for (String id : adaptersId) {
            AdapterDescriptor adapterDescriptor = adc.getAdapterDescriptor(id);
            String category = adapterDescriptor.getCategory().isEmpty()
                    ? "Other" : adapterDescriptor.getCategory();
            String helpLink = getHelpLink(adapterDescriptor.getId(), adapterDescriptor.hasHelp());
            ComponentMetadata adapter = new ComponentMetadata(id,
                    adapterDescriptor.getName(), "", category, helpLink);
            for (String mimeType : adapterDescriptor.getSupportedMediaTypes()) {
                adapter.addSupportedInput(mimeType);
            }
            adapters.add(adapter);
        }
        return adapters;
    }

    @Override
    public List<ComponentMetadata> getExtractors() {
        List<ComponentMetadata> extractors = new ArrayList<ComponentMetadata>();

        ExtractorsClient exc = new ExtractorsClient(serviceUrl);
        HashSet<String> extractorsId = exc.getExtractors();
        for (String id : extractorsId) {
            ExtractorDescriptor extractorDescriptor = exc.getExtractorDescriptor(id);
            String category = extractorDescriptor.getCategory().isEmpty()
                    ? "Other" : extractorDescriptor.getCategory();
            String helpLink = getHelpLink(extractorDescriptor.getType(), extractorDescriptor.hasHelp());
            ComponentMetadata extractor = new ComponentMetadata(id,
                    extractorDescriptor.getName(), "", category, helpLink);
            for (String adpater : extractorDescriptor.getSupportedAdapters()) {
                extractor.addSupportedInput(adpater);
            }
            extractor.addSupportedOutputs(extractorDescriptor.getSupportedFeature());
            extractors.add(extractor);
        }
        return extractors;
    }

    @Override
    public List<ComponentMetadata> getMeasures() {
        List<ComponentMetadata> measures = new ArrayList<ComponentMetadata>();

        MeasuresClient mec = new MeasuresClient(serviceUrl);
        HashSet<String> measuresId = mec.getMeasures();
        for (String id : measuresId) {
            MeasureDescriptor measureDescriptor = mec.getMeasureDescriptor(id);
            String category = measureDescriptor.getCategory().isEmpty()
                    ? "Other" : measureDescriptor.getCategory();
            String helpLink = getHelpLink(measureDescriptor.getType(), measureDescriptor.hasHelp());
            ComponentMetadata extractor = new ComponentMetadata(id,
                    measureDescriptor.getName(), "", category, helpLink);
            for (String feature : measureDescriptor.getSupportedFeatures()) {
                extractor.addSupportedInput(feature);
            }
            measures.add(extractor);
        }
        return measures;
    }

//    private String getHelpLink(HasHelp hasHelp, String name) {
//        String applicationPath = getServletContext().getRealPath("");
//        File helpDirectory = new File(applicationPath, "help");
//        if (!helpDirectory.isDirectory()) {
//            if (helpDirectory.exists()) {
//                helpDirectory.delete();
//            }
//            helpDirectory.mkdir();
//        }
//
//        File help = new File(helpDirectory, name);
//        if (!help.isDirectory()) {
//            if (help.exists()) {
//                help.delete();
//            }
//            help.mkdir();
//        }
//
//        extractIfNeeded(hasHelp, help, name);
//        String helpLink = "help/" + name + "/index.html";
//        return new File(help, "index.html").exists() ? helpLink : "";
//    }
//
//    private void extractIfNeeded(HasHelp hasHelp, File help, String name) {
//        boolean needExtraction = true;
//        String sha1 = hasHelp.getHelpSHA1();
//        File zipFile = new File(help, name + ".zip");
//        if (zipFile.exists()) {
//            String existingHash = "";
//            try {
//                existingHash = Hasher.getHash(zipFile, "SHA1");
//            } catch (NoSuchAlgorithmException ex) {
//                Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            needExtraction = !existingHash.equalsIgnoreCase(sha1);
//        }
//
//        if (!needExtraction) {
//            return;
//        }
//
//        try {
//            FileUtils.cleanDirectory(help);
//
//            final int BUFFER = 2048;
//
//            // Save the zip file on server
//            InputStream is = null;
//            FileOutputStream zipFos = null;
//            try {
//                is = hasHelp.getHelpZipped();
//                zipFos = new FileOutputStream(zipFile);
//                int len;
//                byte buf[] = new byte[BUFFER];
//
//                while ((len = is.read(buf)) > 0) {
//                    zipFos.write(buf, 0, len);
//                }
//            } finally {
//                if (is != null) {
//                    is.close();
//                }
//                if (zipFos != null) {
//                    zipFos.close();
//                }
//            }
//
//            // Extract the zip file
//            ZipInputStream helpZipped = null;
//            try {
//                helpZipped = new ZipInputStream(new FileInputStream(zipFile));
//
//                ZipEntry entry;
//                while ((entry = helpZipped.getNextEntry()) != null) {
//                    FileOutputStream fos = new FileOutputStream(new File(help, entry.getName()));
//                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
//                    int count;
//                    byte data[] = new byte[BUFFER];
//                    while ((count = helpZipped.read(data, 0, BUFFER)) != -1) {
//                        dest.write(data, 0, count);
//                    }
//                    dest.close();
//                }
//            } finally {
//                if (helpZipped != null) {
//                    try {
//                        helpZipped.close();
//                    } catch (IOException ex) {
//                        Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.WARNING, "Cannot extract help of " + name, ex);
//
//            //Delete the eventual zip file, so that the extraction is rerun next time
//            if (zipFile.exists()) {
//                zipFile.delete();
//            }
//        }
//    }
    private String getHelpLink(String id, boolean hasHelp) {
        return hasHelp ? id : "";
    }
}
