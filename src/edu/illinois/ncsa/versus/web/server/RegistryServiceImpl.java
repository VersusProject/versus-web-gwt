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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.extract.Extractor;
import edu.illinois.ncsa.versus.measure.Measure;
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
			ComponentMetadata extractorMetadata = new ComponentMetadata(
					extractor.getClass().getName(), extractor.getName(), "");
			for (Class<? extends Adapter> type : extractor.supportedAdapters()) {
				extractorMetadata.addSupportedInput(type.getName());
				log.debug("Extractor " + extractor.getClass().getName()
						+ " supports adapter " + type.getName());
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
			ComponentMetadata measureMetadata = new ComponentMetadata(measure
					.getClass().getName(), measure.getName(), "");
			measureMetadata.addSupportedInput(measure.getFeatureType());
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
			ComponentMetadata adapterMetadata = new ComponentMetadata(adapter
					.getClass().getName(), adapter.getName(), "");
			for (String mimeType : adapter.getSupportedMediaTypes()) {
				adapterMetadata.addSupportedInput(mimeType);
			}
			Class<? extends Adapter> adapterClass = adapter.getClass();
			Class<?>[] interfaces = adapterClass.getInterfaces();
			for (Class<?> interfaceClass : interfaces) {
				log.debug("Class " + adapterClass.getName() + " implements "
						+ interfaceClass.getName());
				adapterMetadata.addSupportedOutputs(interfaceClass.getName());
			}
			adapters.add(adapterMetadata);
		}
		return adapters;
	}

}
