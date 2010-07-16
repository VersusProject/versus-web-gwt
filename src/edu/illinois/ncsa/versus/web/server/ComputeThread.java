/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tupeloproject.kernel.BeanSession;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.rdf.Resource;

import edu.illinois.ncsa.versus.UnsupportedTypeException;
import edu.illinois.ncsa.versus.adapter.Adapter;
import edu.illinois.ncsa.versus.adapter.FileLoader;
import edu.illinois.ncsa.versus.descriptor.Descriptor;
import edu.illinois.ncsa.versus.extract.Extractor;
import edu.illinois.ncsa.versus.measure.Measure;
import edu.illinois.ncsa.versus.measure.Similarity;
import edu.illinois.ncsa.versus.web.shared.Job;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.illinois.ncsa.versus.web.shared.SetComparison;
import edu.uiuc.ncsa.cet.bean.DatasetBean;
import edu.uiuc.ncsa.cet.bean.tupelo.DatasetBeanUtil;

/**
 * @author Luigi Marini
 * 
 */
public class ComputeThread extends Thread {
	
	/** Commons logging **/
	private static Log				log	= LogFactory.getLog(ComputeThread.class);

	private  File				file1;

	private  File				file2;

	private  Adapter		adapter1;

	private  Extractor			extractor;

	private  Measure			measure;

	private  Adapter		adapter2;

	private  Job job;

	private  Map<String, String> jobStatus;

	private SetComparison comparison;

	private  PairwiseComparison pairwiseComparison;

	private String adapterID;

	private String extractionID;

	private String measureID;

	private final BeanSession beanSession;

	private final Set<PairwiseComparison> comparisons;

	/**
	 * 
	 * @param pairwiseComparison
	 * @param comparison2 
	 * @param jobStatus
	 * @param beanSession 
	 */
	public ComputeThread(PairwiseComparison pairwiseComparison, SetComparison comparison, Set<PairwiseComparison> comparisons, Map<String, String> jobStatus, BeanSession beanSession) {
		this.pairwiseComparison = pairwiseComparison;
		this.comparison = comparison;
		this.comparisons = comparisons;
		this.jobStatus = jobStatus;
		this.beanSession = beanSession;
		adapterID = comparison.getAdapterID();
		extractionID = comparison.getExtractionID();
		measureID = comparison.getMeasureID();
		try {
			log.debug("Selected adapter is " + adapterID);
			adapter1 = (Adapter) Class.forName(adapterID).newInstance();
			adapter2 = (Adapter) Class.forName(adapterID).newInstance();
			log.debug("Selected extractor is " + extractionID);
			extractor = (Extractor) Class.forName(extractionID).newInstance();
			log.debug("Selected measure is " + measureID);
			measure = (Measure) Class.forName(measureID).newInstance();
		} catch (InstantiationException e) {
			log.error("Error setting up compute thread", e);
		} catch (IllegalAccessException e) {
			log.error("Error setting up compute thread", e);
		} catch (ClassNotFoundException e) {
			log.error("Error setting up compute thread", e);
		}
		try {
			file1 = getFile(pairwiseComparison.getFirstDataset());
			file2 = getFile(pairwiseComparison.getSecondDataset());
		} catch (IOException e) {
			log.error("Error getting dataset blob", e);
		} catch (OperatorException e) {
			log.error("Error getting dataset blob", e);
		}
	}

	private File getFile(DatasetBean datasetBean) throws IOException, OperatorException {
		File file = File.createTempFile( "versus", ".tmp" );
        FileOutputStream fos = new FileOutputStream( file );
        byte[] buf = new byte[10240];
        int len;
        InputStream is = beanSession.fetchBlob( Resource.uriRef( datasetBean.getUri() ) );
        while ( (len = is.read( buf )) > 0 ) {
            fos.write( buf, 0, len );
        }
        is.close();
        fos.close();
		return file;
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			Similarity compare = compare(file1, file2);
			for (PairwiseComparison comparison : comparisons) {
				if (comparison.getFirstDataset().getUri().equals(pairwiseComparison.getFirstDataset().getUri()) && 
						comparison.getSecondDataset().getUri().equals(pairwiseComparison.getSecondDataset().getUri())) {
					comparison.setSimilarity(Double.toString(compare.getValue()));
				}
			}
		} catch (Exception e1) {
			log.error("Error computing similarity between " + file1 + " and " + file2, e1);
		}
	}

	/**
	 * 
	 * @param file1
	 * @param file2
	 * @return
	 * @throws Exception
	 */
	private Similarity compare(File file1, File file2) throws Exception {

		if ((adapter1 instanceof FileLoader) && (adapter2 instanceof FileLoader)) {
			FileLoader fileLoaderAdapter = (FileLoader) adapter1;
			fileLoaderAdapter.load(file1);
			Descriptor feature1 = extractor.extract(fileLoaderAdapter);
			FileLoader fileLoaderAdapter2 = (FileLoader) adapter2;
			fileLoaderAdapter2.load(file2);
			Descriptor feature2 = extractor.extract(fileLoaderAdapter2);
			Similarity value = measure.compare(feature1, feature2);
			log.debug("Compared " + file1.getName() + " with " + file2.getName() + " = " + value.getValue());
			return value;
		} else {
			throw new UnsupportedTypeException();
		}
	}
}
