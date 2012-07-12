/**
 *
 */
package edu.illinois.ncsa.versus.web.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.versus.core.adapter.AdapterDescriptor;
import edu.illinois.ncsa.versus.core.adapter.AdaptersClient;
import edu.illinois.ncsa.versus.core.extractor.ExtractorDescriptor;
import edu.illinois.ncsa.versus.core.extractor.ExtractorsClient;
import edu.illinois.ncsa.versus.core.measure.MeasureDescriptor;
import edu.illinois.ncsa.versus.core.measure.MeasuresClient;
import edu.illinois.ncsa.versus.web.client.RegistryService;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;
import gov.nist.itl.ssd.sampling.SamplerDescriptor;
import gov.nist.itl.ssd.sampling.SamplersClient;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class RegistryServiceImpl extends RemoteServiceServlet implements
        RegistryService {

    private static final String serviceUrl = PropertiesManager.getWebServicesUrl();

    private interface ZippedHelpStreamProvider {

        String getHelpSha1(String componentId);

        InputStream getZippedHelpStream(String componentId) throws IOException;
    }
    private static final ZippedHelpStreamProvider adapterHelpProvider = new ZippedHelpStreamProvider() {

        @Override
        public String getHelpSha1(String componentId) {
            return new AdaptersClient(serviceUrl).getAdapterHelpSha1(componentId);
        }

        @Override
        public InputStream getZippedHelpStream(String componentId) throws IOException {
            return new AdaptersClient(serviceUrl).getAdapterZippedHelp(componentId);
        }
    };

    private static final ZippedHelpStreamProvider extractorHelpProvider = new ZippedHelpStreamProvider() {

        @Override
        public String getHelpSha1(String componentId) {
            return new ExtractorsClient(serviceUrl).getExtractorHelpSha1(componentId);
        }

        @Override
        public InputStream getZippedHelpStream(String componentId) throws IOException {
            return new ExtractorsClient(serviceUrl).getExtractorZippedHelp(componentId);
        }
    };

    private static final ZippedHelpStreamProvider measureHelpProvider = new ZippedHelpStreamProvider() {

        @Override
        public String getHelpSha1(String componentId) {
            return new MeasuresClient(serviceUrl).getMeasureHelpSha1(componentId);
        }

        @Override
        public InputStream getZippedHelpStream(String componentId) throws IOException {
            return new MeasuresClient(serviceUrl).getMeasureZippedHelp(componentId);
        }
    };

    @Override
    public List<ComponentMetadata> getAdapters() {
        List<ComponentMetadata> adapters = new ArrayList<ComponentMetadata>();

        AdaptersClient adc = new AdaptersClient(serviceUrl);
        HashSet<String> adaptersId = adc.getAdapters();
        for (String id : adaptersId) {
            AdapterDescriptor adapterDescriptor = adc.getAdapterDescriptor(id);
            String category = getCategory(adapterDescriptor.getCategory());
            String helpLink = getHelpLink(adapterDescriptor.getId(),
                    adapterHelpProvider, adapterDescriptor.hasHelp());
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
            String category = getCategory(extractorDescriptor.getCategory());
            String helpLink = getHelpLink(extractorDescriptor.getType(),
                    extractorHelpProvider, extractorDescriptor.hasHelp());
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
            String category = getCategory(measureDescriptor.getCategory());
            String helpLink = getHelpLink(measureDescriptor.getType(),
                    measureHelpProvider, measureDescriptor.hasHelp());
            ComponentMetadata extractor = new ComponentMetadata(id,
                    measureDescriptor.getName(), "", category, helpLink);
            for (String feature : measureDescriptor.getSupportedFeatures()) {
                extractor.addSupportedInput(feature);
            }
            measures.add(extractor);
        }
        return measures;
    }

    @Override
    public List<ComponentMetadata> getSamplers() {
        List<ComponentMetadata> samplers = new ArrayList<ComponentMetadata>();

        SamplersClient sc = new SamplersClient(serviceUrl);
        HashSet<String> samplersId = sc.getSamplers();
        for (String id : samplersId) {
            SamplerDescriptor samplerDescriptor = sc.getSamplerDescriptor(id);
            String category = getCategory(samplerDescriptor.getCategory());
            String helpLink = getHelpLink(samplerDescriptor.getId(),
                    measureHelpProvider, samplerDescriptor.hasHelp());
            ComponentMetadata sampler = new ComponentMetadata(id,
                    samplerDescriptor.getName(), "", category, helpLink);
            samplers.add(sampler);
        }
        return samplers;
    }

    private String getCategory(String category) {
        return category == null || category.isEmpty() ? "Other" : category;
    }
    
    private String getHelpLink(String id, ZippedHelpStreamProvider helpProvider, boolean hasHelp) {
        if (!hasHelp) {
            return "";
        }

        String applicationPath = getServletContext().getRealPath("");
        File helpDirectory = new File(applicationPath, "help");
        if (!helpDirectory.isDirectory()) {
            if (helpDirectory.exists()) {
                helpDirectory.delete();
            }
            helpDirectory.mkdir();
        }

        File help = new File(helpDirectory, id);
        if (!help.isDirectory()) {
            if (help.exists()) {
                help.delete();
            }
            help.mkdir();
        }

        extractIfNeeded(id, helpProvider, help);
        String helpLink = "help/" + id + "/index.html";
        return new File(help, "index.html").exists() ? helpLink : "";
    }

    private void extractIfNeeded(String id, ZippedHelpStreamProvider helpProvider, File help) {
        File zipFile = new File(help, id + ".zip");

        boolean needExtraction = true;
        String sha1 = helpProvider.getHelpSha1(id);
        if (zipFile.exists()) {
            String existingHash = "";
            try {
                existingHash = Hasher.getHash(zipFile, "SHA1");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            needExtraction = !existingHash.equalsIgnoreCase(sha1);
        }

        if (!needExtraction) {
            return;
        }

        try {
            FileUtils.cleanDirectory(help);

            final int BUFFER = 2048;

            // Save the zip file on server
            InputStream is = null;
            FileOutputStream zipFos = null;
            try {
                is = helpProvider.getZippedHelpStream(id);
                zipFos = new FileOutputStream(zipFile);
                int len;
                byte buf[] = new byte[BUFFER];

                while ((len = is.read(buf)) > 0) {
                    zipFos.write(buf, 0, len);
                }
            } finally {
                if (is != null) {
                    is.close();
                }
                if (zipFos != null) {
                    zipFos.close();
                }
            }

            // Extract the zip file
            ZipInputStream helpZipped = null;
            try {
                helpZipped = new ZipInputStream(new FileInputStream(zipFile));

                ZipEntry entry;
                while ((entry = helpZipped.getNextEntry()) != null) {
                    File file = new File(help, entry.getName());
                    if (entry.isDirectory()) {
                        file.mkdirs();
                    } else {
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                        int count;
                        byte data[] = new byte[BUFFER];
                        while ((count = helpZipped.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.close();
                    }
                    helpZipped.closeEntry();
                }
            } finally {
                if (helpZipped != null) {
                    try {
                        helpZipped.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.WARNING, "Cannot extract help of " + id, ex);

            //Delete the eventual zip file, so that the extraction is rerun next time
            if (zipFile.exists()) {
                zipFile.delete();
            }
        }
    }
}
