/**
 * 
 */
package jAudioFeatureExtractor;

import jAudioFeatureExtractor.ACE.DataTypes.Batch;

import java.util.ResourceBundle;

/**
 * @author mcennis
 *
 */
public class CommandLineThread extends Thread implements Updater {

	public Cancel cancel;
	
	private Batch batch;
	
	public CommandLineThread(Batch b){
		batch=b;
		cancel = batch.getDataModel().cancel_;
		batch.getDataModel().setUpdater(this);
	}
	
	
	/* (non-Javadoc)
	 * @see jAudioFeatureExtractor.Updater#announceUpdate(int, int)
	 */
	public void announceUpdate(int fileNumber, int fileDone) {
		System.out.println();

	}

	/* (non-Javadoc)
	 * @see jAudioFeatureExtractor.Updater#announceUpdate(int)
	 */
	public void announceUpdate(int fileDone) {
		System.out.print(".");

	}

	/* (non-Javadoc)
	 * @see jAudioFeatureExtractor.Updater#setNumberOfFiles(int)
	 */
	public void setNumberOfFiles(int files) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jAudioFeatureExtractor.Updater#setFileLength(int)
	 */
	public void setFileLength(int windows) {
		// TODO Auto-generated method stub

	}
	
	public void cancel(){
		cancel.setCancel(true);
	}

	@Override
	public void run() {
		try {
			batch.execute();
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");

			System.out.println(bundle.getString("execution.completed.sucessfully"));
		} catch (ExplicitCancel e){
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


}
