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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.descriptor.Descriptor;
import edu.illinois.ncsa.versus.extract.Extractor;
import edu.illinois.ncsa.versus.measure.Measure;
import edu.illinois.ncsa.versus.registry.CompareRegistry;
import edu.illinois.ncsa.versus.utility.HasCategory;
import edu.illinois.ncsa.versus.utility.HasHelp;
import edu.illinois.ncsa.versus.utility.Hasher;
import edu.illinois.ncsa.versus.web.client.RegistryService;
import edu.illinois.ncsa.versus.web.shared.ComponentMetadata;

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class RegistryServiceImpl extends RemoteServiceServlet implements
        RegistryService {

    private static CompareRegistry registry = new CompareRegistry();
    private static Log log = LogFactory.getLog(RegistryServiceImpl.class);

    @Override
    public List<ComponentMetadata> getAdapters() {
        List<ComponentMetadata> adapters = new ArrayList<ComponentMetadata>();
        Collection<Adapter> availableAdapters = registry.getAvailableAdapters();
        Iterator<Adapter> adapterIter = availableAdapters.iterator();
        while (adapterIter.hasNext()) {
            Adapter adapter = adapterIter.next();
            String category = "Other";
            if (adapter instanceof HasCategory) {
                category = ((HasCategory) adapter).getCategory();
            }
            String helpLink = "";
            if (adapter instanceof HasHelp) {
                helpLink = getHelpLink((HasHelp) adapter, adapter.getClass().getName());
            }
            ComponentMetadata adapterMetadata = new ComponentMetadata(adapter.getClass().getName(), adapter.getName(), "", category, helpLink);
            for (String mimeType : adapter.getSupportedMediaTypes()) {
                adapterMetadata.addSupportedInput(mimeType);
            }

            for (Extractor extractor : registry.getAvailableExtractors(adapter)) {
                adapterMetadata.addSupportedOutputs(extractor.getClass().getName());
                log.debug("Adapter " + adapter.getClass().getName()
                        + " is supported by extractor " + extractor.getClass().getName());
            }
            adapters.add(adapterMetadata);
        }
        return adapters;
    }

    @Override
    public List<ComponentMetadata> getExtractors() {
        List<ComponentMetadata> extractors = new ArrayList<ComponentMetadata>();
        Collection<Extractor> availableExtractors = registry.getAvailableExtractors();
        Iterator<Extractor> extractorIter = availableExtractors.iterator();
        while (extractorIter.hasNext()) {
            Extractor extractor = extractorIter.next();
            String category = "Other";
            if (extractor instanceof HasCategory) {
                category = ((HasCategory) extractor).getCategory();
            }
            String helpLink = "";
            if (extractor instanceof HasHelp) {
                helpLink = getHelpLink((HasHelp) extractor, extractor.getClass().getName());
            }
            ComponentMetadata extractorMetadata = new ComponentMetadata(
                    extractor.getClass().getName(), extractor.getName(), "", category, helpLink);

            for (Adapter adapter : registry.getAvailableAdapters(extractor)) {
                extractorMetadata.addSupportedInput(adapter.getClass().getName());
                log.debug("Extractor " + extractor.getClass().getName()
                        + " supports adapter " + adapter.getClass().getName());
            }
            extractorMetadata.addSupportedOutputs(extractor.getFeatureType().getName());
            extractors.add(extractorMetadata);
        }
        return extractors;
    }

    @Override
    public List<ComponentMetadata> getMeasures() {
        List<ComponentMetadata> measures = new ArrayList<ComponentMetadata>();
        Collection<Measure> availableMeasures = registry.getAvailableMeasures();
        Iterator<Measure> measureIter = availableMeasures.iterator();
        while (measureIter.hasNext()) {
            Measure measure = measureIter.next();
            String category = "Other";
            if (measure instanceof HasCategory) {
                category = ((HasCategory) measure).getCategory();
            }
            String helpLink = "";
            if (measure instanceof HasHelp) {
                helpLink = getHelpLink((HasHelp) measure, measure.getClass().getName());
            }
            ComponentMetadata measureMetadata = new ComponentMetadata(measure.getClass().getName(), measure.getName(), "", category, helpLink);
            for (Class<? extends Descriptor> feature : measure.supportedFeaturesTypes()) {
                measureMetadata.addSupportedInput(feature.getName());
                log.debug("Measure " + measure.getClass().getName() + " supports feature " + feature.getName());
            }
            measures.add(measureMetadata);
        }
        return measures;
    }

    private String getHelpLink(HasHelp hasHelp, String name) {
        String applicationPath = getServletContext().getRealPath("");
        File helpDirectory = new File(applicationPath, "help");
        if (!helpDirectory.isDirectory()) {
            if (helpDirectory.exists()) {
                helpDirectory.delete();
            }
            helpDirectory.mkdir();
        }

        File help = new File(helpDirectory, name);
        if (!help.isDirectory()) {
            if (help.exists()) {
                help.delete();
            }
            help.mkdir();
        }

        extractIfNeeded(hasHelp, help, name);
        String helpLink = "help/" + name + "/index.html";
        return new File(help, "index.html").exists() ? helpLink : "";
    }

    private void extractIfNeeded(HasHelp hasHelp, File help, String name) {
        boolean needExtraction = true;
        String sha1 = hasHelp.getHelpSHA1();
        File zipFile = new File(help, name + ".zip");
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
                is = hasHelp.getHelpZipped();
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
                    FileOutputStream fos = new FileOutputStream(new File(help, entry.getName()));
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                    int count;
                    byte data[] = new byte[BUFFER];
                    while ((count = helpZipped.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.close();
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
            Logger.getLogger(RegistryServiceImpl.class.getName()).log(Level.WARNING, "Cannot extract help of " + name, ex);

            //Delete the eventual zip file, so that the extraction is rerun next time
            if (zipFile.exists()) {
                zipFile.delete();
            }
        }
    }
}
