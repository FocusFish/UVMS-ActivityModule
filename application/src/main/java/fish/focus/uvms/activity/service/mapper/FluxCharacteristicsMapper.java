/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.activity.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import fish.focus.uvms.activity.fa.entities.FluxCharacteristicEntity;
import fish.focus.uvms.activity.service.dto.FluxCharacteristicsDto;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FluxCharacteristicsMapper extends BaseMapper {

    public static final FluxCharacteristicsMapper INSTANCE = Mappers.getMapper(FluxCharacteristicsMapper.class);


    @Mapping(target = "typeCode", source = "typeCode.value")
    @Mapping(target = "typeCodeListId", source = "typeCode.listID")
    @Mapping(target = "valueMeasure", source = "valueMeasure.value")
    @Mapping(target = "valueMeasureUnitCode", source = "valueMeasure.unitCode")
    @Mapping(target = "valueDateTime", source = "valueDateTime.dateTime")
    @Mapping(target = "valueIndicator", source = "valueIndicator.indicatorString.value")
    @Mapping(target = "valueCode", source = "valueCode.value")
    @Mapping(target = "valueText", expression = "java(BaseUtil.getTextFromList(fluxCharacteristic.getValues()))")
    @Mapping(target = "valueLanguageId", expression = "java(BaseUtil.getLanguageIdFromList(fluxCharacteristic.getValues()))")
    @Mapping(target = "valueQuantity", source = "valueQuantity.value")
    @Mapping(target = "valueQuantityCode", source = "valueQuantity.unitCode")
    @Mapping(target = "description", expression = "java(BaseUtil.getTextFromList(fluxCharacteristic.getDescriptions()))")
    @Mapping(target = "descriptionLanguageId", expression = "java(BaseUtil.getLanguageIdFromList(fluxCharacteristic.getDescriptions()))")
    @Mapping(target = "fluxLocation", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "calculatedValueMeasure", ignore = true)
    @Mapping(target = "calculatedValueQuantity", ignore = true)
    @Mapping(target = "faCatch", ignore = true)
    @Mapping(target = "fishingActivity", ignore = true)
    @Mapping(target = "flapDocument", ignore = true)
    public abstract FluxCharacteristicEntity mapToFluxCharEntity(FLUXCharacteristic fluxCharacteristic);

    @InheritInverseConfiguration
    @Mapping(target = "descriptions", ignore = true)
    @Mapping(target = "values", ignore = true)
    @Mapping(target = "specifiedFLUXLocations", ignore = true)
    @Mapping(target = "relatedFLAPDocuments", ignore = true)
    public abstract FLUXCharacteristic mapToFLUXCharacteristic(FluxCharacteristicEntity fluxCharacteristicEntity);
}
