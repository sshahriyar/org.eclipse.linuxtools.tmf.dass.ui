/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel.HiddenMarkovModel;
import org.eclipse.linuxtools.tmf.totalads.algorithms.ksm.KernelStateModeling;
import org.eclipse.linuxtools.tmf.totalads.algorithms.slidingwindow.SlidingWindow;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;

/**
 * This is an AlgorithmFactory class that registers all the Algorithms with
 * itself. The class follows a factory design pattern, and it creates a
 * singleton object.
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class AlgorithmFactory {

    // Variables used in the class that keep track of algorithms in the class
    private static AlgorithmFactory fAlgorithmTypes = null;

    private HashMap<AlgorithmTypes, HashSet<String>> fAlgList = null;
    private HashMap<String, IDetectionAlgorithm> fAcronymModels = null;

    /**
     * Constructor
     */
    private AlgorithmFactory() {
        fAlgList = new HashMap<>();
        fAcronymModels = new HashMap<>();
    }

    /**
     * Creates an instance of AlgorithmFactory
     *
     * @return Instance of AlgorithmFactory
     */
    public static AlgorithmFactory getInstance() {
        if (fAlgorithmTypes == null) {
            fAlgorithmTypes = new AlgorithmFactory();
        }
        return fAlgorithmTypes;
    }

    /**
     * Destroys the instance of factory if already exists' This code is
     * necessary because when Eclipse is running and TotalADS window is closed
     * then reopened again, the static object is not recreated on the creation
     * of new object of TotalADS. We need to destroy all the objects.
     */
    public static void destroyInstance() {
        if (fAlgorithmTypes != null) {
            fAlgorithmTypes = null;
        }
    }

    /**
     * Gets the list of algorithms by a type; e.g., classification, clustering,
     * etc.
     *
     * @param fAlgorithmTypes
     *            An enum of AlgorithmTypes
     * @return Array of Algorithms
     */
    public IDetectionAlgorithm[] getAlgorithms(AlgorithmTypes algorithmTypes) {
        HashSet<String> list = fAlgList.get(algorithmTypes);
        if (list == null) {
            return null;
        }

        IDetectionAlgorithm[] models = new IDetectionAlgorithm[list.size()];
        Iterator<String> it = list.iterator();
        int count = 0;
        while (it.hasNext()) {
            models[count++] = getAlgorithmByAcronym(it.next());
        }
        return models;
    }

    /**
     * This function registers an algorithm by using its acronym as a name
     *
     * @param name
     *            Acronym of the algorithm
     * @param detectionAlgorithm
     *            The Algorithm to register
     * @throws TotalADSGeneralException
     *             Validation exception
     */
    private void registerAlgorithmWithAcronym(String key, IDetectionAlgorithm detectionAlgorithm) throws TotalADSGeneralException {

        if (key.isEmpty()) {
            throw new TotalADSGeneralException("Empty name/acronym!");
        } else if (key.contains("_")) {
            throw new TotalADSGeneralException("Acronym cannot contain underscore");
        } else {

            IDetectionAlgorithm model = fAcronymModels.get(key);
            if (model == null) {
                fAcronymModels.put(key, detectionAlgorithm);
            } else {
                throw new TotalADSGeneralException("Duplicate name/acronym!");
            }
        }

    }

    /**
     * Registers an algorithm with this factory
     *
     * @param detectionAlgorithm
     *            The Algorithm to register
     * @param algorithmType
     *            An Enum of {@link AlgorithmTypes}
     * @throws TotalADSGeneralException
     *             An exception for incorrect parameters
     */
    public void registerModelWithFactory(AlgorithmTypes algorithmType, IDetectionAlgorithm detectionAlgorithm)
            throws TotalADSGeneralException {

        registerAlgorithmWithAcronym(detectionAlgorithm.getAcronym(), detectionAlgorithm);
        HashSet<String> list = fAlgList.get(algorithmType);

        if (list == null) {
            list = new HashSet<>();
        }

        list.add(detectionAlgorithm.getAcronym());

        fAlgList.put(algorithmType, list);

    }

    /**
     * Get an algorithm by acronym
     *
     * @param name
     * @return an instance of the algorithm
     */
    public IDetectionAlgorithm getAlgorithmByAcronym(String key) {
        IDetectionAlgorithm model = fAcronymModels.get(key);
        if (model == null) {
            return null;
        }
        return model.createInstance();
    }

    /**
     * Gets all algorithms to register with the factory. Currently all the
     * algorithms are manually initialized in this function but in future
     * versions, this code would be replace with reflection and will register
     * all algorithms derived from the interface IDetectionAlgorithms
     * automatically
     *
     * @throws TotalADSGeneralException
     *             An exception for invalid registration
     */
    public void initialize() throws TotalADSGeneralException {

        // Reflections reflections = new
        // Reflections("org.eclipse.linuxtools.tmf.totalads.ui");
        // //java.util.Set<Class<? extends IDetectionAlgorithm>> modules =
        // reflections.getSubTypesOf
        // (org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels.class);

        KernelStateModeling.registerModel();
        SlidingWindow.registerModel();
        HiddenMarkovModel.registerModel();

    }

}
