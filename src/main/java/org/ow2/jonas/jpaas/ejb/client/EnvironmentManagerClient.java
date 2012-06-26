/**
 * 
 */
package org.ow2.jonas.jpaas.ejb.client;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManagerRemote;



/**
 * ApplicationManagerClient is the client class used to invoke operations from
 * the EnvironmentManagerBean
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
public class EnvironmentManagerClient {

	final private static String contextFactory = "org.objectweb.carol.jndi.spi.MultiOrbInitialContextFactory";
	final private static String providerURL = "rmi://localhost:1099";
	final private static String ejbName = "EnvironmentManagerBean";

	/**
	 * Search the remote interface of the EnvironmentManagerBean in the JNDI
	 * server and retrieves its proxy.
	 * 
	 * @return the remote proxy
	 */
	public static EnvironmentManagerRemote getProxy() {
		EnvironmentManagerRemote proxy = null;
		try {
			proxy = (EnvironmentManagerRemote) getInitialContext().lookup(
					ejbName);
		} catch (NamingException e) {
			System.out.println("Cannot get Bean: " + e);
		}
		return proxy;

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
		env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
		env.put(Context.PROVIDER_URL, providerURL);

		try {
			initialContext = new InitialContext(env);
		} catch (NamingException e) {
			System.out.println("Cannot get InitialContext: " + e);
		}
		return initialContext;

	}

}
