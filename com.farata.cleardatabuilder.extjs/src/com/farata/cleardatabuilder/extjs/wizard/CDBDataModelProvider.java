package com.farata.cleardatabuilder.extjs.wizard;

import java.util.Set;

import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;

public class CDBDataModelProvider extends JpaFacetInstallDataModelProvider implements CDBFacetDataModelProperties {
	public Set getPropertyNames() {
		Set<String> names = super.getPropertyNames();
		names.add(CDB_PROJECT_TYPE);
		names.add(CDB_EXTJS_FOLDER);
		names.add(CDB_PERSISTANCE_PLATFORM);
		names.add(CDB_SPRING_ITEGRATION);
		names.add(CDB_SAMPLEDB_FOLDER);
		return names;
	}
}