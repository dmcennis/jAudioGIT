import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;

import jAudioFeatureExtractor.CommandLineThread;
import jAudioFeatureExtractor.DataModel;
import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.ACE.XMLParsers.XMLDocumentParser;
import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.DataTypes.RecordingInfo;

public class JAudioCommandLine {

	private static final String usage = "USAGE: "
			+ System.getProperty("line.separator")
			+ "JAudio -s <settings.xml> <destination xml file> <audiofiles>+"
			+ System.getProperty("line.separator")
			+ "JAudio -b <batchfile.xml>";

	/**
	 * @param args
	 */
	public static void execute(String[] args) {
		if (args[0].equals("-b")) {
			File batch = new File(args[1]);
			if (!batch.exists()) {
				System.out.println("Batch file '" + args[1]
						+ "' does not exist");
				System.exit(2);
			} else {

				Object[] o = new Object[] {};
				try {
					o = (Object[]) XMLDocumentParser.parseXMLDocument(args[1],
							"batchFile");
				} catch (Exception e) {
					System.out.println("Error parsing the batch file");
					System.out.println(e.getMessage());
					System.exit(3);
				}
				for (int i = 0; i < o.length; ++i) {
					Batch b = (Batch) o[i];
					DataModel dm = new DataModel("features.xml",null);
					try {
						dm.featureKey = new FileOutputStream(new File(b.getDestinationFK()));
						dm.featureValue = new FileOutputStream(new File(b.getDestinationFV()));
						b.setDataModel(dm);
						CommandLineThread clt = new CommandLineThread(b);
						clt.start();
						while(clt.isAlive()){
							if(System.in.available()>0){
								clt.cancel();		
							}
							clt.join(1000);
						}
					} catch (Exception e) {
						System.out
								.println("Error in execution - skipping this batch ("
										+ b.getName() + ")");
						e.printStackTrace();
					}
				}
			}
		} else if (args[0].equals("-s")) {

			// Validate command line parameters with simple sanity checks

			if (args.length < 4) {
				System.out.println(usage);
				System.exit(1);
			}
			File test = new File(args[1]);
			if (!test.exists()) {
				System.out.println("Settings file '" + args[1]
						+ "' does not exist");
				System.exit(2);
			}
			for (int i = 3; i < args.length; ++i) {
				boolean good = true;
				File tmp = new File(args[i]);
				if (!tmp.exists()) {
					System.out.println("ERROR: file " + args[i]
							+ " does not exist.");
					good = false;
				}
				if (!good) {
					System.exit(3);
				}
			}
			try {
				executeSettings(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(usage);
			System.exit(1);
		}
	}

	public static void executeSettings(String[] args) throws Exception{
		Object[] data = null;
		try {
			data = (Object[]) XMLDocumentParser.parseXMLDocument(args[1],
					"save_settings");
		} catch (Exception e) {
			System.out.println("Error encountered parsing the settings file");
			System.out.println(e.getMessage());
			System.exit(3);
		}
		int windowLength = 512;
		double offset = 0.0;
		double samplingRate;
		boolean saveWindows;
		boolean saveOverall;
		boolean normalise;
		int outputType;
		try {
			windowLength = Integer.parseInt((String) data[0]);
		} catch (NumberFormatException e) {
			System.out.println("Error in settings file");
			System.out.println("Window length of settings must be an integer");
			System.exit(4);
		}
		try {
			offset = Double.parseDouble((String) data[1]);
		} catch (NumberFormatException e) {
			System.out.println("Error in settings file");
			System.out
					.println("Window offset of settings must be an double between 0 and 1");
			System.exit(4);
		}
		DataModel dm = new DataModel("features.xml",null);
		samplingRate = ((Double) data[2]).doubleValue();
		normalise = ((Boolean) data[3]).booleanValue();
		saveWindows = ((Boolean) data[4]).booleanValue();
		saveOverall = ((Boolean) data[5]).booleanValue();
		String outputName = ((String) data[6]);
		if (outputName.equals("ACE")) {
			outputType = 0;
		} else {
			outputType = 1;
		}

		OutputStream destinationFK = null;
		OutputStream destinationFV = null;
		if (outputType == 0) {
			destinationFK = new FileOutputStream(new File(args[2] + "FK.xml"));
			destinationFV = new FileOutputStream(new File(args[2] + "FV.xml"));
		} else {
			destinationFK = new FileOutputStream(new File("definitions.arff"));
			destinationFV = new FileOutputStream(new File(args[2] + ".arff"));
		}

		HashMap<String, Boolean> active = (HashMap<String, Boolean>) data[7];
		HashMap<String, String[]> attribute = (HashMap<String, String[]>) data[8];
		
//		for (int i = 0; i < dm.features.length; ++i) {
//			String name = dm.features[i].getFeatureDefinition().name;
//			if (attribute.containsKey(name)) {
//				dm.defaults[i] = active.get(name);
//				String[] att = attribute.get(name);
//				for (int j = 0; j < att.length; ++j) {
//					try {
//						dm.features[i].setElement(j, att[j]);
//					} catch (Exception e) {
//						System.out.println("Feature " + name
//								+ "failed apply its " + j + " attribute");
//						e.printStackTrace();
//					}
//				}
//			} else {
//				dm.defaults[i] = false;
//			}
//		}
		
		// now process the aggregators
		String[] aggNames = ((LinkedList<String>)data[9]).toArray(new String[]{});
		String[][] aggFeatures = ((LinkedList<String[]>)data[10]).toArray(new String[][]{});
		String[][] aggParameters = ((LinkedList<String[]>)data[11]).toArray(new String[][]{});
//		LinkedList<Aggregator> aggregator = new LinkedList<Aggregator>();
//		for(int i=0;i<aggNames.length;++i){
//			if(dm.aggregatorMap.containsKey(aggNames[i])){
//				Aggregator tmp = dm.aggregatorMap.get(aggNames[i]);
//				if(!tmp.getAggregatorDefinition().generic){
//					tmp.setParameters(aggFeatures[i],aggParameters[i]);
//				}
//				aggregator.add(tmp);
//			}
//		}
//		dm.aggregators = aggregator.toArray(new Aggregator[]{});
		
		// now process the files
		RecordingInfo[] recording_info = new RecordingInfo[args.length - 3];
		File[] names = new File[args.length - 3];
		for (int i = 0; i < names.length; ++i) {
			names[i] = new File(args[i + 3]);
		}
		// Go through the files one by one
		for (int i = 0; i < names.length; i++) {
			// Assume file is invalid as first guess
			recording_info[i] = new RecordingInfo(names[i].getName(), names[i]
					.getPath(), null, false);
		}// for i in names

		try {
			dm.featureKey = destinationFK;
			dm.featureValue = destinationFV;
			Batch b = new Batch();
			b.setDataModel(dm);
			b.setWindowSize(windowLength);
			b.setWindowOverlap(offset);
			b.setSamplingRate(samplingRate);
			b.setNormalise(normalise);
			b.setPerWindow(saveWindows);
			b.setOverall(saveOverall);
			b.setRecording(recording_info);
			b.setOutputType(outputType);
			b.setFeatures(active,attribute);
			b.setAggregators(aggNames,aggFeatures,aggParameters);
			
			CommandLineThread clt = new CommandLineThread(b);
			clt.start();
			while(clt.isAlive()){
				if(System.in.available()>0){
					clt.cancel();		
				}
				clt.join(1000);
			}
		} catch (Exception e) {
			System.out.println("Error extracting features - aborting");
			System.out.println(e.getMessage());
			System.exit(5);
		}
	}
}
