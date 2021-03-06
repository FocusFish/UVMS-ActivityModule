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

import fish.focus.uvms.asset.client.model.AssetDTO;
import fish.focus.uvms.asset.client.model.search.SearchBranch;
import fish.focus.uvms.activity.fa.entities.VesselIdentifierEntity;
import fish.focus.uvms.activity.service.exception.ServiceException;
import java.util.Collection;
import java.util.List;

public interface AssetModuleService {

    /**
     * Service to Get Asset GUIDs by Vessel Identifiers
     *
     * @param vesselIdentifiers vessel identifiers from Activity
     * @return asset GUIDs
     * @throws ServiceException
     */
    List<String> getAssetGuids(Collection<VesselIdentifierEntity> vesselIdentifiers);

    List<String> getAssetGuids(String vesselSearchStr) throws ServiceException;

    List<AssetDTO> getAssets(SearchBranch assetQuery);
}
