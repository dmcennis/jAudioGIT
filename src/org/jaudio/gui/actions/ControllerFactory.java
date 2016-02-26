package org.jaudio.gui.actions;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by Daniel McEnnis on 2/25/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */
public class ControllerFactory {

    private HashMap<String,AbstractAction> actionMap = new HashMap<String,AbstractAction>();

    private static ControllerFactory ourInstance = new ControllerFactory();

    public static ControllerFactory getInstance() {
        return ourInstance;
    }

    private ControllerFactory() {
        actionMap.put("Copy",new CopyAction());
        actionMap.put("Cut",new CutAction());
        actionMap.put("EditRecording",new EditRecordingsAction(null));
        actionMap.put("ExecuteBatch",new ExecuteBatchAction());
        actionMap.put("Exit",new ExitAction());
        actionMap.put("GlobalWindowChange",new GlobalWindowChangeAction(null));
        actionMap.put("Load",new LoadAction());
        actionMap.put("LoadBatch",);
        actionMap.put("MultipleToggle",);
        actionMap.put("OutputType",);
        actionMap.put("Paste",);
        actionMap.put("PlayMIDI",);
        actionMap.put("PlayNow",);
        actionMap.put("PlaySamples",);
        actionMap.put("RecordFromMic",);
        actionMap.put("RemoveBatch",);
        actionMap.put("RemoveRecording",);
        actionMap.put("SamplingRate",);
        actionMap.put("Save",);
        actionMap.put("SaveBatch",);
        actionMap.put("StopPlayback",);
        actionMap.put("Synthesize",);
        actionMap.put("ViewBatch",);
        actionMap.put("ViewFileInfo",);

    }


    static public AbstractAction get(String action) {
        return ourInstance.getInstance().actionMap.get(action);
    }

    static public void register(String name, AbstractAction action){
        ourInstance.getInstance().actionMap.put(name,action);
    }
}
