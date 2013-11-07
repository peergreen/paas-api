package org.ow2.jonas.jpaas.core.server.task;

import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.task.Task;
import org.ow2.jonas.jpaas.api.xml.OwnerXML;
import org.ow2.jonas.jpaas.core.server.resources.manager.common.Util;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ScaleUpApplicationVersionInstanceTask extends AbstractTask implements Task {

    private Future<ApplicationVersionInstance> instance;


    public static final String TASK_NAME = "task:scale-up-application";

    public ScaleUpApplicationVersionInstanceTask(Future<ApplicationVersionInstance> instance, String baseUrl) {
        super(TASK_NAME, baseUrl);
        this.instance = instance;

        this.setStatus(Status.RUNNING);
        TaskManager.getSingleton().registerTask(this);

    }

    public Future<ApplicationVersionInstance> getApplicationVersionInstance() {
        return instance;
    }

    public void setApplicationVersionInstance(Future<ApplicationVersionInstance> instance) {
        this.instance = instance;
    }

    @Override
    public Future<?> getJob() {
        return getApplicationVersionInstance();
    }

    @Override
    public OwnerXML getOwner() {
        OwnerXML owner = new OwnerXML();

        Future<ApplicationVersionInstance> job = (Future<ApplicationVersionInstance>) this.getJob();
        if (job.isDone()) {
            ApplicationVersionInstance instance = null;
            try {
                instance = (ApplicationVersionInstance) job.get();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            owner.setName(instance.getInstanceName());
            owner.setHref(Util.getApplicationVersionInstanceHref(baseUrl, instance.getAppId(), instance.getVersionId(), instance.getInstanceId()));
            owner.setType(MediaType.APPLICATION_XML);
        } else {
            owner.setName("");
            owner.setHref("");
            owner.setType(MediaType.APPLICATION_XML);
        }

        return owner;
    }

}
