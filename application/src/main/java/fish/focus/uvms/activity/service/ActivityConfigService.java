/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package fish.focus.uvms.activity.service;

import javax.ejb.Local;
import fish.focus.uvms.activity.service.dto.config.ActivityConfigDTO;

@Local
public interface ActivityConfigService {

    /**
     * This service converts the Admin Preferences config received from USM into Activity Config DTO
     *
     * @param adminConfig
     * @return ActivityConfigDTO
     * @throws ServiceException
     */
    ActivityConfigDTO getAdminConfig(String adminConfig);

    /**
     * This service converts the user config received from USM into Activity Config DTO and merges non overridden values
     * from Admin Configuration.
     *
     * @param userConfig
     * @param adminConfig
     * @return ActivityConfigDTO
     * @throws ServiceException
     */
    ActivityConfigDTO getUserConfig(String userConfig, String adminConfig);

    /**
     * This service converts updates Admin Configuration received from frontend to Json String for saving into USM
     *
     * @param config
     * @return Admin config in Json String
     * @throws ServiceException
     */
    String saveAdminConfig(ActivityConfigDTO config);

    /**
     * This services updates the user configuration with the updated values received from fronend
     * and returns the updated value in Json
     *
     * @param updatedConfig
     * @param userConfig
     * @return Admin config in Json String
     * @throws ServiceException
     */
    String saveUserConfig(ActivityConfigDTO updatedConfig, String userConfig);

    /**
     * This service reset selected properties in user configuration.
     * The Reset values gets deleted from User config and fetch from default value set by admin.
     *
     * @param resetConfig
     * @param userConfig
     * @throws ServiceException
     */
    String resetUserConfig(ActivityConfigDTO resetConfig, String userConfig);
}
