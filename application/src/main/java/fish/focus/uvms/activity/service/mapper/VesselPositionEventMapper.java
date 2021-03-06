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

package fish.focus.uvms.activity.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import fish.focus.uvms.activity.fa.entities.VesselPositionEventEntity;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselPositionEvent;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class VesselPositionEventMapper {

    @Mapping(target = "typeCode", source = "vesselPositionEvent.typeCode.value")
    @Mapping(target = "obtainedOccurrenceDateTime", source = "vesselPositionEvent.obtainedOccurrenceDateTime.dateTime")
    @Mapping(target = "speedValueMeasure", source = "vesselPositionEvent.speedValueMeasure.value")
    @Mapping(target = "courseValueMeasure", source = "vesselPositionEvent.courseValueMeasure.value")
    @Mapping(target = "latitude", source = "vesselPositionEvent.specifiedVesselGeographicalCoordinate.latitudeMeasure.value")
    @Mapping(target = "altitude", source = "vesselPositionEvent.specifiedVesselGeographicalCoordinate.altitudeMeasure.value")
    @Mapping(target = "longitude", source = "vesselPositionEvent.specifiedVesselGeographicalCoordinate.longitudeMeasure.value")
    @Mapping(target = "activityTypeCode", source = "vesselPositionEvent.activityTypeCode.value")
    @Mapping(target = "vesselTransportMeans", source = "vesselTransportMeansEntity")
    @Mapping(target = "geom", ignore = true)
    public abstract VesselPositionEventEntity mapToVesselPositionEventEntity(VesselPositionEvent vesselPositionEvent, VesselTransportMeansEntity vesselTransportMeansEntity);

}
