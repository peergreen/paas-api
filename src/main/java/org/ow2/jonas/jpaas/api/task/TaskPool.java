/**
 * 
 */
package org.ow2.jonas.jpaas.api.task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sellami
 * 
 */

public enum TaskPool {
	INSTANCE;

	private List<IWorkTask> taskList = new ArrayList<IWorkTask>();

	public List<IWorkTask> getWorkTasks() {
		return taskList;
	}

	public void add(IWorkTask task) {
		taskList.add(task);
	}

	public IWorkTask getTask(String taskId) {
		if (taskList == null || taskList.size() == 0) {
			System.out.println("[TaskPool]: the task list is empty!");
			return null;
		} else {
			for (IWorkTask task : taskList) {
				if (task.getId() == Long.parseLong(taskId))
					return task;
			}
		}
		// The specified Task was not found
		return null;
	}

}
