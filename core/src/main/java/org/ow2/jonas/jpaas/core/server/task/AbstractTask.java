package org.ow2.jonas.jpaas.core.server.task;


import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.xml.LinkXML;
import org.ow2.jonas.jpaas.api.xml.OwnerXML;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class AbstractTask {

    private String id;
    private Status status;
    private Date startTime;
    private Date endTime;
    private String name;
    public String baseUrl;

    public AbstractTask(String name, String baseUrl) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.startTime = new Date();
        this.status = Status.QUEUED;
        this.baseUrl = baseUrl;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract OwnerXML getOwner();


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
