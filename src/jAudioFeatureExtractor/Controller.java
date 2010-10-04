package jAudioFeatureExtractor;

import java.util.Vector;

import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.actions.*;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * Controller is a master location for all actions and non-gui components. Not
 * the best design, but it works.
 * 
 * @author Daniel McEnnis
 */
public class Controller implements ModelListener {

	/**
	 * model controlling display of recordings
	 */
	public FeatureSelectorTableModel fstm_;

	/**
	 * model controlling display of features
	 */
	public RecordingsTableModel rtm_;

	/**
	 * model containing all executable code not directly linked to a gui.
	 */
	public DataModel dm_;
	
	/**
	 * model containing a list of all avialable aggregators
	 */
	public AggListTableModel aggList_;
	
	/**
	 * model containing list of all configured aggregators
	 */
	public ActiveAggTableModel activeAgg_;

	/**
	 * @see jAudioFeatureExtractor.actions.ExitAction
	 */
	public ExitAction exitAction = new ExitAction();

	/**
	 * @see jAudioFeatureExtractor.actions.SaveAction
	 */
	public SaveAction saveAction;

	/**
	 * @see jAudioFeatureExtractor.actions.LoadAction
	 */
	public LoadAction loadAction;

	/**
	 * Not really applicable but included anyways for completeness
	 */
	public CutAction cutAction = new CutAction();;

	/**
	 * Not really applicable but included anyways for completeness
	 */
	public CopyAction copyAction = new CopyAction();;

	/**
	 * Not really applicable but included anyways for completeness
	 */
	public PasteAction pasteAction = new PasteAction();

	/**
	 * @see jAudioFeatureExtractor.actions.RecordFromMicAction
	 */
	public RecordFromMicAction recordFromMicAction = new RecordFromMicAction(
			this);

	/**
	 * @see jAudioFeatureExtractor.actions.SynthesizeAction
	 */
	public SynthesizeAction synthesizeAction;

	/**
	 * @see jAudioFeatureExtractor.actions.ViewFileInfoAction
	 */
	public ViewFileInfoAction viewFileInfoAction;

	/**
	 * @see jAudioFeatureExtractor.actions.AddRecordingAction
	 */
	public AddRecordingAction addRecordingsAction = new AddRecordingAction();

	/**
	 * @see jAudioFeatureExtractor.actions.RemoveRecordingAction
	 */
	public RemoveRecordingAction removeRecordingsAction = new RemoveRecordingAction();

	/**
	 * @see jAudioFeatureExtractor.actions.GlobalWindowChangeAction
	 */
	public GlobalWindowChangeAction globalWindowChangeAction;

	/**
	 * @see jAudioFeatureExtractor.actions.OutputTypeAction
	 */
	public OutputTypeAction outputTypeAction = new OutputTypeAction();

	/**
	 * @see jAudioFeatureExtractor.actions.PlayNowAction
	 */
	public PlayNowAction playNowAction;

	/**
	 * @see jAudioFeatureExtractor.actions.PlaySamplesAction
	 */
	public PlaySamplesAction playSamplesAction;

	/**
	 * @see jAudioFeatureExtractor.actions.StopPlayBackAction
	 */
	public StopPlayBackAction stopPlayBackAction;

	/**
	 * @see jAudioFeatureExtractor.actions.PlayMIDIAction
	 */
	public PlayMIDIAction playMIDIAction = new PlayMIDIAction();

	/**
	 * @see jAudioFeatureExtractor.actions.SamplingRateAction
	 */
	public SamplingRateAction samplingRateAction = new SamplingRateAction();

	/**
	 * @see jAudioFeatureExtractor.actions.EditRecordingsAction
	 */
	public EditRecordingsAction editRecordingsAction;

	/**
	 * @see jAudioFeatureExtractor.actions.AboutAction
	 */
	public AboutAction aboutAction = new AboutAction();

	/**
	 * @see jAudioFeatureExtractor.actions.SaveBatchAction
	 */
	public SaveBatchAction saveBatchAction;

	/**
	 * @see jAudioFeatureExtractor.actions.LoadBatchAction
	 */
	public LoadBatchAction loadBatchAction;

	/**
	 * @see jAudioFeatureExtractor.actions.ExecuteBatchAction
	 */
	public ExecuteBatchAction executeBatchAction;

	/**
	 * @see jAudioFeatureExtractor.actions.AddBatchAction
	 */
	public AddBatchAction addBatchAction;

	/**
	 * @see jAudioFeatureExtractor.actions.RemoveBatchAction
	 */
	public RemoveBatchAction removeBatchAction;

