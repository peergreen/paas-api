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
import org.ow2.jonas.jpaas.api.xml.*;
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



    private List<ApplicationXML> checkFindApplications(String appName, boolean testIfPresent) {
        System.out.println("checkFindApplications ....");

        List<ApplicationXML> listApp = null;

        try {
            listApp = client.target(BASE_URL_API + "app")
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ApplicationXML>>() {
                    });

        } catch (WebApplicationException ex) {
            fail("Error : " + ex.getMessage());
        }

        System.out.println("result = " + listApp);
        assertNotNull(listApp);
        assertFalse(listApp.isEmpty());
        boolean found = false;
        for (ApplicationXML app:listApp) {
            if (app.getAppName().equals(appName)) {
                found = true;
                break;
            }

        }
        if (testIfPresent) {
            assertTrue(found);
        } else {
            assertFalse(found);
        }
        return listApp;
    }

    private List<ApplicationVersionXML> checkFindApplicationVersions(String appId, String labelVersion, boolean testIfPresent) {
        System.out.println("checkFindApplicationVersions ....");

        List<ApplicationVersionXML> versions = null;
        String url = BASE_URL_API + "app/" + appId + "/version";
        System.out.println("url=" + url);

        try {
            versions = client.target(url)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ApplicationVersionXML>>() {
                    });

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Error : " + ex.getMessage());
        }

        System.out.println("result = " + versions);
        assertNotNull(versions);
        assertFalse(versions.isEmpty());
        boolean found = false;
        for (ApplicationVersionXML version:versions) {
            if (version.getAppVerLabel().equals(labelVersion)) {
                found = true;
                break;
            }

        }
        if (testIfPresent) {
            assertTrue(found);
        } else {
            assertFalse(found);
        }
        return versions;
    }

    private List<ApplicationVersionInstanceXML> checkFindApplicationVersionInstances(String appId, String versionId, String instanceName, boolean testIfPresent) {
        System.out.println("checkFindApplicationVersionInstances ....");

        List<ApplicationVersionInstanceXML> instances = null;
        String url = BASE_URL_API + "app/" + appId + "/version/" + versionId + "/instance";
        System.out.println("url=" + url);


        try {
            instances = client.target(url)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<List<ApplicationVersionInstanceXML>>() {
                    });

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Error : " + ex.getMessage());
        }

        System.out.println("result = " + instances);
        assertNotNull(instances);
        assertFalse(instances.isEmpty());
        boolean found = false;
        for (ApplicationVersionInstanceXML instance:instances) {
            if (instance.getInstanceName().equals(instanceName)) {
                found = true;
                break;
            }

        }
        if (testIfPresent) {
            assertTrue(found);
        } else {
            assertFalse(found);
        }
        return instances;
    }

    @Test
    public void checkFindApplicationsMyApp() {
        System.out.println("checkFindApplications ....");
        checkFindApplications("myapp", true);
        checkFindApplicationVersions("1", "myversion", true);
        checkFindApplicationVersionInstances("1", "1", "myinstance", true);
    }

    private ApplicationXML checkGetApplication(String appId, String appName) {
        System.out.println("checkGetApplication: appId=" + appId + ", appName=" + appName);
        ApplicationXML app = null;

        try {
            app = client.target(BASE_URL_API + "app/" + appId)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<ApplicationXML>() {
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error : " + ex.getMessage());
        }
        System.out.println("result = " + app);
        assertNotNull(app);
        assertEquals(app.getAppId(), appId);

        if (appName != null) {
             assertEquals(app.getAppName(), appName);
        }

        return app;
    }

    private ApplicationVersionXML checkGetApplicationVersion(String appId, String versionId, String versionLabel) {
        System.out.println("checkGetApplicationVersion: appId=" + appId + ", versionId=" + versionId + ", versionLabel=" + versionLabel);
        ApplicationVersionXML version = null;

        try {
            version = client.target(BASE_URL_API + "app/" + appId + "/version/" + versionId)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<ApplicationVersionXML>() {
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error : " + ex.getMessage());
        }
        System.out.println("result = " + version);
        assertNotNull(version);
        assertEquals(version.getAppId(), appId);
        assertEquals(version.getAppVerId(), versionId);

        if (versionLabel != null) {
            assertEquals(version.getAppVerLabel(), versionLabel);
        }

        return version;
    }

    private ApplicationVersionInstanceXML checkGetApplicationVersionInstance(String appId, String versionId, String instanceId, String instanceName) {
        System.out.println("checkGetApplicationVersionInstance: appId=" + appId + ", versionId=" + versionId + ", instanceId=" + instanceId + ", instanceName" + instanceName);
        ApplicationVersionInstanceXML instance = null;

        try {
            instance = client.target(BASE_URL_API + "app/" + appId + "/version/" + versionId + "/instance/" + instanceId)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<ApplicationVersionInstanceXML>() {
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error : " + ex.getMessage());
        }
        System.out.println("result = " + instance);
        assertNotNull(instance);
        assertEquals(instance.getAppId(), appId);
        assertEquals(instance.getVersionID(), versionId);
        assertEquals(instance.getInstanceId(), instanceId);

        if (instanceName != null) {
            assertEquals(instance.getInstanceName(), instanceName);
        }

        return instance;
    }

    @Test
    public void checkGetApplicationMyApp() {
        checkGetApplication("1", "myapp");
        checkGetApplicationVersion("1", "1", "myversion");
        checkGetApplicationVersionInstance("1", "1", "1", "myinstance");
    }

    private ApplicationXML checkCreateApplication(String filename, String appName) {
        System.out.println("checkCreateApplication, fileName=" + filename);

        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        String inputStreamString = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
        System.out.println("create app desc=" + inputStreamString);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "app")
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.xml(inputStreamString),
                            Response.class);

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Exception : " + ex);
        }

        ApplicationXML appCreated = response.readEntity(ApplicationXML.class);
        System.out.println("appCreated = " + appCreated.getAppId());
        System.out.println("appCreated = " + appCreated.getAppName());

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            fail("Status=" + response.getStatus());
        }

        assertNotNull(appCreated);
        assertEquals(appCreated.getAppName(), appName);

        return appCreated;

    }

    private ApplicationVersionXML checkCreateApplicationVersion(String appId, String filename, String versionLabel) {
        System.out.println("checkCreateApplicationVersion, appId=" + appId + ", fileName=" + filename);

        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        String inputStreamString = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
        System.out.println("create version desc=" + inputStreamString);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "app/" + appId + "/version")
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.xml(inputStreamString),
                            Response.class);

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Exception : " + ex);
        }

        ApplicationVersionXML versionCreated = response.readEntity(ApplicationVersionXML.class);
        System.out.println("versionCreated = " + versionCreated.getAppVerId());
        System.out.println("versionCreated = " + versionCreated.getAppVerLabel());

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            fail("Status=" + response.getStatus());
        }

        assertNotNull(versionCreated);
        assertEquals(versionCreated.getAppVerLabel(), versionLabel);

        return versionCreated;

    }

    private ApplicationVersionInstanceXML checkCreateApplicationVersionInstance(String appId, String versionId, String filename, String instanceName) {
        System.out.println("checkCreateApplicationVersionInstance, appId=" + appId + ", versionId=" + versionId + ", fileName=" + filename);

        InputStream input = this.getClass().getClassLoader().getResourceAsStream("cloud-application-version-instance-deployment.xml");
        String inputStreamString = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
        System.out.println("create instance desc=" + inputStreamString);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "app/" + appId + "/version/" + versionId + "/instance")
                    .request(MediaType.APPLICATION_XML)
                    .post(Entity.xml(inputStreamString),
                            Response.class);

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Exception : " + ex);
        }

        ApplicationVersionInstanceXML instanceCreated = response.readEntity(ApplicationVersionInstanceXML.class);
        System.out.println("instanceCreated = " + instanceCreated.getInstanceId());
        System.out.println("instanceCreated = " + instanceCreated.getInstanceName());

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            fail("Status=" + response.getStatus());
        }

        assertNotNull(instanceCreated);
        assertEquals(instanceCreated.getInstanceName(), instanceName);

        return instanceCreated;

    }

    private void checkDeleteApplication(String appId) {
        System.out.println("checkDeleteApplication, appId=" + appId);

        ApplicationXML app = checkGetApplication(appId, null);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "app/" + appId)
                    .request(MediaType.APPLICATION_XML)
                    .delete(Response.class);

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Exception : " + ex);
        }

        checkFindApplications(app.getAppName(),false);

    }

    private void checkDeleteApplicationVersion(String appId, String versionId) {
        System.out.println("checkDeleteApplication, appId=" + appId + ", versionId=" + versionId);

        ApplicationVersionXML version = checkGetApplicationVersion(appId, versionId, null);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "app/" + appId + "/version/" + versionId)
                    .request(MediaType.APPLICATION_XML)
                    .delete(Response.class);

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Exception : " + ex);
        }

        checkFindApplications(version.getAppVerLabel(),false);

    }

    private void checkDeleteApplicationVersionInstance(String appId, String versionId, String instanceId) {
        System.out.println("checkDeleteApplication, appId=" + appId + ", versionId=" + versionId + ", instanceId=" + instanceId);

        ApplicationVersionInstanceXML instance = checkGetApplicationVersionInstance(appId, versionId, instanceId, null);

        Response response = null;
        try {
            response = client.target(BASE_URL_API + "app/" + appId + "/version/" + versionId + "/instance/" + instanceId)
                    .request(MediaType.APPLICATION_XML)
                    .delete(Response.class);

        } catch (WebApplicationException ex) {
            ex.printStackTrace();
            fail("Exception : " + ex);
        }

        checkFindApplications(instance.getInstanceName(),false);

    }
    @Test
    public void checkApplicationLifeCycle() {
        System.out.println("checkApplicationLifeCycle ....");

        // Test application creation
        ApplicationXML appCreated = checkCreateApplication("cloud-application.xml", "testapp");
        ApplicationVersionXML versionCreated = checkCreateApplicationVersion(appCreated.getAppId(), "cloud-application-version.xml", "7.0.0-SNAPSHOT");
        ApplicationVersionInstanceXML instanceCreated = checkCreateApplicationVersionInstance(appCreated.getAppId(), versionCreated.getAppVerId(), "cloud-application-version-instance-deployment.xml", "test-for-demo");

        // Test getters
        checkGetApplication(appCreated.getAppId(), appCreated.getAppName());
        checkGetApplicationVersion(versionCreated.getAppId(), versionCreated.getAppVerId(), versionCreated.getAppVerLabel());
        checkGetApplicationVersionInstance(instanceCreated.getAppId(),instanceCreated.getVersionID(),instanceCreated.getInstanceId(),instanceCreated.getInstanceName());

        // Test finders
        checkFindApplications(appCreated.getAppName(),true);
        checkFindApplicationVersions(versionCreated.getAppId(),versionCreated.getAppVerLabel(),true);
        checkFindApplicationVersionInstances(instanceCreated.getAppId(),instanceCreated.getVersionID(), instanceCreated.getInstanceName(),true);

        // Test deletes
        checkDeleteApplicationVersionInstance(instanceCreated.getAppId(),instanceCreated.getVersionID(),instanceCreated.getInstanceId());
        checkDeleteApplicationVersion(versionCreated.getAppId(), versionCreated.getAppVerId());
        checkDeleteApplication(appCreated.getAppId());
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
        System.out.println("Task[1] = " + task);

        if (response.getStatus() != Response.Status.ACCEPTED.getStatusCode()) {
            fail("Status=" + response.getStatus());
        }

        assertNotNull(task);

        boolean success = false;
        if (task.getStatus().equals("SUCCESS")) {
            success = true;
        }

        int retry = 2;
        while (! success && retry < 10){
            System.out.println("Waiting ending of creating env - retry = " + retry);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            String target = null;
            try {
                target = BASE_URL_API + "task/" + task.getId();
                task = client.target(target)
                        .request(MediaType.APPLICATION_XML)
                        .get(new GenericType<TaskXML>() {
                        });

            } catch (WebApplicationException ex) {
                fail("Exception : " + target + ", ex=" + ex);
            }

            System.out.println("Task[" + retry + "] = " + task);

            assertNotNull(task);
            if (task.getStatus().equals("SUCCESS")) {
                success = true;
            }
            retry++;
        }
        assertEquals(task.getStatus(), "SUCCESS");

        String target = null;
        EnvironmentXML env = null;
        try {
            target = task.getOwner().getHref();
            env = client.target(target)
                    .request(MediaType.APPLICATION_XML)
                    .get(new GenericType<EnvironmentXML>() {
                    });

        } catch (WebApplicationException ex) {
            fail("Exception : " + target + ", ex=" + ex);
        }

        System.out.println("Env=" + env);
        assertEquals(env.getEnvName(),"testenv");

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
            fail("Error : " + ex.getMessage());
        }

        System.out.println("result = " + listEnv);
        assertNotNull(listEnv);
        assertFalse(listEnv.isEmpty());
    }

}
