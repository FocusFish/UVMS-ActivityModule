/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.activity.service.mapper;

import org.locationtech.jts.geom.Geometry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import fish.focus.uvms.activity.fa.entities.ContactPartyEntity;
import fish.focus.uvms.activity.fa.entities.RegistrationEventEntity;
import fish.focus.uvms.activity.fa.entities.VesselIdentifierEntity;
import fish.focus.uvms.activity.fa.entities.VesselPositionEventEntity;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import fish.focus.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselPositionEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.inject.Inject;
import static fish.focus.uvms.activity.service.util.GeomUtil.createPoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "cdi", uses = {FaReportDocumentMapper.class, VesselIdentifierMapper.class, ContactPartyMapper.class, FlapDocumentMapper.class,VesselStorageCharacteristicsMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class VesselTransportMeansMapper extends BaseMapper {

    @Inject
    VesselPositionEventMapper vesselPositionEventMapper;

    @Inject
    ContactPartyMapper contactPartyMapper;

    @Inject
    RegistrationEventMapper registrationEventMapper;

    @Inject
    VesselTransportMeansMapper vesselTransportMeansMapperImpl;

    @Mapping(target = "roleCode", source = "roleCode.value")
    @Mapping(target = "roleCodeListId", source = "roleCode.listID")
    @Mapping(target = "name", expression = "java(BaseUtil.getTextFromList(vesselTransportMeans.getNames()))")
    @Mapping(target = "country", source = "registrationVesselCountry.ID.value")
    @Mapping(target = "countrySchemeId", source = "registrationVesselCountry.ID.schemeID")
    @Mapping(target = "vesselIdentifiers", expression = "java(mapToVesselIdentifierEntities(vesselTransportMeans.getIDS(), vesselTransportMeansEntity))")
    @Mapping(target = "contactParty", expression = "java(getContactPartyEntity(vesselTransportMeans.getSpecifiedContactParties(), vesselTransportMeansEntity))")
    @Mapping(target = "registrationEvent", expression = "java(getRegistrationEventEntity(vesselTransportMeans.getSpecifiedRegistrationEvents(), vesselTransportMeansEntity))")
    @Mapping(target = "vesselPositionEvents", expression = "java(getVesselPositionEventEntities(vesselTransportMeans.getSpecifiedVesselPositionEvents(),vesselTransportMeansEntity))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "fishingActivity", ignore = true)
    @Mapping(target = "faReportDocument", ignore = true)
    @Mapping(target = "flapDocuments", ignore = true)
    @Mapping(target = "vesselIdentifiersMap", ignore = true)
    public abstract VesselTransportMeansEntity mapToVesselTransportMeansEntity(VesselTransportMeans vesselTransportMeans);



    @Mapping(target = "vesselIdentifierId", source = "value")
    @Mapping(target = "vesselIdentifierSchemeId", source = "schemeID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vesselTransportMeans", ignore = true)
    protected abstract VesselIdentifierEntity mapToVesselIdentifierEntity(IDType idType);


    @Mapping(target = "contactPartyDetailsDTOSet", source = "contactParty")
    @Mapping(target = "storageDto", ignore = true)
    public abstract VesselDetailsDTO map(VesselTransportMeansEntity entity);

    protected Set<VesselPositionEventEntity> getVesselPositionEventEntities(List<VesselPositionEvent> specifiedVesselPositionEvents, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (specifiedVesselPositionEvents == null || specifiedVesselPositionEvents.isEmpty()) {
            return Collections.emptySet();
        }
        Set<VesselPositionEventEntity> vesselPositionEventEntities = new HashSet<>();
        for (VesselPositionEvent vesselPositionEvent : specifiedVesselPositionEvents) {
            VesselPositionEventEntity entity = vesselPositionEventMapper.mapToVesselPositionEventEntity(vesselPositionEvent,vesselTransportMeansEntity);
            Geometry point = createPoint(entity.getLongitude(), entity.getLatitude());
            entity.setGeom(point);
            vesselPositionEventEntities.add(entity);
        }
        return vesselPositionEventEntities;
    }

    protected Set<ContactPartyEntity> getContactPartyEntity(List<ContactParty> contactParties, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (contactParties == null || contactParties.isEmpty()) {
            return Collections.emptySet();
        }
        Set<ContactPartyEntity> contactPartyEntities = new HashSet<>();
        for (ContactParty contactParty : contactParties) {
            ContactPartyEntity contactPartyEntity = contactPartyMapper.mapToContactPartyEntity(contactParty,vesselTransportMeansEntity);
            contactPartyEntity.setVesselTransportMeans(vesselTransportMeansEntity);
            contactPartyEntities.add(contactPartyEntity);
        }
        return contactPartyEntities;
    }

    protected RegistrationEventEntity getRegistrationEventEntity(List<RegistrationEvent> registrationEvents, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (registrationEvents == null || registrationEvents.isEmpty()) {
            return null;
        }
        RegistrationEventEntity registrationEventEntity = registrationEventMapper.mapToRegistrationEventEntity(registrationEvents.get(0));
        registrationEventEntity.setVesselTransportMeanses(vesselTransportMeansEntity);
        return registrationEventEntity;
    }

    protected Set<VesselIdentifierEntity> mapToVesselIdentifierEntities(List<IDType> idTypes, VesselTransportMeansEntity vesselTransportMeansEntity) {
        if (idTypes == null || idTypes.isEmpty()) {
            return Collections.emptySet();
        }
        Set<VesselIdentifierEntity> vesselIdentifierEntities = new HashSet<>();
        for (IDType idType : idTypes) {
            VesselIdentifierEntity vesselIdentifierEntity = vesselTransportMeansMapperImpl.mapToVesselIdentifierEntity(idType);
            vesselIdentifierEntity.setVesselTransportMeans(vesselTransportMeansEntity);
            vesselIdentifierEntities.add(vesselIdentifierEntity);
        }
        return vesselIdentifierEntities;
    }

}
