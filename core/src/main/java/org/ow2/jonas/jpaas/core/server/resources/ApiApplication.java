package org.ow2.jonas.jpaas.core.server.resources;


import org.ow2.jonas.jpaas.core.server.resources.manager.application.ApplicationManagerRessource;
import org.ow2.jonas.jpaas.core.server.resources.manager.environment.EnvironmentManagerRessource;
import org.ow2.jonas.jpaas.core.server.resources.manager.taskpool.TaskPoolRessource;

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

        set.add(new ApplicationManagerRessource());
        set.add(new EnvironmentManagerRessource());
        set.add(new TaskPoolRessource());

        return set;
    }
}