	/**
	 * @see jAudioFeatureExtractor.actions.ViewBatchAction
	 */
	public ViewBatchAction viewBatchAction;

	/**
	 * The removeBatch menu item needs to be seen outside the main frame to
	 * allow dynamically adding and removing batches from its menu.
	 */
	public JMenu removeBatch;

	/**
	 * The viewBatch menu item needs to be seen outside the main frame to allow
	 * dynamically adding and removing batches from its menu.
	 */
	public JMenu viewBatch;

	/**
	 * SampleRate needs to be seen outside the normal structure to provide
	 * access to its radio button children for saving settings and similar
	 * actions
	 */
	public JMenu sampleRate;

	/**
	 * outputType needs to be seen outside the normal structure to provide
	 * access to its radio button children for saving settings and similar
	 * actions
	 */
	public JMenu outputType;

	/**
	 * vector holding a list of actions for removing batches.
	 */
	public Vector<AbstractAction> removeBatchVector = new Vector<AbstractAction>();

	/**
	 * storeSamples needs to be available for saving settings and similar
	 * actions
	 */
	public JCheckBoxMenuItem storeSamples = new JCheckBoxMenuItem(
			"Store Samples", false);

	/**
	 * validate needs to be available for saving settings and similar actions
	 */
	public JCheckBoxMenuItem validate = new JCheckBoxMenuItem(
			"Validate Recordings", true);

	/**
	 * normalise needs to be available for saving settings and similar actions
	 */
	public JCheckBoxMenuItem normalise = new JCheckBoxMenuItem(
			"Normalise Recordings", false);

	/**
	 * @see jAudioFeatureExtractor.ExtractionThread
	 */
	public ExtractionThread extractionThread;

	/**
	 * @see jAudioFeatureExtractor.BatchExecutionThread
	 */
	public BatchExecutionThread batchExecutionThread;

	/**
	 * holds all currently defined batches.
	 */
	public Vector<Batch> batches;

	/**
	 * Initial creation and configuration of most controller and model data.
	 * Unfortunately, many of the controller componenets are tied to GUI
	 * components, requiring further inititalization in those GUI components
	 */
	public Controller() {
		dm_ = new DataModel("features.xml",this);
		fstm_ = new FeatureSelectorTableModel(new Object[] {
				new String("Save"), new String("Feature"),
				new String("Dimensions"), new String("IsPrimary") },
				dm_.features.length);
		rtm_ = new RecordingsTableModel(new Object[] { new String("Name"),
				new String("Path") }, 0);
		aggList_ = new AggListTableModel();
		activeAgg_ = new ActiveAggTableModel();
		saveAction = new SaveAction(this, fstm_);
		loadAction = new LoadAction(this, fstm_);
		globalWindowChangeAction = new GlobalWindowChangeAction(dm_);
		addRecordingsAction.setModel(this);
		synthesizeAction = new SynthesizeAction(this);
		playSamplesAction = new PlaySamplesAction(this);
		stopPlayBackAction = new StopPlayBackAction(this);
		playNowAction = new PlayNowAction(this);
		editRecordingsAction = new EditRecordingsAction(this);
		viewFileInfoAction = new ViewFileInfoAction(this);
		batches = new Vector<Batch>();
		addBatchAction = new AddBatchAction(this);
		saveBatchAction = new SaveBatchAction(this);
		loadBatchAction = new LoadBatchAction(this);
		removeBatchAction = new RemoveBatchAction(this);
		viewBatchAction = new ViewBatchAction(this);
	}

	/**
	 * function for permitting features to request this panel that it updates
	 * its table to reflect changes in its number of dimensions. As of 05-08-05
	 * only LPC uses this feature to change its own number of dimensions. (This
	 * is critical for LPC because so much of the choice of dimension is a black
	 * art.)
	 */
	public synchronized void updateTable() {
		FeatureDefinition[] feature_definitions = new FeatureDefinition[dm_.features.length];
		boolean[] features_selected = new boolean[dm_.features.length];
		boolean[] is_primary = new boolean[dm_.features.length];
		for (int i = 0; i < feature_definitions.length; i++) {
			feature_definitions[i] = dm_.features[i].getFeatureDefinition();
			features_selected[i] = ((Boolean) fstm_.getValueAt(i, 0))
					.booleanValue();
			is_primary[i] = ((Boolean) fstm_.getValueAt(i, 3)).booleanValue();
		}
		fstm_.clearTable();
		fstm_.fillTable(feature_definitions, features_selected, is_primary);
		fstm_.fireTableDataChanged();
	}

}
