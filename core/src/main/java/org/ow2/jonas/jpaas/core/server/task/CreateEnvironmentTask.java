package org.ow2.jonas.jpaas.core.server.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.task.Task;
import org.ow2.jonas.jpaas.api.xml.OwnerXML;
import org.ow2.jonas.jpaas.manager.api.Environment;

import javax.ws.rs.core.MediaType;

public class CreateEnvironmentTask extends AbstractTask implements Task {

    private String environmentTemplateDescriptor;

    private Future<Environment> environment;


    public static final String TASK_NAME = "task:create-environment";

    public CreateEnvironmentTask(String environmentTemplateDescriptor,
                                 Future<Environment> environment, String baseUrl) {
        super(TASK_NAME, baseUrl);
        this.environmentTemplateDescriptor = environmentTemplateDescriptor;
        this.environment = environment;

        this.setStatus(Status.RUNNING);
        TaskManager.getSingleton().registerTask(this);

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

    @Override
    public Future<?> getJob() {
        return getEnvironment();
    }

    @Override
    public OwnerXML getOwner() {
        OwnerXML owner = new OwnerXML();

        Future<Environment> job = (Future<Environment>) this.getJob();
        if (job.isDone()) {
            Environment env = null;
            try {
                env = (Environment) job.get();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            owner.setName(env.getEnvName());
            owner.setHref(getUrl(env.getEnvId()));
            owner.setType(MediaType.APPLICATION_XML);
        } else {
            owner.setName("");
            owner.setHref("");
            owner.setType(MediaType.APPLICATION_XML);
        }

        return owner;
    }

    private String getUrl(String id) {
        return this.baseUrl + "/environment/" + id;
    }
}
