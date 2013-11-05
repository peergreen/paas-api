package org.ow2.jonas.jpaas.core.server.task;

import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.task.Task;
import org.ow2.jonas.jpaas.api.xml.OwnerXML;
import org.ow2.jonas.jpaas.core.server.resources.manager.common.Util;
import org.ow2.jonas.jpaas.manager.api.Environment;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StartEnvironmentTask extends AbstractTask implements Task {

    private Future<Environment> environment;


    public static final String TASK_NAME = "task:start-environment";

    public StartEnvironmentTask(Future<Environment> environment, String baseUrl) {
        super(TASK_NAME, baseUrl);
        this.environment = environment;

        this.setStatus(Status.RUNNING);
        TaskManager.getSingleton().registerTask(this);

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
            owner.setHref(Util.getEnvironmentHref(baseUrl, env.getEnvId()));
            owner.setType(MediaType.APPLICATION_XML);
        } else {
            owner.setName("");
            owner.setHref("");
            owner.setType(MediaType.APPLICATION_XML);
        }

        return owner;
    }

}
