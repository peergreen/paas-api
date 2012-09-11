package org.ow2.jonas.jpaas.server.task;

public class CreateEnvironmentTask implements ITask{

	private String environmentTemplateDescriptor;
	
	public static final String TASK_NAME = "task:createEnvironment";
	
	public CreateEnvironmentTask(String environmentTemplateDescriptor) {
		this.environmentTemplateDescriptor=environmentTemplateDescriptor;
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

}
