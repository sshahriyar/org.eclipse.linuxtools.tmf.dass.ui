package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;

import java.util.HashMap;

/**
 * This class maps the name to integr ids
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
public class NameToIDMapper {
	
private HashMap <String, Integer>nameToID;
/**
 * Constructor
 */
public NameToIDMapper(){
	nameToID=new HashMap<String, Integer>();
}
/**
 * Returns the id mapped to a name
 * @param name Event name
 * @return Integer id
 */
public Integer getId(String name){
	Integer id=nameToID.get(name);
	if (id==null){
		Integer size=nameToID.size();
		updateId(name, size);
		return size;
	}else
	  return id;
}
/**
 * Sets the id to a name
 * @param name Event name
 * @param value Id value
 */
private void updateId(String name, Integer value){
	nameToID.put(name, value);
	System.out.println(name + " "+ value);
}
/**
 * Size
 * @return
 */
public Integer getSize(){
	return nameToID.size();
	
}

}
