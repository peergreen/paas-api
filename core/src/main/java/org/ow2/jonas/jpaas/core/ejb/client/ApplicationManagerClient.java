/**
 * 
 */
package org.ow2.jonas.jpaas.core.ejb.client;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.jpaas.application.api.ApplicationManager;

/**
 * ApplicationManagerClient is the client class used to invoke operations from
 * the ApplicationManagerBean
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
public class ApplicationManagerClient {

    /*
	final private static String contextFactory = "org.objectweb.carol.jndi.spi.MultiOrbInitialContextFactory";
	final private static String providerURL = "rmi://localhost:1099";
	final private static String ejbName = "ApplicationManagerBean";
	*/


    private static ApplicationManagerClient singleton;
    private  ApplicationManager applicationManagerService;


    public ApplicationManagerClient() {

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
            BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationManager.class).getBundleContext();
            //BundleContext bundleContext = FrameworkUtil.getBundle(BundleContext.class).getBundleContext();
            //BundleContext bundleContext = (BundleContext) initialContext.lookup("java:comp/BundleContext");
            ServiceReference serviceReference = bundleContext.getServiceReference(ApplicationManager.class.getName());
            Object service = bundleContext.getService(serviceReference);
            if (service instanceof ApplicationManager) {
                applicationManagerService = (ApplicationManager) service;
            }
            System.out.println("service: " + applicationManagerService);

        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    }


    private  ApplicationManager getService() {
        return applicationManagerService;
    }


    /**
     * Search the remote interface of the EnvironmentManagerBean in the JNDI
     * server and retrieves its proxy.
     *
     * @return the remote proxy
     */

    public static ApplicationManager getProxy() {
       /* ApplicationManager proxy = null;

        try {
            proxy = (ApplicationManager) getInitialContext().lookup(ejbName);
        } catch (NamingException e) {
            System.out.println("Cannot get Bean: " + e);
        }
        return proxy;*/


        if (singleton == null) {
            singleton = new  ApplicationManagerClient();
        }
        return singleton.getService();

	}

	/**
	 * This method connects to the specified JNDI server and returns a naming
	 * Context.
	 * 
	 * @return a naming Context
	 */
    /*
	static Context getInitialContext() {
		Context initialContext = null;
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
		env.put(Context.PROVIDER_URL, providerURL);

		try {
			initialContext = new InitialContext(env);
		} catch (NamingException e) {
			System.out.println("Cannot get InitialContext: " + e);
		}
		return initialContext;

	}
	*/

}
