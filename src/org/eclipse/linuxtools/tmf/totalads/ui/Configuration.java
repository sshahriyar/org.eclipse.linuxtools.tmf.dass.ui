package org.eclipse.linuxtools.tmf.totalads.ui;

public abstract class Configuration {
public static final String dbStates="states";
public static final String host="localhost";
public static final Integer port=27017;
//public static final String collectionNormal="normalTraces";
//public static final String collectionAnomalous="anomalousTraces";
//public static final String collectionAnomalyInfo="anomaliesHistory";
public static final Boolean notCalledAtRuntime=true;
public static DBMS connection;
public static String selectedDB="";
public static String traceCollection="trace_data";
public static String settingsCollection="settings";
}
