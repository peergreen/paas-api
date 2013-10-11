package org.ow2.jonas.jpaas.core.server.resources;


import org.ow2.jonas.jpaas.core.server.resources.manager.application.ApplicationManagerResource;
import org.ow2.jonas.jpaas.core.server.resources.manager.environment.EnvironmentManagerResource;
import org.ow2.jonas.jpaas.core.server.resources.manager.taskpool.TaskPoolResource;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApiApplication extends Application {

    @PostConstruct
    protected void postConstruct() {
        System.out.println("postConstruct");
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();

        set.add(new ApplicationManagerResource());
        set.add(new EnvironmentManagerResource());
        set.add(new TaskPoolResource());

        return set;
    }
}

