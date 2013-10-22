/**
 * 
 */
package org.ow2.jonas.jpaas.core.ejb.client;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.jpaas.application.api.ApplicationManager;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManager;

/**
 * ApplicationManagerClient is the client class used to invoke operations from
 * the EnvironmentManagerBean
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
public class EnvironmentManagerClient {

	/*
	final private static String contextFactory = "org.objectweb.carol.jndi.spi.MultiOrbInitialContextFactory";
	final private static String providerURL = "rmi://localhost:1099";
	final private static String ejbName = "EnvironmentManagerBean";
	 */

    private static EnvironmentManagerClient singleton;
    private  EnvironmentManager environmentManagerService;

    public EnvironmentManagerClient() {

        Context initialContext = null;
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        //env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        //env.put(Context.PROVIDER_URL, providerURL);

        /*
        try {
            initialContext = new InitialContext(env);
        } catch (NamingException e) {
            System.out.println("Cannot get InitialContext: " + e);
        }
        */


        try {
            BundleContext bundleContext = FrameworkUtil.getBundle(BundleContext.class).getBundleContext();

            //BundleContext bundleContext = (BundleContext) initialContext.lookup("java:comp/BundleContext");
            ServiceReference serviceReference = bundleContext.getServiceReference(EnvironmentManager.class.getName());
            Object service = bundleContext.getService(serviceReference);
            if (service instanceof EnvironmentManager) {
                environmentManagerService = (EnvironmentManager) service;
            }
            System.out.println("service: " + environmentManagerService);

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }


    private  EnvironmentManager getService() {
        return environmentManagerService;
    }


    /**
	 * Search the remote interface of the EnvironmentManagerBean in the JNDI
	 * server and retrieves its proxy.
	 * 
	 * @return the remote proxy
	 */
	public static EnvironmentManager getProxy() {
		/*EnvironmentManager proxy = null;
		try {
			proxy = (EnvironmentManager) getInitialContext().lookup(ejbName);
		} catch (NamingException e) {
			System.out.println("Cannot get Bean: " + e);
		}
		return proxy;*/

        if (singleton == null) {
            singleton = new  EnvironmentManagerClient();
        }
        return singleton.getService();


	}

	/**
	 * This method connects to the specified JNDI server and returns a naming
	 * Context.
	 * 
	 * @return a naming Context
	 */
	static Context getInitialContext() {
		Context initialContext = null;
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		//env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
		//env.put(Context.PROVIDER_URL, providerURL);

		try {
			initialContext = new InitialContext(env);
		} catch (NamingException e) {
			System.out.println("Cannot get InitialContext: " + e);
		}
		return initialContext;

	}

}
