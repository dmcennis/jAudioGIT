package jAudioFeatureExtractor;

import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.ACE.XMLParsers.XMLDocumentParser;
import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.Aggregators.AggregatorContainer;
import jAudioFeatureExtractor.AudioFeatures.*;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;
import jAudioFeatureExtractor.jAudioTools.AudioMethodsPlayback;
//import jAudioFeatureExtractor.jAudioTools.AudioSamples;
import jAudioFeatureExtractor.jAudioTools.FeatureProcessor;

/**
 * All components that are not tightly tied to GUI. Used by console interface as
 * well as the GUI interface.
 * 
 * @author Daniel McEnnis
 */
public class DataModel {

	/**
	 * Reference to use for piping progress updates
	 */
	public ModelListener ml_;

	/**
	 * Handle for killing in-progress analysis
	 */
	public Cancel cancel_;

	/**
	 * list of which features are enabled by default
	 */
	public boolean[] defaults;

	/**
	 * list of all features available
	 */
	public FeatureExtractor[] features;

	/**
	 * Mapping between aggregator names and aggregator prototypes
	 */
	public java.util.HashMap<String, Aggregator> aggregatorMap;

	/**
	 * List of aggreggators to apply
	 * <p>
	 * Must be set externally. Duplicates of a class are permitted (hence not a
	 * map) but each entry in the array must be fully initialized prior to
	 * calling extract().
	 */
	public Aggregator[] aggregators;
	
	/**
	 * wrapper object for the aggregators.  This reference is null until a file extraction has been performed.
	 */
	public AggregatorContainer container = null;

	/**
	 * whether or a feature is a derived feature or not
	 */
	public boolean[] is_primary;

	/**
	 * cached FeatureDefinitions for all available features
	 */
	public FeatureDefinition[] featureDefinitions;

	/**
	 * info on all recordings that are made avaiable for feature extraction
	 */
	public RecordingInfo[] recordingInfo;

	/**
	 * thread for playing back a recording
	 */
	public AudioMethodsPlayback.PlayThread playback_thread;

	Updater updater = null;

	public OutputStream featureKey = null;

	public OutputStream featureValue = null;

