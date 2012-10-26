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
import java.util.Date;
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

    // Cache time out in milliseconds
    private static final long CACHE_TIMEOUT = 300000;

    private static final int getTimeout = 3;

    private static final int getRetry = 3;

    private final Object adaptersLock = new Object();

    private final List<ComponentMetadata> adaptersCache = new ArrayList<ComponentMetadata>();

    private Date lastAdaptersRefresh = new Date(0);

    private final Object extractorsLock = new Object();

    private final List<ComponentMetadata> extractorsCache = new ArrayList<ComponentMetadata>();

    private Date lastExtractorsRefresh = new Date(0);

    private final Object measuresLock = new Object();

    private final List<ComponentMetadata> measuresCache = new ArrayList<ComponentMetadata>();

    private Date lastMeasuresRefresh = new Date(0);

    private final Object samplersLock = new Object();

    private final List<ComponentMetadata> samplersCache = new ArrayList<ComponentMetadata>();

    private Date lastSamplersRefresh = new Date(0);

    private static final String serviceUrl = PropertiesManager.getWebServicesUrl();

    @Override
    public List<ComponentMetadata> getAdapters() {
        synchronized (adaptersLock) {
            Date now = new Date();
            if (now.getTime() - lastAdaptersRefresh.getTime() > CACHE_TIMEOUT) {
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Refreshing adapters cache");
                adaptersCache.clear();
                AdaptersClient adc = new AdaptersClient(serviceUrl, getTimeout, getRetry);
                HashSet<String> adaptersId = adc.getAdapters();
                for (String id : adaptersId) {
                    try {
                        AdapterDescriptor adapterDescriptor = adc.getAdapterDescriptor(id);
                        String category = getCategory(adapterDescriptor.getCategory());
                        String helpLink = getHelpLink(adapterDescriptor.getId(),
                                adapterHelpProvider, adapterDescriptor.hasHelp());
                        ComponentMetadata adapter = new ComponentMetadata(id,
                                adapterDescriptor.getName(), "", category, helpLink);
                        for (String mimeType : adapterDescriptor.getSupportedMediaTypes()) {
                            adapter.addSupportedInput(mimeType);
                        }
                        adaptersCache.add(adapter);
                    } catch (Exception e) {
                        Logger.getLogger(RegistryServiceImpl.class.getName()).
                                log(Level.WARNING, "Cannot get adapter " + id, e);
                    }
                }
                lastAdaptersRefresh = new Date();
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Found {0} adapters", adaptersCache.size());
            }
            return new ArrayList<ComponentMetadata>(adaptersCache);
        }
    }

    @Override
    public List<ComponentMetadata> getExtractors() {
        synchronized (extractorsLock) {
            Date now = new Date();
            if (now.getTime() - lastExtractorsRefresh.getTime() > CACHE_TIMEOUT) {
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Refreshing extractors cache");
                extractorsCache.clear();
                ExtractorsClient exc = new ExtractorsClient(serviceUrl, getTimeout, getRetry);
                HashSet<String> extractorsId = exc.getExtractors();
                for (String id : extractorsId) {
                    try {
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
                        extractorsCache.add(extractor);
                    } catch (Exception e) {
                        Logger.getLogger(RegistryServiceImpl.class.getName()).
                                log(Level.WARNING, "Cannot get extractor " + id, e);
                    }
                }
                lastExtractorsRefresh = new Date();
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Found {0} extractors", extractorsCache.size());
            }

            return new ArrayList<ComponentMetadata>(extractorsCache);
        }
    }

    @Override
    public List<ComponentMetadata> getMeasures() {
        synchronized (measuresLock) {
            Date now = new Date();
            if (now.getTime() - lastMeasuresRefresh.getTime() > CACHE_TIMEOUT) {
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Refreshing measures cache");
                measuresCache.clear();
                MeasuresClient mec = new MeasuresClient(serviceUrl, getTimeout, getRetry);
                HashSet<String> measuresId = mec.getMeasures();
                for (String id : measuresId) {
                    try {
                        MeasureDescriptor measureDescriptor = mec.getMeasureDescriptor(id);
                        String category = getCategory(measureDescriptor.getCategory());
                        String helpLink = getHelpLink(measureDescriptor.getType(),
                                measureHelpProvider, measureDescriptor.hasHelp());
                        ComponentMetadata extractor = new ComponentMetadata(id,
                                measureDescriptor.getName(), "", category, helpLink);
                        for (String feature : measureDescriptor.getSupportedFeatures()) {
                            extractor.addSupportedInput(feature);
                        }
                        measuresCache.add(extractor);
                    } catch (Exception e) {
                        Logger.getLogger(RegistryServiceImpl.class.getName()).
                                log(Level.WARNING, "Cannot get measure " + id, e);
                    }
                }
                lastMeasuresRefresh = new Date();
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Found {0} measures", measuresCache.size());
            }
            return new ArrayList<ComponentMetadata>(measuresCache);
        }
    }

    @Override
    public List<ComponentMetadata> getSamplers() {
        synchronized (samplersLock) {
            Date now = new Date();
            if (now.getTime() - lastSamplersRefresh.getTime() > CACHE_TIMEOUT) {
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Refreshing samplers cache");
                samplersCache.clear();
                SamplersClient sc = new SamplersClient(serviceUrl, getTimeout, getRetry);
                HashSet<String> samplersId = sc.getSamplers();
                for (String id : samplersId) {
                    try {
                        SamplerDescriptor samplerDescriptor = sc.getSamplerDescriptor(id);
                        String category = getCategory(samplerDescriptor.getCategory());
                        String helpLink = getHelpLink(samplerDescriptor.getId(),
                                measureHelpProvider, samplerDescriptor.hasHelp());
                        ComponentMetadata sampler = new ComponentMetadata(id,
                                samplerDescriptor.getName(), "", category, helpLink);
                        samplersCache.add(sampler);
                    } catch (Exception e) {
                        Logger.getLogger(RegistryServiceImpl.class.getName()).
                                log(Level.WARNING, "Cannot get sampler " + id, e);
                    }
                }
                lastSamplersRefresh = new Date();
                Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                        Level.INFO, "Found {0} samplers", samplersCache.size());
            }
            return new ArrayList<ComponentMetadata>(samplersCache);
        }
    }

    private String getCategory(String category) {
        return category == null || category.isEmpty() ? "Other" : category;
    }

    private interface ZippedHelpStreamProvider {

        String getHelpSha1(String componentId);

        InputStream getZippedHelpStream(String componentId) throws IOException;
    }
    private static final ZippedHelpStreamProvider adapterHelpProvider = new ZippedHelpStreamProvider() {
        @Override
        public String getHelpSha1(String componentId) {
            return new AdaptersClient(serviceUrl, getTimeout, getRetry).
                    getAdapterHelpSha1(componentId);
        }

        @Override
        public InputStream getZippedHelpStream(String componentId) throws IOException {
            return new AdaptersClient(serviceUrl, getTimeout, getRetry).
                    getAdapterZippedHelp(componentId);
        }
    };

    private static final ZippedHelpStreamProvider extractorHelpProvider = new ZippedHelpStreamProvider() {
        @Override
        public String getHelpSha1(String componentId) {
            return new ExtractorsClient(serviceUrl, getTimeout, getRetry).
                    getExtractorHelpSha1(componentId);
        }

        @Override
        public InputStream getZippedHelpStream(String componentId) throws IOException {
            return new ExtractorsClient(serviceUrl, getTimeout, getRetry).
                    getExtractorZippedHelp(componentId);
        }
    };

    private static final ZippedHelpStreamProvider measureHelpProvider = new ZippedHelpStreamProvider() {
        @Override
        public String getHelpSha1(String componentId) {
            return new MeasuresClient(serviceUrl, getTimeout, getRetry).
                    getMeasureHelpSha1(componentId);
        }

        @Override
        public InputStream getZippedHelpStream(String componentId) throws IOException {
            return new MeasuresClient(serviceUrl, getTimeout, getRetry).
                    getMeasureZippedHelp(componentId);
        }
    };

    private String getHelpLink(String id, ZippedHelpStreamProvider helpProvider, boolean hasHelp) {
        if (!hasHelp) {
            return "";
        }

        try {
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
        } catch (Exception e) {
            Logger.getLogger(RegistryServiceImpl.class.getName()).log(
                    Level.WARNING, "Cannot get help of " + id, e);
            return "";
        }
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
