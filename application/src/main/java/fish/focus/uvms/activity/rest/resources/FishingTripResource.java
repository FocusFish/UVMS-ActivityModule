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
package fish.focus.uvms.activity.rest.resources;

import fish.focus.uvms.commons.rest.resource.UnionVMSResource;
import fish.focus.uvms.activity.model.schemas.ActivityFeaturesEnum;
import fish.focus.uvms.activity.rest.ActivityExceptionInterceptor;
import fish.focus.uvms.activity.rest.IUserRoleInterceptor;
import fish.focus.uvms.activity.service.FishingTripService;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionDTO;
import fish.focus.uvms.activity.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/trip")
@Slf4j
@Stateless
public class FishingTripResource extends UnionVMSResource {

    @Context
    private UriInfo context;

    @EJB
    private FishingTripService fishingTripService;

    @GET
    @Path("/reports/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripSummary(@Context HttpServletRequest request,
                                          @Context HttpServletResponse response,
                                          @HeaderParam("scopeName") String scopeName,
                                          @HeaderParam("roleName") String roleName,
                                          @PathParam("fishingTripId") String fishingTripId) throws ServiceException {

        log.debug("Fishing Trip summary from fishing trip: {}", fishingTripId);
        return createSuccessResponse(fishingTripService.getFishingTripSummaryAndReports(fishingTripId));
    }

    @GET
    @Path("/vessel/details/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getVesselDetails(@Context HttpServletRequest request,
                                     @Context HttpServletResponse response,
                                     @PathParam("fishingTripId") String fishingTripId) {

        log.debug("Getting Vessels details for trip: {}", fishingTripId);
        return createSuccessResponse(fishingTripService.getVesselDetailsForFishingTrip(fishingTripId));
    }

    @GET
    @Path("/messages/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripMessageCounter(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response,
                                                 @PathParam("fishingTripId") String fishingTripId) {

        log.debug("Message counters for fishing trip: {}", fishingTripId);
        return createSuccessResponse(fishingTripService.getMessageCountersForTripId(fishingTripId));
    }

    @GET
    @Path("/catches/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripCatchReports(@Context HttpServletRequest request,
                                               @Context HttpServletResponse response,
                                               @PathParam("fishingTripId") String fishingTripId) {

        log.debug("Catches for fishing trip: {}", fishingTripId);
        return createSuccessResponse(fishingTripService.retrieveFaCatchesForFishingTrip(fishingTripId));
    }

    @GET
    @Path("/catchevolution/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripCatchEvolution(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response,
                                                 @PathParam("fishingTripId") String fishingTripId) throws ServiceException {
        log.debug("Catch evolution for fishing trip: {}", fishingTripId);
        CatchEvolutionDTO data = fishingTripService.retrieveCatchEvolutionForFishingTrip(fishingTripId);
        return createSuccessResponse(data);
    }
}
