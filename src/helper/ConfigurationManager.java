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

/**
 * The ConfigurationManger interface represents the entire .properties file. One might think about
 * adding a getAttribute method in order to make it more generic.
 * 
 * @author wojnar
 * 
 */
public interface ConfigurationManager {

  public String getVocabularyMSLabeling();

  public String getConfigurationFileName();

  public String getDataSourceUser();

  public String getDataSourcePassword();

  public String getDataSourceUrl();

  public String getBarcodeScriptsFolder();

  public String getTmpFolder();

  public String getBarcodePathVariable();

  public String getAttachmentURI();

  public String getAttachmentUser();

  public String getAttachmenPassword();

  public String getAttachmentMaxSize();

  public String getMysqlHost();

  public String getMysqlPort();

  public String getMysqlDB();

  public String getMysqlUser();

  public String getMysqlPass();

  public String getBarcodeResultsFolder();
}
