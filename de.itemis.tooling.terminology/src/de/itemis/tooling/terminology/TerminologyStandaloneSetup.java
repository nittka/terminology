/*******************************************************************************
 * Copyright (C) 2013-2020 itemis AG (http://www.itemis.eu).
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
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

