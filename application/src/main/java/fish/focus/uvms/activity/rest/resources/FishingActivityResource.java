/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.activity.rest.resources;

import fish.focus.uvms.commons.rest.resource.UnionVMSResource;
import fish.focus.uvms.activity.fa.utils.FaReportSourceEnum;
import fish.focus.uvms.activity.model.schemas.ActivityFeaturesEnum;
import fish.focus.uvms.activity.model.schemas.FishingTripResponse;
import fish.focus.uvms.activity.rest.ActivityExceptionInterceptor;
import fish.focus.uvms.activity.rest.IUserRoleInterceptor;
import fish.focus.uvms.activity.service.ActivityService;
import fish.focus.uvms.activity.service.FishingTripService;
import fish.focus.uvms.activity.service.dto.FilterFishingActivityReportResultDTO;
import fish.focus.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import fish.focus.uvms.activity.service.exception.ServiceException;
import fish.focus.uvms.activity.service.search.FishingActivityQuery;
import fish.focus.uvms.activity.service.search.FishingActivityQueryWithStringMaps;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/fa")
@Slf4j
@Stateless
public class FishingActivityResource extends UnionVMSResource {

    @EJB
    private ActivityService activityService;

    @EJB
    private FishingTripService fishingTripService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/commChannel")
    public Response getCommunicationChannels() {
        log.debug("getCommunicationChannels");
        return createSuccessResponse(FaReportSourceEnum.values());
    }


    @POST
    @Path("/list")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response listActivityReportsByQuery(@Context HttpServletRequest request,
                                               @HeaderParam("scopeName") String scopeName,
                                               @HeaderParam("roleName") String roleName,
                                               FishingActivityQueryWithStringMaps fishingActivityQueryWithStringMaps) throws ServiceException {
        log.debug("Query Received to search Fishing Activity Reports. " + fishingActivityQueryWithStringMaps);
        if (fishingActivityQueryWithStringMaps == null) {
            return createErrorResponse("Query to find list is null.");
        }
        FishingActivityQuery fishingActivityQuery = fishingActivityQueryWithStringMaps.convert();

        FilterFishingActivityReportResultDTO resultDTO = activityService.getFishingActivityListByQuery(fishingActivityQuery);
        return createSuccessPaginatedResponse(resultDTO.getResultList(), resultDTO.getTotalCountOfRecords());
    }

    @POST
    @Path("/listTrips")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response listFishingTripsByQuery(@Context HttpServletRequest request,
                                            @HeaderParam("scopeName") String scopeName,
                                            @HeaderParam("roleName") String roleName,
                                            FishingActivityQueryWithStringMaps fishingActivityQueryWithStringMaps) throws ServiceException {

        log.debug("Query Received to search Fishing Activity Reports. " + fishingActivityQueryWithStringMaps);
        if (fishingActivityQueryWithStringMaps == null) {
            return createErrorResponse("Query to find list is null.");
        }
        FishingActivityQuery fishingActivityQuery = fishingActivityQueryWithStringMaps.convert();

        FishingTripResponse fishingTripIdsForFilter = fishingTripService.filterFishingTrips(fishingActivityQuery);
        return createSuccessResponse(fishingTripIdsForFilter);
    }

    @GET
    @Path("/history/{referenceId}/{schemeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getAllCorrections(@Context HttpServletRequest request,
                                      @Context HttpServletResponse response,
                                      @PathParam("referenceId") String referenceId,
                                      @PathParam("schemeId") String schemeId) {

        List<FaReportCorrectionDTO> faReportHistory = activityService.getFaReportHistory(referenceId, schemeId);
        return createSuccessResponse(faReportHistory);
    }

    @GET
    @Path("/previous/{activityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getPreviousFishingActivity(@Context HttpServletRequest request,
                                               @Context HttpServletResponse response,
                                               @PathParam("activityId") int activityId) {
        log.debug("Received Activity ID {}", activityId);
        int previousFishingActivity = activityService.getPreviousFishingActivity(activityId);
        return createSuccessResponse(previousFishingActivity);
    }

    @GET
    @Path("/next/{activityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.LIST_ACTIVITY_REPORTS})
    public Response getNextFishingActivity(@Context HttpServletRequest request,
                                           @Context HttpServletResponse response,
                                           @PathParam("activityId") int activityId) {
        log.debug("Received Activity ID {}", activityId);
        int nextFishingActivity = activityService.getNextFishingActivity(activityId);
        return createSuccessResponse(nextFishingActivity);
    }
}
