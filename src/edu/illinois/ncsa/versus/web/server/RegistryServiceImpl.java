/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
	
	@Override
	public List<ComponentMetadata> getExtractors() {
		List<ComponentMetadata> extractors = new ArrayList<ComponentMetadata>();
		Collection<Extractor> availableExtractors = registry.getAvailableExtractors();
		Iterator<Extractor> extractorIter = availableExtractors.iterator();
		while (extractorIter.hasNext()) {
			Extractor extractor = extractorIter.next();
			ComponentMetadata extractorMetadata = new ComponentMetadata(extractor.getClass().getName(), extractor.getName(), "");
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
			ComponentMetadata measureMetadata = new ComponentMetadata(measure.getClass().getName(), measure.getName(), "");
			measures.add(measureMetadata);
		}
		return measures;
	}

}
