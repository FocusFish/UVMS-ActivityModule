/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package fish.focus.uvms.activity.rest.resources;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import fish.focus.uvms.constants.AuthConstants;
import fish.focus.uvms.exception.ServiceException;
import fish.focus.uvms.commons.rest.resource.UnionVMSResource;
import fish.focus.uvms.rest.security.bean.USMService;
import fish.focus.uvms.activity.model.schemas.ActivityFeaturesEnum;
import fish.focus.uvms.activity.rest.ActivityExceptionInterceptor;
import fish.focus.uvms.activity.rest.IUserRoleInterceptor;
import fish.focus.uvms.activity.service.ActivityConfigService;
import fish.focus.uvms.activity.service.dto.config.ActivityConfigDTO;

@Path("/config")
@Stateless
public class ConfigResource extends UnionVMSResource {

    private static final String DEFAULT_CONFIG = "DEFAULT_CONFIG";
    private static final String USER_CONFIG = "USER_CONFIG";
    private static final String CONFIG_INIT_PARAMETER = "usmApplication";

    @EJB
    private USMService usmService;

    @EJB
    private ActivityConfigService preferenceConfigService;

    @GET
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MANAGE_ADMIN_CONFIGURATIONS})
    public Response getAdminConfig(@Context HttpServletRequest request) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter(CONFIG_INIT_PARAMETER);
        String adminConfig = usmService.getOptionDefaultValue(DEFAULT_CONFIG, applicationName);
        return createSuccessResponse(preferenceConfigService.getAdminConfig(adminConfig));
    }

    @POST
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.MANAGE_ADMIN_CONFIGURATIONS})
    public Response saveAdminConfig(@Context HttpServletRequest request, ActivityConfigDTO activityConfigDTO) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter(CONFIG_INIT_PARAMETER);
        String adminJson = preferenceConfigService.saveAdminConfig(activityConfigDTO);
        usmService.setOptionDefaultValue(DEFAULT_CONFIG, adminJson, applicationName);
        return createSuccessResponse();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response getUserConfig(@Context HttpServletRequest request,
                                  @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                  @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter(CONFIG_INIT_PARAMETER);
        String username = request.getRemoteUser();
        String adminConfig = usmService.getOptionDefaultValue(DEFAULT_CONFIG, applicationName);
        String userConfig = usmService.getUserPreference(USER_CONFIG, username, applicationName, roleName, scopeName);
        return createSuccessResponse(preferenceConfigService.getUserConfig(userConfig, adminConfig));
    }

    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response saveUserConfig(@Context HttpServletRequest request,
                                   @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                   @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName,
                                   ActivityConfigDTO activityConfigDTO) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter(CONFIG_INIT_PARAMETER);
        String username      = request.getRemoteUser();
        String userConfig    = usmService.getUserPreference(USER_CONFIG, username, applicationName, roleName, scopeName);
        String updatedConfig = preferenceConfigService.saveUserConfig(activityConfigDTO, userConfig);
        usmService.putUserPreference(USER_CONFIG, updatedConfig, applicationName, scopeName, roleName, username);
        return createSuccessResponse();
    }

    @POST
    @Path("/user/reset")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    public Response resetUserConfig(@Context HttpServletRequest request,
                                    @HeaderParam(AuthConstants.HTTP_HEADER_SCOPE_NAME) String scopeName,
                                    @HeaderParam(AuthConstants.HTTP_HEADER_ROLE_NAME) String roleName,
                                    ActivityConfigDTO activityConfigDTO) throws ServiceException {
        String applicationName = request.getServletContext().getInitParameter(CONFIG_INIT_PARAMETER);
        String username        = request.getRemoteUser();
        String adminConfig     = usmService.getOptionDefaultValue(DEFAULT_CONFIG, applicationName);
        String userConfig      = usmService.getUserPreference(USER_CONFIG, username, applicationName, roleName, scopeName);
        String updatedConfig   = preferenceConfigService.resetUserConfig(activityConfigDTO, userConfig);
        usmService.putUserPreference(USER_CONFIG, updatedConfig, applicationName, scopeName, roleName, username);
        return createSuccessResponse(preferenceConfigService.getUserConfig(updatedConfig, adminConfig));
    }
}
