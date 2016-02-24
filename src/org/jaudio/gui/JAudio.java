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
}
