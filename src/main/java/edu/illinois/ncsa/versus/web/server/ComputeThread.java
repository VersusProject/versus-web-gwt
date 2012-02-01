/**
 * 
 */
package edu.illinois.ncsa.versus.web.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
import edu.illinois.ncsa.versus.web.shared.Job.ComparisonStatus;
import edu.illinois.ncsa.versus.web.shared.PairwiseComparison;
import edu.uiuc.ncsa.cet.bean.DatasetBean;

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

	private  PairwiseComparison pairwiseComparison;

	private String adapterID;

	private String extractionID;

	private String measureID;

	private final BeanSession beanSession;

	/**
	 * 
	 * @param pairwiseComparison
	 * @param comparison2 
	 * @param jobStatus
	 * @param beanSession 
	 */
	public ComputeThread(PairwiseComparison pairwiseComparison, Job job, BeanSession beanSession) {
		this.pairwiseComparison = pairwiseComparison;
		this.job = job;
		this.beanSession = beanSession;
		adapterID = pairwiseComparison.getAdapterId();
		extractionID = pairwiseComparison.getExtractorId();
		measureID = pairwiseComparison.getMeasureId();
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
			job.updateSimilarityValue(pairwiseComparison.getUri(), compare.getValue());
			log.debug("Done computing similarity between " + file1 + " and " + file2);
		} catch (Exception e1) {
            job.updateError(pairwiseComparison.getUri(), e1.getMessage());
			log.error("Error computing similarity between " + file1 + " and " 
					+ file2 + " [" + pairwiseComparison.getUri() + "]", e1);
		}
	}

	/**
	 * 
	 * @param file1
	 * @param file2
	 * @return comparison
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
