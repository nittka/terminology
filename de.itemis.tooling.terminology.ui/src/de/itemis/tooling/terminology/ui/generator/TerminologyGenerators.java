package de.itemis.tooling.terminology.ui.generator;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.xtext.builder.preferences.OptionsConfigurationBlock;
import org.eclipse.xtext.ui.editor.preferences.PreferenceStoreAccessImpl;

import com.google.common.collect.Lists;

import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant;
import de.itemis.tooling.terminology.ui.TerminologyExecutableExtensionFactory;

@SuppressWarnings("restriction")
public class TerminologyGenerators {

	private PreferenceStoreAccessImpl preferenceStoreAccess;
	private List<TerminologyGeneratorParticipant> generators=null;

	@Inject
	public TerminologyGenerators(PreferenceStoreAccessImpl prefStoreAccess) {
		preferenceStoreAccess = prefStoreAccess;
	}

	public List<TerminologyGeneratorParticipant> getGenerators(IProject context){
		//context plays a role only if there are project specific settings
		IPreferenceStore preferences=preferenceStoreAccess.getContextPreferenceStore(context);
		if(!preferences.getBoolean(OptionsConfigurationBlock.IS_PROJECT_SPECIFIC)){
			preferences=preferenceStoreAccess.getContextPreferenceStore(null);
		}
		if(generators==null){
			generators=Lists.newArrayList();
			TerminologyExecutableExtensionFactory factory=new TerminologyExecutableExtensionFactory();
			IConfigurationElement[] configs = Platform.getExtensionRegistry().getConfigurationElementsFor("de.itemis.tooling.terminology.generator");
			for (IConfigurationElement config : configs) {
				String className=config.getAttribute("class");
				try {
					factory.setInitializationData(config, null, className);
					TerminologyGeneratorParticipant generator=(TerminologyGeneratorParticipant)factory.create();
					String folder=preferences.getString(generator.getId()+"_folder");
					if(folder!=null){
						generator.setFolder(folder);
					}else{
						generator.setFolder(generator.getDefaultFolder());
					}
					boolean active=preferences.getBoolean(generator.getId()+"_active");
					generator.setActive(active);
					generators.add(generator);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return generators;
	}

	public void applyDefaults(IPreferenceStore store){
		for (TerminologyGeneratorParticipant generator : getGenerators(null)) {
			store.setValue(generator.getId()+"_active", false);
			store.setValue(generator.getId()+"_folder", generator.getDefaultFolder());
//			store.setDefault(generator.getId()+"_active", false);
//			store.setDefault(generator.getId()+"_folder", generator.getDefaultFolder());
			generator.setActive(false);
			generator.setFolder(generator.getDefaultFolder());
		}
	}

	public void apply(IPreferenceStore store){
		for (TerminologyGeneratorParticipant generator : getGenerators(null)) {
			store.setValue(generator.getId()+"_active", ""+generator.isActive());
			store.setValue(generator.getId()+"_folder", generator.getFolder());
		}
	}
}