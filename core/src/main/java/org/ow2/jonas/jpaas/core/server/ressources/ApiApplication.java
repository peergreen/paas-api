package org.ow2.jonas.jpaas.core.server.ressources;


import org.ow2.jonas.jpaas.core.server.ressources.manager.application.ApplicationManagerRessource;
import org.ow2.jonas.jpaas.core.server.ressources.manager.environment.EnvironmentManagerRessource;
import org.ow2.jonas.jpaas.core.server.ressources.manager.taskpool.TaskPoolRessource;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApiApplication extends Application {

    public static EntityManager entityManager;

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

