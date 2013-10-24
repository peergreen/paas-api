package org.ow2.jpaas.api.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.peergreen.deployment.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.ow2.jonas.jpaas.api.xml.ApplicationXML;
import org.ow2.jonas.jpaas.api.xml.EnvironmentXML;
import org.ow2.jonas.jpaas.api.xml.TaskXML;
import org.ow2.jonas.jpaas.util.clouddescriptors.cloudapplication.CloudApplicationDesc;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ApiTest {

    public static final String PROJECT_VERSION = "1.0.0-M1-SNAPSHOT";
    public static final String BASE_URL_API = "http://localhost:9000/com.peergreen.paas-paas-api-" + PROJECT_VERSION + "/api/";


    @Inject
    BundleContext context;

    @Inject
    private DeploymentService deploymentService;

    @Inject
    private ArtifactBuilder artifactBuilder;

    private static URI mvnURIWar;

    private static Artifact artifact;

    private Client client;

    private static boolean warDeployed = false;

    @Configuration
    public Option[] config() {

        // Reduce log level.
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        return options(systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                mavenBundle("org.ow2.util.bundles", "jaxb2-ri-2.2.5").version("1.0.0"),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-common").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-environment-template-topology-datasource").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-environment-template-topology-connector").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-environment-template-node-template-jonas").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-environment-template-node-template-jk").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-environment-template-node-template-external-db").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-environment-template-core").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-cloud-application-deployable-artefact").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-cloud-application-deployable-xml").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-cloud-application-core").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-util-cloud-descriptors-deployment-core").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-application-manager-api").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-application-manager-mock").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-environment-manager-api").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-environment-manager-mock").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "paas-manager-api").version(PROJECT_VERSION),
                mavenBundle("com.peergreen.paas", "api-xml").version(PROJECT_VERSION),
                junitBundles());
    }

    @Before
    public void deployWar() throws Exception {

        if (!warDeployed) {
            // M2 URI of ow2 util file
            mvnURIWar = new URI("mvn:com.peergreen.paas/paas-api/" + PROJECT_VERSION + "/war");
            artifact = artifactBuilder.build("myappli.war", mvnURIWar);
            ArtifactProcessRequest artifactProcessRequest = new ArtifactProcessRequest(artifact);
            artifactProcessRequest.setDeploymentMode(DeploymentMode.DEPLOY);
            deploymentService.process(Collections.singleton(artifactProcessRequest));
            System.out.println("War deployment in progress ....");
            Thread.sleep(3000);
            System.out.println("Deployment ok");
            warDeployed = true;
        }
    }

    @Before
    public void initRestClient() {
        client = ClientBuilder.newClient();
        System.out.println("Init REST client ok");
    }

    @After
    public void terminateRestClient() {
        client.close();
        System.out.println("Terminate REST client ok");
    }

    @Test
    public void checkInjectContext() {
        System.out.println("checkInjectContext ....");
        assertNotNull(context);
    }

    @Test
    public void checkFindApplications() {
        System.out.println("checkFindApplications ....");

        List<ApplicationXML> listApp = null;

        try {
            listApp = client.target(BASE_URL_API + "app")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ApplicationXML>>() {
                    });

        } catch (WebApplicationException ex) {
            fail("Status not found : " + Response.Status.NOT_FOUND);
        }

        System.out.println("result = " + listApp);
        assertNotNull(listApp);
        assertFalse(listApp.isEmpty());
        assertEquals(listApp.get(0).getAppId(), "1");
        assertEquals(listApp.get(0).getAppName(), "myapp");
    }

    @Test
    public void checkCreateApplication() {
        System.out.println("checkCreateApplication ....");


        InputStream input = this.getClass().getClassLoader().getResourceAsStream("cloud-application.xml");
        String inputStreamString = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
        System.out.println("desc=" + inputStreamString);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "app")
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.xml(inputStreamString),
                            Response.class);

        } catch (WebApplicationException ex) {
            fail("Exception : " + ex);
        }

        ApplicationXML appCreated = response.readEntity(ApplicationXML.class);
        System.out.println("appCreated = " + appCreated.getAppId());
        System.out.println("appCreated = " + appCreated.getAppName());

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            fail("Status=" + response.getStatus());
        }

        assertNotNull(appCreated);
        assertEquals(appCreated.getAppName(), "testapp");
    }

    @Test
    public void checkCreateEnv() {
        System.out.println("checkCreateEnv ....");

        InputStream input = this.getClass().getClassLoader().getResourceAsStream("environment-template.xml");
        String inputStreamString = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
        System.out.println("desc=" + inputStreamString);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "environment")
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.xml(inputStreamString),
                            Response.class);

        } catch (WebApplicationException ex) {
            fail("Exception : " + ex);
        }

        System.out.println("envCreated = " + response.getEntity().toString());

        TaskXML task = response.readEntity(TaskXML.class);
        System.out.println("task = " + task);

        if (response.getStatus() != Response.Status.ACCEPTED.getStatusCode()) {
            fail("Status=" + response.getStatus());
        }

        assertNotNull(task);
    }

    @Test
    public void checkFindEnvironments() {
        System.out.println("checkFindEnvironments ....");

        List<EnvironmentXML> listEnv = null;

        try {
            listEnv = client.target(BASE_URL_API + "environment")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<EnvironmentXML>>() {
                    });

        } catch (WebApplicationException ex) {
            fail("Status not found : " + Response.Status.NOT_FOUND);
        }

        System.out.println("result = " + listEnv);
        assertNotNull(listEnv);
        assertFalse(listEnv.isEmpty());
        assertEquals(listEnv.get(0).getEnvName(), "myenv");
    }

}
