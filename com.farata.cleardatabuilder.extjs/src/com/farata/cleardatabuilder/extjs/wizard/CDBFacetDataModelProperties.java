package com.farata.cleardatabuilder.extjs.wizard;

import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetInstallDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;

public interface CDBFacetDataModelProperties extends IFacetProjectCreationDataModelProperties, JpaFacetInstallDataModelProperties {
	public static final String CDB_PROJECT_TYPE = "cdb.projectType";
	public static final String CDB_APPLICATION_NAME = "cdb.applicationName";
	public static final String CDB_EXTJS_FOLDER = "cdb.extjs.folder";
	public static final String CDB_EXTJS_LOCATION_TYPE = "cdb.extjs.location.type";
	public static final String CDB_PERSISTANCE_PLATFORM = "cdb.persistance.platform";
	public static final String CDB_SPRING_INTEGRATION = "cdb.spring.integration";
	public static final String CDB_SAMPLEDB_FOLDER = "cdb.sampledb.folder";
	
	public static final String TYPE_LOCAL_FOLDER = "cdb.type.local.folder";
	public static final String TYPE_LOCAL_URL = "cdb.type.local.url";
	public static final String TYPE_CDN = "cdb.type.local.cdn";
	
	public static final String PARAM_DS_DRIVER_CLASS_NAME = "dataSourceDriverClassName";
	public static final String PARAM_DS_NAME = "dataSourceName";
	public static final String PARAM_DS_URL = "dataSourceUrl";
	public static final String PARAM_DS_USER = "dataSourceUserName";
	public static final String PARAM_DS_PASSWORD = "dataSourcePassword";
	
	public static final String WARNING_TEXT = "ATTENTION: Hibernate and MyBatis example projects require that cleardb is up and running.\n" +
			"Please navigate to cleardb install folder and start the database with the batch file.\n\n"
			+ "Windows users should use /startdb.bat.\n" 
			+ "Mac users should chmod 777 and then use /startdb.sh";
}
