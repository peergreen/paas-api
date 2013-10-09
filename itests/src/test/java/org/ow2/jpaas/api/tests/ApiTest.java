package org.ow2.jpaas.api.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.peergreen.deployment.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ApiTest {

    public static final String PROJECT_VERSION = "1.0.0-M2-SNAPSHOT";

    @Inject
    BundleContext context;

    @Inject
    private DeploymentService deploymentService;

    @Inject
    private ArtifactBuilder artifactBuilder;

    private URI mvnURIWar;

    private Artifact artifact;

    @Configuration
    public Option[] config() {

        // Reduce log level.
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        return options(systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                junitBundles());
    }

    @Before
    public void deployWar() throws Exception {
        // M2 URI of ow2 util file
        this.mvnURIWar = new URI("mvn:org.ow2.jonas.jpaas.jpaas-api/jpaas-api-core/" + PROJECT_VERSION + "/war");
        this.artifact = artifactBuilder.build("myappli.war", mvnURIWar);
        ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(artifact);
        artifactProcessRequest.setDeploymentMode(DeploymentMode.DEPLOY);
        deploymentService.process(Collections.singleton(artifactProcessRequest));

    }

    @Test
    public void checkInjectContext() {
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        assertNotNull(context);
    }



   /* @Test
    public void checkInjectNamingManager() {
        assertNotNull(namingManager);
    }

    @Test
    public void checkBundleNamingManager() {
        Boolean found = false;
        Boolean active = false;
        Bundle[] bundles = context.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle != null) {
                if (bundle.getSymbolicName().equals("org.ow2.jonas.jpaas.naming-manager.ejb")) {
                    found = true;
                    if (bundle.getState() == Bundle.ACTIVE) {
                        active = true;
                    }
                }
            }
        }
        assertTrue(found);
        assertTrue(active);
    }*/



}
