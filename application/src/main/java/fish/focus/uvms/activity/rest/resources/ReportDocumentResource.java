/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package fish.focus.uvms.activity.rest.resources;

import fish.focus.uvms.commons.message.impl.JAXBUtils;
import fish.focus.uvms.commons.rest.resource.UnionVMSResource;
import fish.focus.uvms.activity.model.mapper.FANamespaceMapper;
import fish.focus.uvms.activity.service.FaQueryService;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfaquerymessage._3.FLUXFAQueryMessage;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.time.Instant;

@Path("/report")
@Slf4j
@Stateless
public class ReportDocumentResource extends UnionVMSResource {

    @EJB
    private FaQueryService faQueryService;

    @POST
    @Produces(value = {MediaType.APPLICATION_XML})
    @Consumes(value = {MediaType.APPLICATION_XML})
    public Object getReport(FLUXFAQueryMessage fluxfaQueryMessage) throws JAXBException {
        Instant startInstant = fluxfaQueryMessage.getFAQuery().getSpecifiedDelimitedPeriod().getStartDateTime().getDateTime().toGregorianCalendar().getTime().toInstant();
        Instant endInstant = fluxfaQueryMessage.getFAQuery().getSpecifiedDelimitedPeriod().getEndDateTime().getDateTime().toGregorianCalendar().getTime().toInstant();
        FLUXFAReportMessage reportsByCriteria = faQueryService.getReportsByCriteria(startInstant, endInstant);
        String controlSource = JAXBUtils.marshallJaxBObjectToString(reportsByCriteria, "ISO-8859-15", true, new FANamespaceMapper());
        return clearEmptyTags(controlSource);
    }

    private String clearEmptyTags(String testSource) {
        testSource = testSource
                .replaceAll("<([a-zA-Z][a-zA-Z0-9]*)[^>]*/>", "") // clear tags like  <udt:IndicatorString/>
                .replaceAll("\n?\\s*<(\\w+)></\\1>", "")
                .replaceAll("<ram:SpecifiedPhysicalFLUXGeographicalCoordinate>\\s*</ram:SpecifiedPhysicalFLUXGeographicalCoordinate>", "")
                .replaceAll("<ram:ValueIndicator>\\s*</ram:ValueIndicator>","")
                .replaceAll("(?m)^[ \t]*\r?\n", "");// clear empty lines

        return testSource;
    }
}
