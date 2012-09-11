/**
 * 
 */
package org.ow2.jonas.jpaas.server.task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sellami
 *
 */
public final class TaskPool {
	private static volatile TaskPool instance = null;
	
	private List<IWorkTask> taskList;
	
	public TaskPool() {
		taskList= new ArrayList<IWorkTask>();
    }
	
	public final static TaskPool getInstance() {
        if (instance == null) {
        	instance= new TaskPool();
        } 
        return instance;
    }
	
	public List<IWorkTask> getWorkTasks(){
		return taskList;
	}
	
	public void add(IWorkTask task){
		taskList.add(task);
	}

}
