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

import java.util.Map;
import fish.focus.uvms.activity.model.schemas.FishingTripResponse;
import fish.focus.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.MessageCountDTO;
import fish.focus.uvms.activity.service.exception.ServiceException;
import fish.focus.uvms.activity.service.search.FishingActivityQuery;

public interface FishingTripService {

    /**
     * Return FishingTripSummary view screen data for specified Fishing Trip ID
     *
     * @param fishingTripId
     * @return FishingTripSummaryViewDTO All of summary view data
     * @throws ServiceException
     */
    FishingTripSummaryViewDTO getFishingTripSummaryAndReports(String fishingTripId) throws ServiceException;


    /**
     * get Vessel Details for Perticular fishing trip (this is for fishing trip summary view)
     *
     * @param fishingTripId
     */
    VesselDetailsDTO getVesselDetailsForFishingTrip(String fishingTripId);


    /**
     * Service that given a trip-id collects all the message summs for it and returns a MessageCountDTO object;
     *
     * @param tripId
     * @return MessageCountDTO
     */
    MessageCountDTO getMessageCountersForTripId(String tripId);

    /**
     * Retrieves all the catches for the given fishing trip;
     *
     * @param fishingTripId
     */
    Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId);

    FishingTripResponse filterFishingTrips(FishingActivityQuery query) throws ServiceException;

    CatchEvolutionDTO retrieveCatchEvolutionForFishingTrip(String fishingTripId) throws ServiceException;
}
