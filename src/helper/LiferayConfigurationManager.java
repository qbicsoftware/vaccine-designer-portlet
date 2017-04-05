/*******************************************************************************
 * QBiC Project Wizard enables users to create hierarchical experiments including different study conditions using factorial design.
 * Copyright (C) "2016"  Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package helper;

import java.util.Properties;

import com.liferay.util.portlet.PortletProps; // util-java.jar

/**
 * Implements {@see ConfigurationManager} Portal dependent
 * 
 * @author wojnar
 * 
 */
public enum LiferayConfigurationManager implements ConfigurationManager {
  Instance;
  public static final String DATASOURCE_USER = "datasource.user";
  public static final String DATASOURCE_PASS = "datasource.password";
  public static final String DATASOURCE_URL = "datasource.url";

  public static final String TMP_FOLDER = "tmp.folder";
  public static final String BARCODE_SCRIPTS_FOLDER = "barcode.scripts";
  public static final String BARCODE_RESULTS = "barcode.results";
  public static final String BARCODE_PATH_VARIABLE = "path.variable";

  public static final String ATTACHMENT_URI = "attachment.uri";
  public static final String ATTACHMENT_USER = "attachment.user";
  public static final String ATTACHMENT_PASS = "attachment.password";
  public static final String ATTACHMENT_MAX_SIZE = "max.attachment.size";

  public static final String MSQL_HOST = "mysql.host";
  public static final String MSQL_DB = "mysql.db";
  public static final String MSQL_USER = "mysql.user";
  public static final String MSQL_PORT = "mysql.port";
  public static final String MSQL_PASS = "mysql.pass";
  
  private String LABELING_METHODS = "vocabulary.ms.labeling";

  private String configurationFileName;
  private String dataSourceUser;
  private String dataSourcePass;
  private String dataSourceUrl;

  private String tmpFolder;
  private String barcodeScriptsFolder;
  private String barcodePathVariable;
  private String barcodeResultsFolder;

  private String attachmentURI;
  private String attachmentUser;
  private String attachmentPass;
  private String attachmentMaxSize;

  private String msqlHost;
  private String msqlDB;
  private String msqlUser;
  private String msqlPort;
  private String msqlPass;
  
  private String labelingMethods;

  private boolean initialized = false;

  /*
   * private LiferayConfigurationManager(){ init(); }
   */
  public boolean isInitialized() {
    return initialized;
  }

  public void init() {
    Properties portletConfig = PortletProps.getProperties();
    dataSourceUser = portletConfig.getProperty(DATASOURCE_USER);
    dataSourcePass = portletConfig.getProperty(DATASOURCE_PASS);
    dataSourceUrl = portletConfig.getProperty(DATASOURCE_URL);

    tmpFolder = portletConfig.getProperty(TMP_FOLDER);
    barcodeScriptsFolder = portletConfig.getProperty(BARCODE_SCRIPTS_FOLDER);
    barcodePathVariable = portletConfig.getProperty(BARCODE_PATH_VARIABLE);
    barcodeResultsFolder = portletConfig.getProperty(BARCODE_RESULTS);

    attachmentURI = portletConfig.getProperty(ATTACHMENT_URI);
    attachmentUser = portletConfig.getProperty(ATTACHMENT_USER);
    attachmentPass = portletConfig.getProperty(ATTACHMENT_PASS);
    attachmentMaxSize = portletConfig.getProperty(ATTACHMENT_MAX_SIZE);

    msqlHost = portletConfig.getProperty(MSQL_HOST);
    msqlDB = portletConfig.getProperty(MSQL_DB);
    msqlUser = portletConfig.getProperty(MSQL_USER);
    msqlPort = portletConfig.getProperty(MSQL_PORT);
    msqlPass = portletConfig.getProperty(MSQL_PASS);
    
    labelingMethods = portletConfig.getProperty(LABELING_METHODS);

    initialized = true;
  }

  @Override
  public String getConfigurationFileName() {
    return configurationFileName;
  }

  @Override
  public String getDataSourceUser() {
    return dataSourceUser;
  }

  @Override
  public String getDataSourcePassword() {
    return dataSourcePass;
  }

  @Override
  public String getDataSourceUrl() {
    return dataSourceUrl;
  }

  @Override
  public String getBarcodeScriptsFolder() {
    return barcodeScriptsFolder;
  }

  @Override
  public String getTmpFolder() {
    return tmpFolder;
  }

  @Override
  public String getBarcodePathVariable() {
    return barcodePathVariable;
  }

  @Override
  public String getAttachmentURI() {
    return attachmentURI;
  }

  @Override
  public String getAttachmentUser() {
    return attachmentUser;
  }

  @Override
  public String getAttachmenPassword() {
    return attachmentPass;
  }

  @Override
  public String getAttachmentMaxSize() {
    return attachmentMaxSize;
  }

  @Override
  public String getMysqlHost() {
    return msqlHost;
  }

  @Override
  public String getMysqlPort() {
    return msqlPort;
  }

  @Override
  public String getMysqlDB() {
    return msqlDB;
  }

  @Override
  public String getMysqlUser() {
    return msqlUser;
  }

  @Override
  public String getMysqlPass() {
    return msqlPass;
  }

  @Override
  public String getVocabularyMSLabeling() {
    return labelingMethods;
  }

  @Override
  public String getBarcodeResultsFolder() {
    return barcodeResultsFolder;
  }

}
