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

import de.uni_tuebingen.qbic.main.LiferayAndVaadinUtils;

/**
 * The ConfigurationManager Factory has only the getInstance mehtod. Dependigng on whether the
 * portlet runs on its own or in a Portal environemnt different implementation of the
 * ConfigurationManager are returned.
 * 
 * @author wojnar
 * 
 */
public class ConfigurationManagerFactory {
  /**
   * checks whether it runs in a Liferay Portal and returns either an independent or an dependent
   * Liferay implementation
   * 
   * @return Instance of ConfigurationManager
   */
  public static ConfigurationManager getInstance() {
    if (LiferayAndVaadinUtils.isLiferayPortlet()) {
      if (!LiferayConfigurationManager.Instance.isInitialized()) {
        LiferayConfigurationManager.Instance.init();
      }
      return LiferayConfigurationManager.Instance;
    }

    if (!LiferayIndependentConfigurationManager.Instance.isInitialized()) {
      LiferayIndependentConfigurationManager.Instance.init();
    }

    return LiferayIndependentConfigurationManager.Instance;
  }
}
