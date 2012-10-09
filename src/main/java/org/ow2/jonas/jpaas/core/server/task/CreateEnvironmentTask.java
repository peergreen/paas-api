package org.ow2.jonas.jpaas.core.server.task;

import java.util.concurrent.Future;

import org.ow2.jonas.jpaas.api.task.ITask;
import org.ow2.jonas.jpaas.api.task.TaskException;
import org.ow2.jonas.jpaas.manager.api.Environment;

public class CreateEnvironmentTask implements ITask {

	private String environmentTemplateDescriptor;

	private Future<Environment> environment;

	public static final String TASK_NAME = "task:createEnvironment";

	public CreateEnvironmentTask(String environmentTemplateDescriptor,
			Future<Environment> environment) {
		this.environmentTemplateDescriptor = environmentTemplateDescriptor;
		this.environment = environment;
	}

	@Override
	public void execute() throws TaskException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void preExecution() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postExecution() {
		// TODO Auto-generated method stub
	}

	public String getEnvironmentTemplateDescriptor() {
		return environmentTemplateDescriptor;
	}

	public void setEnvironmentTemplateDescriptor(
			String environmentTemplateDescriptor) {
		this.environmentTemplateDescriptor = environmentTemplateDescriptor;
	}

	public Future<Environment> getEnvironment() {
		return environment;
	}

	public void setEnvironment(Future<Environment> environment) {
		this.environment = environment;
	}

}
