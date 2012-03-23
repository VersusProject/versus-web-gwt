/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.descriptor.Descriptor;
import edu.illinois.ncsa.versus.extract.Extractor;
import edu.illinois.ncsa.versus.measure.Measure;
import edu.illinois.ncsa.versus.category.HasCategory;
import edu.illinois.ncsa.versus.registry.CompareRegistry;
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
	public List<ComponentMetadata> getExtractors() {
		List<ComponentMetadata> extractors = new ArrayList<ComponentMetadata>();
		Collection<Extractor> availableExtractors = registry
				.getAvailableExtractors();
		Iterator<Extractor> extractorIter = availableExtractors.iterator();
		while (extractorIter.hasNext()) {
			Extractor extractor = extractorIter.next();
                        String category = "Other";
			if(extractor instanceof HasCategory) {
				category = ((HasCategory) extractor).getCategory();
			}
			ComponentMetadata extractorMetadata = new ComponentMetadata(
					extractor.getClass().getName(), extractor.getName(), "", category);
			
			for (Adapter adapter : registry.getAvailableAdapters(extractor)) {
				extractorMetadata.addSupportedInput(adapter.getClass().getName());
				log.debug("Extractor " + extractor.getClass().getName()
						+ " supports adapter " + adapter.getClass().getName());
			}
			extractorMetadata.addSupportedOutputs(extractor.getFeatureType()
					.getName());
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
			if(measure instanceof HasCategory) {
				category = ((HasCategory) measure).getCategory();
			}
			ComponentMetadata measureMetadata = new ComponentMetadata(measure
					.getClass().getName(), measure.getName(), "", category);
			for(Class<? extends Descriptor> feature : measure.supportedFeaturesTypes()) {
				measureMetadata.addSupportedInput(feature.getName());
				log.debug("Measure " + measure.getClass().getName() + " supports feature " + feature.getName());
			}
			measures.add(measureMetadata);
		}
		return measures;
	}

	@Override
	public List<ComponentMetadata> getAdapters() {
		List<ComponentMetadata> adapters = new ArrayList<ComponentMetadata>();
		Collection<Adapter> availableAdapters = registry.getAvailableAdapters();
		Iterator<Adapter> adapterIter = availableAdapters.iterator();
		while (adapterIter.hasNext()) {
			Adapter adapter = adapterIter.next();
                        String category = "Other";
			if(adapter instanceof HasCategory) {
				category = ((HasCategory) adapter).getCategory();
			}
			ComponentMetadata adapterMetadata = new ComponentMetadata(adapter
					.getClass().getName(), adapter.getName(), "", category);
			for (String mimeType : adapter.getSupportedMediaTypes()) {
				adapterMetadata.addSupportedInput(mimeType);
			}
			
			for(Extractor extractor : registry.getAvailableExtractors(adapter)) {
				adapterMetadata.addSupportedOutputs(extractor.getClass().getName());
				log.debug("Adapter " + adapter.getClass().getName() + 
						" is supported by extractor " + extractor.getClass().getName());
			}
			adapters.add(adapterMetadata);
		}
		return adapters;
	}

}
