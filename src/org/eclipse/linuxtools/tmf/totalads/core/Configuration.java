/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.core;

import java.io.File;

import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
/**
 * This class contains global variables used throughout the code
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
public abstract class Configuration {
public static final String dbStates="states";
public static final String host="localhost";
public static final Integer port=27017;
//public static final String collectionNormal="normalTraces";
//public static final String collectionAnomalous="anomalousTraces";
//public static final String collectionAnomalyInfo="anomaliesHistory";
//public static final Boolean notCalledAtRuntime=true;
public static DBMS connection;
public static String selectedDB="";
//public static String traceCollection="trace_data";
//public static String settingsCollection="settings";
/**
 * Returns the current directory of the application
 * @return
 * @throws Exception
 */
public static String getCurrentPath() {
	String applicationDir=Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	//File file= new File (Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath()); 
	//String applicationDir =file.getParent();
	return applicationDir+File.separator;
}
}
