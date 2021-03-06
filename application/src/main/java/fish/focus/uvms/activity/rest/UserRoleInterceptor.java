/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package fish.focus.uvms.activity.rest;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.Serializable;
import fish.focus.uvms.commons.rest.constants.ErrorCodes;
import fish.focus.uvms.activity.model.schemas.ActivityFeaturesEnum;
import fish.focus.uvms.activity.service.exception.ServiceException;

@IUserRoleInterceptor
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class UserRoleInterceptor implements Serializable {

    @Context
    private transient HttpServletRequest request;

    @AroundInvoke
    public Object interceptRequest(final InvocationContext ic) throws Exception {
        IUserRoleInterceptor iUserRoleInterceptor = ic.getMethod().getAnnotation(IUserRoleInterceptor.class);
        ActivityFeaturesEnum[] features = iUserRoleInterceptor.requiredUserRole(); // Get User role defined in the Rest service
        Object[] parameters = ic.getParameters(); // Request parameters
        HttpServletRequest req = getHttpServletRequest(parameters);
        boolean isUserAuthorized = false;
        for (ActivityFeaturesEnum activityFeaturesEnum : features) {
            isUserAuthorized = req.isUserInRole(activityFeaturesEnum.value());
        }
        if (!isUserAuthorized) {
            throw new ServiceException(ErrorCodes.NOT_AUTHORIZED);
        }
        return ic.proceed();
    }

    private HttpServletRequest getHttpServletRequest(Object[] parameters) throws ServiceException {
        for (Object object : parameters) {
            if (object instanceof HttpServletRequest) {
                return (HttpServletRequest) object;
            }
        }
        throw new ServiceException("REST_SERVICE_REQUEST_PARAM_NOT_FOUND");
    }
}
