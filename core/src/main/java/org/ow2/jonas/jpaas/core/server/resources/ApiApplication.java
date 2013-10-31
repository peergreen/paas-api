package org.ow2.jonas.jpaas.core.server.resources;


import org.ow2.jonas.jpaas.core.server.resources.manager.application.ApplicationManagerResource;
import org.ow2.jonas.jpaas.core.server.resources.manager.application.DeployableManagerResource;
import org.ow2.jonas.jpaas.core.server.resources.manager.environment.EnvironmentManagerResource;
import org.ow2.jonas.jpaas.core.server.resources.manager.task.TaskResource;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApiApplication extends Application {

    private Log logger = LogFactory.getLog(ApiApplication.class);

    @PostConstruct
    protected void postConstruct() {
        logger.debug("postConstruct");
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();

        set.add(new ApplicationManagerResource());
        set.add(new EnvironmentManagerResource());
        set.add(new TaskResource());
        set.add(new DeployableManagerResource());

        return set;
    }
}