	/**
	 * Initializes each of the arrays with all available efeatures. Place to add
	 * new features.
	 * 
	 * @param ml
	 *            reference to a controller that will handle table updates.
	 */
	public DataModel(String featureXMLLocation, ModelListener ml) {
		ml_ = ml;
		cancel_ = new Cancel();
		LinkedList<MetaFeatureFactory> metaExtractors = new LinkedList<MetaFeatureFactory>();

		metaExtractors.add(new Derivative());
		metaExtractors.add(new Mean());
		metaExtractors.add(new StandardDeviation());
		metaExtractors.add(new Derivative(new Mean()));
		metaExtractors.add(new Derivative(new StandardDeviation()));

		LinkedList<FeatureExtractor> extractors = new LinkedList<FeatureExtractor>();
		LinkedList<Boolean> def = new LinkedList<Boolean>();
		aggregatorMap = new java.util.HashMap<String, Aggregator>();

		// extractors.add(new AreaMoments());
		// def.add(false);
		// extractors.add(new BeatHistogram());
		// def.add(false);
		// extractors.add(new BeatHistogramLabels());
		// def.add(false);
		// extractors.add(new BeatSum());
		// def.add(false);
		// extractors.add(new Compactness());
		// def.add(true);
		// extractors.add(new FFTBinFrequencies());
		// def.add(false);
		// extractors.add(new FractionOfLowEnergyWindows());
		// def.add(true);
		// extractors.add(new HarmonicSpectralCentroid());
		// def.add(false);
		// extractors.add(new HarmonicSpectralFlux());
		// def.add(false);
		// extractors.add(new HarmonicSpectralSmoothness());
		// def.add(false);
		// extractors.add(new LPC());
		// def.add(false);
		// extractors.add(new MagnitudeSpectrum());
		// def.add(false);
		// extractors.add(new MFCC());
		// def.add(true);
		// extractors.add(new Moments());
		// def.add(true);
		// extractors.add(new PeakFinder());
		// def.add(false);
		// extractors.add(new PowerSpectrum());
		// def.add(false);
		// extractors.add(new RelativeDifferenceFunction());
		// def.add(false);
		// extractors.add(new RMS());
		// def.add(true);
		// extractors.add(new SpectralCentroid());
		// def.add(true);
		// extractors.add(new SpectralFlux());
		// def.add(true);
		// extractors.add(new SpectralRolloffPoint());
		// def.add(true);
		// extractors.add(new SpectralVariability());
		// def.add(false);
		// extractors.add(new StrengthOfStrongestBeat());
		// def.add(false);
		// extractors.add(new StrongestBeat());
		// def.add(false);
		// extractors.add(new StrongestFrequencyVariability());
		// def.add(false);
		// extractors.add(new StrongestFrequencyViaFFTMax());
		// def.add(false);
		// extractors.add(new StrongestFrequencyViaSpectralCentroid());
		// def.add(false);
		// extractors.add(new StrongestFrequencyViaZeroCrossings());
		// def.add(false);
		// extractors.add(new ZeroCrossings());
		// def.add(true);
		try {

			Object[] lists = (Object[]) XMLDocumentParser.parseXMLDocument(
					featureXMLLocation, "feature_list");
			extractors = (LinkedList<FeatureExtractor>) lists[0];
			def = (LinkedList<Boolean>) lists[1];
			Aggregator[] aggArray = ((LinkedList<Aggregator>) lists[2])
					.toArray(new Aggregator[] {});

			for (int i = 0; i < aggArray.length; ++i) {
				aggregatorMap.put(aggArray[i].getAggregatorDefinition().name,
						aggArray[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		populateMetaFeatures(metaExtractors, extractors, def);

	}

	void populateMetaFeatures(LinkedList<MetaFeatureFactory> listMFF,
			LinkedList<FeatureExtractor> listFE, LinkedList<Boolean> def) {
		LinkedList<Boolean> tmpDefaults = new LinkedList<Boolean>();
		LinkedList<FeatureExtractor> tmpFeatures = new LinkedList<FeatureExtractor>();
		LinkedList<Boolean> isPrimaryList = new LinkedList<Boolean>();
		Iterator<FeatureExtractor> lFE = listFE.iterator();
		Iterator<Boolean> lD = def.iterator();
		while (lFE.hasNext()) {
			FeatureExtractor tmpF = lFE.next();
			Boolean tmpB = lD.next();
			tmpFeatures.add(tmpF);
			tmpDefaults.add(tmpB);
			isPrimaryList.add(new Boolean(true));
			tmpF.setParent(this);
			if (tmpF.getFeatureDefinition().dimensions != 0) {
				Iterator<MetaFeatureFactory> lM = listMFF.iterator();
				while (lM.hasNext()) {
					MetaFeatureFactory tmpMFF = lM.next();
					FeatureExtractor tmp = tmpMFF
							.defineFeature((FeatureExtractor) tmpF.clone());
					tmp.setParent(this);
					tmpFeatures.add(tmp);
					tmpDefaults.add(new Boolean(false));
					isPrimaryList.add(new Boolean(false));
				}
			}
		}
		this.features = tmpFeatures.toArray(new FeatureExtractor[1]);
		Boolean[] defaults_temp = tmpDefaults.toArray(new Boolean[1]);
		Boolean[] is_primary_temp = isPrimaryList.toArray(new Boolean[] {});
		this.defaults = new boolean[defaults_temp.length];
		is_primary = new boolean[defaults_temp.length];
		for (int i = 0; i < this.defaults.length; i++) {
			this.defaults[i] = defaults_temp[i].booleanValue();
			is_primary[i] = is_primary_temp[i].booleanValue();
		}
		this.featureDefinitions = new FeatureDefinition[this.defaults.length];
		for (int i = 0; i < this.featureDefinitions.length; ++i) {
			this.featureDefinitions[i] = features[i].getFeatureDefinition();
		}

	}

	/**
	 * This is the function called when features change in such a way as the
	 * main display becomes out of date. WHen executed from the consol, this
	 * value is null.
	 */
	public void updateTable() {
		if (ml_ != null) {
			ml_.updateTable();
		}
	}

	/**
	 * Function for executing the feature extraction process against a set of
	 * files.
	 * 
	 * @param windowSize
	 *            Size of the window in samples
	 * @param windowOverlap
	 *            Percent of the window to be overlapped - must be between 0 and
	 *            1.
	 * @param samplingRate
	 *            Sample rate given in samples per second
	 * @param normalise
	 *            indicates whether or not the file should be normalised before
	 *            feature extraction
	 * @param perWindowStats
	 *            should features be extracted for every window
	 * @param overallStats
	 *            should features be extracted over the entire window
	 * @param destinationFV
	 *            file where the extracted features should be stored
	 * @param destinationFK
	 *            file where descriptions of features extracted should be stored
	 * @param info
	 *            list of the files that are to be analyzed
	 * @param arff
	 *            output format of the data
	 * @throws Exception
	 */
	public void extract(int windowSize, double windowOverlap,
			double samplingRate, boolean normalise, boolean perWindowStats,
			boolean overallStats, RecordingInfo[] info, int arff)
			throws Exception {
		// Get the control parameters
		boolean save_features_for_each_window = perWindowStats;
		boolean save_overall_recording_features = overallStats;
		int window_size = windowSize;
		double window_overlap = windowOverlap;
		double sampling_rate = samplingRate;
		int outputType = arff;
		// Get the audio recordings to extract features from and throw an
		// exception
		// if there are none
		RecordingInfo[] recordings = info;
		if (recordings == null)
			throw new Exception(
					"No recordings available to extract features from.");

		if (updater != null) {
			updater.setNumberOfFiles(recordings.length);
		}

		container = new AggregatorContainer();
		if((aggregators==null)||(aggregators.length==0)){
			aggregators = new Aggregator[3];
			aggregators[0]=new jAudioFeatureExtractor.Aggregators.Mean();
			aggregators[1]=new jAudioFeatureExtractor.Aggregators.StandardDeviation();
			aggregators[2]=new jAudioFeatureExtractor.Aggregators.AreaMoments();
			aggregators[2].setParameters(new String[]{"Area Method of Moments of MFCCs"},new String[]{""});
		}
		container.add(aggregators);

		// Prepare to extract features
		FeatureProcessor processor = new FeatureProcessor(window_size,
				window_overlap, sampling_rate, normalise, this.features,
				this.defaults, save_features_for_each_window,
				save_overall_recording_features, featureValue, featureKey,
				outputType, cancel_, container);

		// Extract features from recordings one by one and save them in XML
		// files
//		AudioSamples recording_content;
		for (int i = 0; i < recordings.length; i++) {
			File load_file = new File(recordings[i].file_path);
			if (updater != null) {
				updater.announceUpdate(i, 0);
			}
			processor.extractFeatures(load_file, updater);
		}

		// Finalize saved XML files

		processor.finalize();

		// JOptionPane.showMessageDialog(null,
		// "Features successfully extracted and saved.", "DONE",
		// JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Establish a listener for periodic updates on the feature extraction
	 * progress.
	 * 
	 * @param u
	 */
	public void setUpdater(Updater u) {
		this.updater = u;
	}

	public void validateFile(String definitions, String values)
			throws Exception {
		File feature_values_save_file = new File(values);
		File feature_definitions_save_file = new File(definitions);

		// Throw an exception if the given file paths are not writable. Involves
		// creating a blank file if one does not already exist.
		if (feature_values_save_file.exists())
			if (!feature_values_save_file.canWrite())
				throw new Exception("Cannot write to " + values + ".");
		if (feature_definitions_save_file.exists())
			if (!feature_definitions_save_file.canWrite())
				throw new Exception("Cannot write to " + definitions + ".");

	}

}
