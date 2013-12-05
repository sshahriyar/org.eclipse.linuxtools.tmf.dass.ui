package org.eclipse.linuxtools.tmf.totalids.ui;
import java.util.List;
import java.util.Arrays;

public class KernelStateModeling implements IDetectionModels {
	
    List<Integer> MM_CALLS_LIST=  Arrays.asList(45,87,90,91,115,125,144,150,151,152,153,163,192,218,219,257,274,275,276,294,317);
    List<Integer> FS_CALLS_LIST =Arrays.asList(3,4,5,6,8,9,10,12,14,15,18,19,21,22,28,30,33,36,38,39,40,41,42,52,54,55,61,62,63,82,83,84,85,86,89,92,93,94,99,100,106,107,
            108,111,118,131,133,134,135,140,141,142,143,145,146,148,168,169,183,187,195,196,197,198,207,212,217,220,221,226,227,228,229,230,231,
            232,233,234,235,236,237,239,245,246,247,248,249,254,255,256,268,269,271,289,290,291,292,293,295,296,297,298,299,300,
            301,302,303,304,305,306,307,308,309,313,315,316,319,320,321,322,323,325,326,327,328,329,330,331,332,333,334);
    List<Integer> KERNEL_CALLS_LIST=Arrays.asList(0,1,7,13,16,20,23,24,25,26,27,29,34,37,43,46,47,48,49,50,51,57,59,60,64,65,66,68,69,
            70,71,73,74,75,76,77,78,79,80,81,88,95,96,97,103,104,105,109,114,116,121,122,124,126,128,129,132,136,138,139,147,149,154,155,
            156,157,158,159,160,161,162,164,165,170,171,172,174,175,176,177,178,179,182,184,185,191,199,200,201,202,203,204,205,206,208,209,
            210,211,213,214,215,216,224,238,240,241,242,252,258,259,260,261,262,263,264,265,266,267,270,283,284,310,311,312,318,335,336);
    List<Integer> ARCH_CALLS_LIST=Arrays.asList(2,11,67,72,119,120,173,186,190,243);
    List<Integer> IPC_CALLS_LIST=Arrays.asList(117,277,278,279,280,281,282);
    List<Integer> NET_CALLS_LIST=Arrays.asList(102,337);
    List<Integer> SECURITY_CALLS_LIST=Arrays.asList(286,287,288);
   
    private class TraceStates{
	    Double FS=0.0;
	    Double MM=0.0;
	    Double KL=0.0;
	    Double AC=0.0;
	    Double IPC=0.0;
	    Double NT=0.0;
	    Double SC=0.0;
	    Double UN=0.0;
    }
    DBMS connection;
    String database;
    
    public KernelStateModeling(DBMS connection){
    	this.connection=connection;
    	this.database=Configuration.dbStates;
    }
	
    @Override
	public void train(char[] trace, Boolean isLastTrace) throws Exception {
		TraceStates states= new TraceStates();
		stateMapping(trace, states);
		connection.insert("query", database);
	
		
	}

	@Override
	public void validate(char[] trace) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void test(char[] trace) throws Exception {
		// TODO Auto-generated method stub

	}
	
	private void stateMapping(char[] trace, TraceStates states){
		Double totalSysCalls=0.0;
		
		StringBuilder syscallID= new StringBuilder();
		
		for (int cnt=0;cnt<trace.length;cnt++){
			syscallID.append(trace[cnt]);
			if (trace[cnt]=='\n'){
				stateMatch(syscallID.toString());
				syscallID.delete(0, syscallID.length());
			}
		}
		
		totalSysCalls=states.MM+states.FS+states.KL+states.NT+states.IPC+states.SC+states.AC+states.UN;
		states.FS=states.FS/totalSysCalls;
		states.MM=states.MM/totalSysCalls;
		states.KL=states.KL/totalSysCalls;
		states.NT=states.NT/totalSysCalls;
		states.IPC=states.IPC/totalSysCalls;
		states.SC=states.SC/totalSysCalls;
		states.AC=states.AC/totalSysCalls;
		states.UN=states.UN/totalSysCalls;
	}
	
	private void stateMatch(String syscallID, TraceStates states){
		  if (MM_CALLS_LIST.contains(Integer.parseInt(syscallID)))
			  states.MM++;// keep track of the last sys_entry function id and
		  else if (FS_CALLS_LIST.contains(Integer.parseInt(syscallID)))
			  states.FS++;    
		  else if (KERNEL_CALLS_LIST.contains(Integer.parseInt(syscallID)))
			  states.KL++;	
		  else if (NET_CALLS_LIST.contains(Integer.parseInt(syscallID)))
			  states.NT++;
		  else if (IPC_CALLS_LIST.contains(Integer.parseInt(syscallID)))
			  states.IPC++;
		  else if (SECURITY_CALLS_LIST.contains(Integer.parseInt(syscallID)))
			  states.SC++;
		  else if (ARCH_CALLS_LIST.contains(Integer.parseInt(syscallID)))
			  states.AC++;
		  else 
			  states.UN++;
		      
	}

}