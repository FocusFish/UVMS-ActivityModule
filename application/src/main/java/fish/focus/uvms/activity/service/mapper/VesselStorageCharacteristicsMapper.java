/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.activity.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import fish.focus.uvms.activity.fa.entities.VesselStorageCharCodeEntity;
import fish.focus.uvms.activity.fa.entities.VesselStorageCharacteristicsEntity;
import fish.focus.uvms.activity.service.dto.StorageDto;
import fish.focus.uvms.activity.service.dto.VesselStorageCharCodeDto;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class VesselStorageCharacteristicsMapper {


    @Mapping(target = "vesselId", source = "vesselStorageCharacteristic.ID.value")
    @Mapping(target = "vesselSchemaId", source = "vesselStorageCharacteristic.ID.schemeID")
    @Mapping(target = "vesselStorageCharCode", expression = "java(mapToVesselStorageCharCodes(vesselStorageCharacteristic.getTypeCodes(), vesselStorageCharacteristicsEntity))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fishingActivitiesForDestVesselCharId", ignore = true)
    @Mapping(target = "fishingActivitiesForSourceVesselCharId", ignore = true)
    public abstract VesselStorageCharacteristicsEntity mapToDestVesselStorageCharEntity(VesselStorageCharacteristic vesselStorageCharacteristic);


    @Mapping(target = "vesselTypeCode", source = "value")
    @Mapping(target = "vesselTypeCodeListId", source = "listID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vesselStorageCharacteristics", ignore = true)
    protected abstract VesselStorageCharCodeEntity mapToVesselStorageCharCodeEntity(CodeType codeType);

    protected Set<VesselStorageCharCodeEntity> mapToVesselStorageCharCodes(List<CodeType> codeTypes, VesselStorageCharacteristicsEntity vesselStorageChar) {
        if(codeTypes == null || codeTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<VesselStorageCharCodeEntity> vesselStorageCharCodes = new HashSet<>();
        for (CodeType codeType : codeTypes) {
            VesselStorageCharCodeEntity vesselStorageCharCode = mapToVesselStorageCharCodeEntity(codeType);
            vesselStorageCharCode.setVesselStorageCharacteristics(vesselStorageChar);
            vesselStorageCharCodes.add(vesselStorageCharCode);
        }
        return vesselStorageCharCodes;
    }


    @Mapping(target = "identifier.faIdentifierId", source = "vesselId")
    @Mapping(target = "identifier.faIdentifierSchemeId", source = "vesselSchemaId")
    @Mapping(target = "vesselStorageCharCodeDto", source = "firstVesselStorageCharCode")
    @Mapping(target = "identifiers", ignore = true)
    public abstract StorageDto mapToStorageDto(VesselStorageCharacteristicsEntity entity);

    public abstract VesselStorageCharCodeDto mapToVesselStorageCharCodeDto(VesselStorageCharCodeEntity entity);
}
