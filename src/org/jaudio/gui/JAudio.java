/**
 * Created by Daniel McEnnis on 2/23/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.util.ResourceBundle;

/**
 * Default Description Google Interview Project
 */
public class JAudio {

    private JButton saveButton;
    private JButton runButton;
    private JButton addButton;
    private JMenuBar mainMenuConstruction;

    /**
     * Default constructor for JAudio
     */
    public JAudio() {

        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        runButton.setToolTipText(bundle.getString("perform.analysis.on.sources.or.files"));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        addButton.setToolTipText(bundle.getString("add.this.aalysis.run.to.a.batch.processing.file"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        saveButton.setToolTipText(bundle.getString("save.settings.and.batches.to.a.file"));
    }

    private void createUIComponents() {
        mainMenuConstruction = new JMenuBar();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations");
        JMenu fileMenu = new JMenu(resourceBundle.getString("file"));
        fileMenu.add(c.saveAction);
        fileMenu.add(c.saveBatchAction);
        fileMenu.add(c.loadAction);
        fileMenu.add(c.loadBatchAction);
        fileMenu.addSeparator();
        fileMenu.add(c.addBatchAction);
        fileMenu.add(c.executeBatchAction);
        controller.removeBatch = new JMenu(resourceBundle.getString("remove.batch"));
        controller.removeBatch.setEnabled(false);
        fileMenu.add(c.removeBatch);
        controller.viewBatch = new JMenu(resourceBundle.getString("view.batch"));
        controller.viewBatch.setEnabled(false);
        fileMenu.add(c.viewBatch);
        fileMenu.addSeparator();
        fileMenu.add(c.exitAction);
        JMenu editMenu = new JMenu(resourceBundle.getString("edit"));
        editMenu.add(c.cutAction);
        editMenu.add(c.copyAction);
        editMenu.add(c.pasteAction);
        JMenu recordingMenu = new JMenu(resourceBundle.getString("recording"));
        recordingMenu.add(c.addRecordingsAction);
        recordingMenu.add(c.editRecordingsAction);
        recordingMenu.add(c.removeRecordingsAction);
        recordingMenu.add(c.recordFromMicAction);
        recordingMenu.add(c.synthesizeAction);
        recordingMenu.add(c.viewFileInfoAction);
        recordingMenu.add(c.storeSamples);
        recordingMenu.add(c.validate);
        JMenu analysisMenu = new JMenu(resourceBundle.getString("analysis"));
        analysisMenu.add(c.globalWindowChangeAction);
        c.outputType = new JMenu(resourceBundle.getString("output.format"));
        c.outputType.add(ace);
        c.outputType.add(arff);
        analysisMenu.add(c.outputType);
        c.sampleRate = new JMenu(resourceBundle.getString("sample.rate.khz"));
        c.sampleRate.add(sample8);
        c.sampleRate.add(sample11);
        c.sampleRate.add(sample16);
        c.sampleRate.add(sample22);
        c.sampleRate.add(sample44);
        analysisMenu.add(c.sampleRate);
        analysisMenu.add(controller.normalise);
        JMenu playbackMenu = new JMenu(resourceBundle.getString("playback"));
        playbackMenu.add(c.playNowAction);
        playbackMenu.add(c.playSamplesAction);
        playbackMenu.add(c.stopPlayBackAction);
        playbackMenu.add(c.playMIDIAction);
        JMenu helpMenu = new JMenu(resourceBundle.getString("help"));
        helpMenu.add(helpTopics);
        helpMenu.add(c.aboutAction);

    }
}
