
package org.ow2.jonas.jpaas.core.server.task;

import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.task.Task;
import org.ow2.jonas.jpaas.api.xml.LinkXML;
import org.ow2.jonas.jpaas.api.xml.TaskXML;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;


import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class TaskManager {

    private Map<String, Task> tasks;

    private Log logger = LogFactory.getLog(TaskManager.class);

    private static TaskManager singleton;

    public static final long THRESHOLD_TO_CLEAN = 86400;

    public static final String CANCEL_TASK_NAME = "task:cancel";

    public static final long PERIOD_REFRESH = 10000;
    public static final long PERIOD_CLEAN = 60000;
    public static final long PERIOD_SLEEP = 5000;

    private static TaskThreadManager taskThreadManager;

    public TaskManager() {
        logger.info("Init");
        tasks = new ConcurrentHashMap<String, Task>();
        taskThreadManager = new TaskThreadManager(this);
        taskThreadManager.start();
    }

    public static TaskManager getSingleton() {
        if (singleton == null) {
            singleton = new TaskManager();
        }
        return singleton;
    }

    public Task getTask(String id) {
        Task task = tasks.get(id);
        refreshTask(task);
        return task;
    }

    public List<Task> getTasks() {
        refresh();
        return new ArrayList<Task>(tasks.values());
    }

    public void registerTask(Task task) {
        logger.info("register new task:" + task.getId());
        tasks.put(task.getId(), task);
    }

    public void unregisterTask(Task task) {
        logger.info("unregister new task:" + task.getId());
        tasks.remove(task.getId());
    }

    private void clean() {
        logger.info("Clean tasks list");

        Date currentDate = new Date();

        for (Task task : tasks.values()) {
            if (task.getJob().isDone()) {
                logger.debug("Task <" + task.getId() + "> is done");
                if (task.getEndTime().getTime() - currentDate.getTime() > THRESHOLD_TO_CLEAN){
                    logger.debug("Remove task <" + task.getId() + ">");
                    tasks.remove(task.getId());
                }
            }
        }
    }

    private void refreshTask(Task task) {
        logger.info("Refresh task: " + task.getId());
         if (task.getStatus().equals(Status.RUNNING)) {

                if (task.getJob().isDone()) {
                    logger.info("Task is done: " + task.getId());

                    task.setStatus(Status.SUCCESS);
                    task.setEndTime(new Date());
                }
                if (task.getJob().isCancelled()) {
                    logger.info("Task is canceled: " + task.getId());

                    task.setStatus(Status.CANCELED);
                    task.setEndTime(new Date());
                }
            }

    }


    private void refresh() {
        logger.info("Refresh tasks list");
        for (Task task : tasks.values()) {
            refreshTask(task);
        }
    }

    public TaskXML buildTaskXml(Task task) {

        TaskXML xmlTask = new TaskXML();
        xmlTask.setId(task.getId());
        xmlTask.setOperationName(task.getName());

        if (task != null) {
            Status status = task.getStatus();
            if (status != null) {
                xmlTask.setStatus(String.valueOf(status));
            }
            Date startTime = task.getStartTime();
            if (startTime != null) {
                xmlTask.setStartTime(startTime);
            }
            Date endTime = task.getEndTime();
            if (endTime != null) {
                xmlTask.setEndTime(endTime);
            }
            List<LinkXML> links = new ArrayList<LinkXML>();
            links.add(getCancelLink(task));
            xmlTask.setLink(links);

            xmlTask.setOwner(task.getOwner());

        }
        return xmlTask;
    }


    private LinkXML getCancelLink(final Task task) {
        LinkXML link = new LinkXML();
        link.setHref(getCancelTaskUrl(task));
        link.setRel(CANCEL_TASK_NAME);
        link.setType(MediaType.APPLICATION_XML);
        return link;
    }

    private String getCancelTaskUrl(final Task task) {
        return task.getBaseUrl() + "/task/" + task.getId() + "/action/cancel";
    }

    class TaskThreadManager extends Thread {
        private TaskManager taskManager;

        public TaskThreadManager(TaskManager taskManager) {
            super("TaskManager");
            this.taskManager = taskManager;
        }

        public void run() {

            taskManager.logger.info("Start thread");

            Date lastClean = new Date();
            Date lastRefresh = new Date();

            while (true) {

                Date currentDate = new Date();

                if (currentDate.getTime() - lastClean.getTime() > TaskManager.PERIOD_CLEAN) {
                    this.taskManager.clean();
                    lastClean = currentDate;
                }
                if (currentDate.getTime() - lastRefresh.getTime() > TaskManager.PERIOD_REFRESH) {
                    this.taskManager.refresh();
                    lastRefresh = currentDate;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

        }
    }

}


