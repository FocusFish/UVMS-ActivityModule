/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
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

import java.util.List;
import fish.focus.uvms.activity.service.dto.FilterFishingActivityReportResultDTO;
import fish.focus.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import fish.focus.uvms.activity.service.exception.ServiceException;
import fish.focus.uvms.activity.service.search.FishingActivityQuery;

public interface ActivityService {

    /**
     * returns fishing Activity reports list based on Filters
     *
     * @param query
     * @throws ServiceException
     */
    FilterFishingActivityReportResultDTO getFishingActivityListByQuery(FishingActivityQuery query) throws ServiceException;


    /**
     * <p>
     * This service returns the list of corrections (e.g. deletes, cancels, updates) received previously for a Fishing Activity report
     * Corrections are identified by the <code>referenceId</code> of the selected <code>FluxReportDocument.FluxReportIdentifier</code> recursively.
     * </p>
     *
     * @param refReportId selected FA report Reference Id
     * @param refSchemeId selected FA scheme Reference Id
     * @throws ServiceException Exception
     */
    List<FaReportCorrectionDTO> getFaReportHistory(String refReportId, String refSchemeId);

    boolean checkAndEnrichIfVesselFiltersArePresent(FishingActivityQuery query) throws ServiceException;

    int getPreviousFishingActivity(int fishingActivityId);

    int getNextFishingActivity(int fishingActivityId);
}
