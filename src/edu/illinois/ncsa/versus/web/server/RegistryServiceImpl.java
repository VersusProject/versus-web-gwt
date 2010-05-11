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

/**
 * @author lmarini
 *
 */
@SuppressWarnings("serial")
public class RegistryServiceImpl extends RemoteServiceServlet implements
		RegistryService {

	private static CompareRegistry registry = new CompareRegistry();
	
	@Override
	public List<String> getExtractors() {
		List<String> extractorTitles = new ArrayList<String>();
		Collection<Extractor> availableExtractors = registry.getAvailableExtractors();
		Iterator<Extractor> extractorIter = availableExtractors.iterator();
		while (extractorIter.hasNext()) {
			Extractor extractor = extractorIter.next();
			extractorTitles.add(extractor.getName());
		}
		Collections.sort(extractorTitles);
		return extractorTitles;
	}

	@Override
	public List<String> getMeasures() {
		List<String> measureTitles = new ArrayList<String>();
		Collection<Measure> availableMeasures = registry.getAvailableMeasures();
		Iterator<Measure> measureIter = availableMeasures.iterator();
		while (measureIter.hasNext()) {
			Measure measure = measureIter.next();
			measureTitles.add(measure.getName());
		}
		Collections.sort(measureTitles);
		return measureTitles;
	}

}
