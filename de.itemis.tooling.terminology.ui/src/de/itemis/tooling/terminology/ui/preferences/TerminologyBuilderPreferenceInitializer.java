package de.itemis.tooling.terminology.ui.preferences;

import static org.eclipse.xtext.builder.EclipseOutputConfigurationProvider.*;

import javax.inject.Inject;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.builder.preferences.BuilderPreferenceAccess;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.preferences.PreferenceConstants;

@SuppressWarnings("restriction")
//adapted from BuilderPreferenceAccess$Initializer in order to disable compiler
//by default
public class TerminologyBuilderPreferenceInitializer implements IPreferenceStoreInitializer {
		private IOutputConfigurationProvider outputConfigurationProvider;

		public IOutputConfigurationProvider getOutputConfigurationProvider() {
			return outputConfigurationProvider;
		}

		@Inject
		public void setOutputConfigurationProvider(IOutputConfigurationProvider outputConfigurationProvider) {
			this.outputConfigurationProvider = outputConfigurationProvider;
		}

		public void initialize(IPreferenceStoreAccess preferenceStoreAccess) {
			IPreferenceStore store = preferenceStoreAccess.getWritablePreferenceStore();
			intializeBuilderPreferences(store);
			initializeOutputPreferences(store);
		}

		private void intializeBuilderPreferences(IPreferenceStore store) {
			store.setDefault(BuilderPreferenceAccess.PREF_AUTO_BUILDING, false);
		}

		private void initializeOutputPreferences(IPreferenceStore store) {
			for (OutputConfiguration configuration : outputConfigurationProvider.getOutputConfigurations()) {
				store.setDefault(getKey(configuration, OUTPUT_NAME), configuration.getName());
				store.setDefault(getKey(configuration, OUTPUT_DESCRIPTION), configuration.getDescription());
				store.setDefault(getKey(configuration, OUTPUT_DERIVED), configuration.isSetDerivedProperty());
				store.setDefault(getKey(configuration, OUTPUT_DIRECTORY), configuration.getOutputDirectory());
				store.setDefault(getKey(configuration, OUTPUT_CREATE_DIRECTORY),
						configuration.isCreateOutputDirectory());
				store.setDefault(getKey(configuration, OUTPUT_CLEAN_DIRECTORY),
						configuration.isCanClearOutputDirectory());
				store.setDefault(getKey(configuration, OUTPUT_OVERRIDE), configuration.isOverrideExistingResources());
				store.setDefault(getKey(configuration, OUTPUT_CLEANUP_DERIVED),
						configuration.isCleanUpDerivedResources());
			}
		}
		private String getKey(OutputConfiguration outputConfiguration, String preferenceName) {
			return OUTPUT_PREFERENCE_TAG + PreferenceConstants.SEPARATOR + outputConfiguration.getName()
					+ PreferenceConstants.SEPARATOR + preferenceName;
		}
	}