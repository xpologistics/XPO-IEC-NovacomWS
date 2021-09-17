package com.xpo.novacom.utils;

/**
 * The Interface ConfigurationUtils.
 */
public interface ConfigurationUtils {

	/** The environment variable web service novacom. */
	public String ENVIRONMENT_VARIABLE_WEB_SERVICE_NOVACOM = "WS_NOVACOM_PROPERTIES";

	/** The log4j file name. */
	public String LOG4J_FILE_NAME = "log4j.properties";

	/** The webservice novacom conf file name. */
	public String WEBSERVICE_NOVACOM_CONF_FILE_NAME = "wsnovacom.properties";
	
	public String HAZELCAST_CONF_FILE_NAME = "webnovacom-hazelcast.xml";
	
	// *****************************/
	// **** PROPERTIES EXTERNES ****/
	// *****************************/
	/** Chemin des properties externes de l'application Web Service Parc. */
	public String APP_WEB_SERVICE_NOVACOM_PROPERTIES_EXT_PATH = FWKNDIEnvironment.getVariable(ENVIRONMENT_VARIABLE_WEB_SERVICE_NOVACOM);

	/** Configuration Log4j. */
	public String LOG4J_PROPERTIES_PATH = APP_WEB_SERVICE_NOVACOM_PROPERTIES_EXT_PATH + "/" + LOG4J_FILE_NAME;

	/** Web Service Configuration. */
	public String WS_NOVACOM_CONF_URL_PROPERTIES = APP_WEB_SERVICE_NOVACOM_PROPERTIES_EXT_PATH + "/" + WEBSERVICE_NOVACOM_CONF_FILE_NAME;

	/** The ws parc conf url file. */
	public String WS_NOVACOM_CONF_URL_FILE = "file:///" + WS_NOVACOM_CONF_URL_PROPERTIES;
	
	/** Hazelcast config file*/
	public String HAZELCAST_CONF_URL_PROPERTIES = APP_WEB_SERVICE_NOVACOM_PROPERTIES_EXT_PATH + "/" + HAZELCAST_CONF_FILE_NAME;
	
	public String HAZELCAST_CONF_URL_FILE = "file:///" + HAZELCAST_CONF_URL_PROPERTIES;
	
	/** The conf jdbc datasource. */
	// Conf base de donnees
	public String CONF_JDBC_DATASOURCE = "datasource";

	/** The char space. */
	public String CHAR_SPACE = " ";

	/** The char plus. */
	public String CHAR_PLUS = "+";
}
