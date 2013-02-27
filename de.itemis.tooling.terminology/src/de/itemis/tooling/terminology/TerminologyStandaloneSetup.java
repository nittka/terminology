
package de.itemis.tooling.terminology;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class TerminologyStandaloneSetup extends TerminologyStandaloneSetupGenerated{

	public static void doSetup() {
		new TerminologyStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

