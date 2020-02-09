/*******************************************************************************
 * Copyright (C) 2013-2020 itemis AG (http://www.itemis.eu).
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Alexander Nittka (alex@nittka.de) - initial implementation
 ******************************************************************************/
package de.itemis.tooling.terminology.generator;

import java.util.List;

import org.eclipse.xtext.xbase.lib.Pair;

import de.itemis.tooling.terminology.terminology.SubjectEntries;

public abstract class TerminologyGeneratorParticipant {

	private String id;
	private String defaultFolder;
	private String displayName;
	private String folder;
	private boolean active;

	protected TerminologyGeneratorParticipant(String id, String displayName, String defaultFolder) {
		this.id=id;
		this.displayName=displayName;
		this.defaultFolder=defaultFolder;
		this.folder=defaultFolder;
	}

	/**
	 * provide a list filename->content for the given catagory
	 * files are written to <Compiler-output-folder>/<TerminologyName>/<folder>/<file>
	 * where folder is the folder configured for this generator and file is the 
	 * key element of the return values
	 * */
	public abstract List<Pair<String, CharSequence>> getFileContents(SubjectEntries entries);

	/**
	 * default folder in which the files are placed
	 * */
	public final String getDefaultFolder(){
		return defaultFolder;
	}

	/**
	 * Display name of the generator (shown in the complier preference page)
	 * */
	public final String getDisplayName(){
		return displayName;
	}

	/**
	 * id of this generator
	 * */
	public final String getId(){
		return id;
	}

	public final void setFolder(String folder) {
		this.folder = folder;
	}

	public final String getFolder() {
		if(folder!=null&&folder.length()>0){
			//we do not want any navigation to parent folders
			int i=0;
			while(i<folder.length() &&!Character.isLetterOrDigit(folder.charAt(i))){
				i++;
			}
			folder=folder.substring(i);
		}
		return folder;
	}

	public final void setActive(boolean active) {
		this.active = active;
	}

	public final boolean isActive() {
		return active;
	}

	@Override
	public final String toString() {
		return new StringBuilder()
		.append(getClass())
		.append(" id: ")
		.append(getId())
		.append(" active: ")
		.append(isActive())
		.append(" folder: ")
		.append(getFolder()).toString();
	}
}
