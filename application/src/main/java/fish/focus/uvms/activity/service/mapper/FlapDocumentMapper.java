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

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import fish.focus.uvms.activity.fa.entities.FlapDocumentEntity;
import fish.focus.uvms.activity.service.dto.FlapDocumentDto;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLAPDocument;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", uses = BaseMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class FlapDocumentMapper {

    @Mapping(target = "flapDocumentId", source = "ID.value")
    @Mapping(target = "flapDocumentSchemeId", source = "ID.schemeID")
    @Mapping(target = "flapTypeCode", source = "typeCode.value")
    @Mapping(target = "flapTypeCodeListId", source = "typeCode.listID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fishingActivity", ignore = true)
    @Mapping(target = "vesselTransportMeans", ignore = true)
    @Mapping(target = "fluxCharacteristic", ignore = true)
    public abstract FlapDocumentEntity mapToFlapDocumentEntity(FLAPDocument flapDocument);

    @InheritInverseConfiguration
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "firstApplicationIndicator", ignore = true)
    @Mapping(target = "remarks", ignore = true)
    @Mapping(target = "decisionDateTime", ignore = true)
    @Mapping(target = "entryIntoForceDelimitedPeriod", ignore = true)
    @Mapping(target = "vesselIDs", ignore = true)
    @Mapping(target = "joinedVesselIDs", ignore = true)
    @Mapping(target = "specifiedFLUXCharacteristics", ignore = true)
    @Mapping(target = "specifiedVesselCrews", ignore = true)
    @Mapping(target = "applicableDelimitedPeriods", ignore = true)
    @Mapping(target = "specifiedVesselTransportCharters", ignore = true)
    @Mapping(target = "specifiedContactParties", ignore = true)
    @Mapping(target = "specifiedTargetedQuotas", ignore = true)
    @Mapping(target = "attachedFLUXBinaryFiles", ignore = true)
    @Mapping(target = "specifiedFLUXLocations", ignore = true)
    @Mapping(target = "relatedValidationResultDocuments", ignore = true)
    @Mapping(target = "relatedFLAPRequestDocuments", ignore = true)
    @Mapping(target = "specifiedAuthorizationStatuses", ignore = true)
    public abstract FLAPDocument mapToFlapDocument(FlapDocumentEntity flapDocument);

    public abstract List<FLAPDocument> mapToFlapDocumentList(Set<FlapDocumentEntity> flapDocument);


    @Mapping(target = "faIdentifierId", source = "flapDocumentId")
    @Mapping(target = "faIdentifierSchemeId", source = "flapDocumentSchemeId")
    public abstract FlapDocumentDto mapToFlapDocumentDto(FlapDocumentEntity entity);

    public abstract Set<FlapDocumentDto> mapToFlapDocumentDto(Set<FlapDocumentEntity> entity);

}
