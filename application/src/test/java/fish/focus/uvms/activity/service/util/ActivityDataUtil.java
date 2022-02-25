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

package fish.focus.uvms.activity.service.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fish.focus.uvms.activity.fa.entities.FaCatchEntity;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.entities.FishingTripEntity;
import fish.focus.uvms.activity.fa.entities.RegistrationEventEntity;
import fish.focus.uvms.activity.fa.entities.VesselIdentifierEntity;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import fish.focus.uvms.activity.fa.utils.FaReportStatusType;

public class ActivityDataUtil {

    public static void addFluxReportFieldsToFaReportDocumentEntity(FaReportDocumentEntity faReportDocumentEntity, String fluxDocumentID, String referencedFaReportDocumentId, Instant creationDateTime, String purposeCode, String purposeCodeListId, String purpose, String ownerFluxPartyId, String ownerFluxPartyName) {
        faReportDocumentEntity.setFluxReportDocument_Id(fluxDocumentID);
        faReportDocumentEntity.setFluxReportDocument_ReferencedFaReportDocumentId(referencedFaReportDocumentId);
        faReportDocumentEntity.setFluxReportDocument_CreationDatetime(creationDateTime);
        faReportDocumentEntity.setFluxReportDocument_PurposeCode(purposeCode);
        faReportDocumentEntity.setFluxReportDocument_PurposeCodeListId(purposeCodeListId);
        faReportDocumentEntity.setFluxReportDocument_Purpose(purpose);

        faReportDocumentEntity.setFluxParty_identifier(ownerFluxPartyId);
        faReportDocumentEntity.setFluxParty_schemeId("FLUX_GP_PARTY");
        faReportDocumentEntity.setFluxParty_name(ownerFluxPartyName);
    }

    public static VesselTransportMeansEntity getVesselTransportMeansEntity(String roleCode, String roleCodeListId, String name, RegistrationEventEntity registrationEventEntity) {
        VesselTransportMeansEntity vesselTransportMeansEntity = new VesselTransportMeansEntity();
        vesselTransportMeansEntity.setRoleCode(roleCode);
        vesselTransportMeansEntity.setRoleCodeListId(roleCodeListId);
        vesselTransportMeansEntity.setName(name);
        vesselTransportMeansEntity.setRegistrationEvent(registrationEventEntity);
        return vesselTransportMeansEntity;
    }

    public static FaReportDocumentEntity getFaReportDocumentEntity(String typeCode, String typeCodeListId, Instant acceptedDatetime, VesselTransportMeansEntity vesselTransportMeansEntity, FaReportStatusType status) {
        FaReportDocumentEntity faReportDocumentEntity = new FaReportDocumentEntity();
        faReportDocumentEntity.setTypeCode(typeCode);
        faReportDocumentEntity.setTypeCodeListId(typeCodeListId);
        faReportDocumentEntity.setAcceptedDatetime(acceptedDatetime);
        faReportDocumentEntity.setVesselTransportMeans(new HashSet<>(Arrays.asList(vesselTransportMeansEntity)));
        faReportDocumentEntity.setStatus(status.name());
        return faReportDocumentEntity;
    }

    public static FishingActivityEntity getFishingActivityEntity(String typeCode, String typeCodeListId, Instant occurence, String reasonCode, String reasonCodeListId, FaReportDocumentEntity faReportDocumentEntity, FishingActivityEntity relatedfishingActivityEntity) {
        FishingActivityEntity fishingActivityEntity = new FishingActivityEntity();
        fishingActivityEntity.setTypeCode(typeCode);
        fishingActivityEntity.setTypeCodeListid(typeCodeListId);
        fishingActivityEntity.setOccurence(occurence);
        fishingActivityEntity.setReasonCode(reasonCode);
        fishingActivityEntity.setReasonCodeListId(reasonCodeListId);
        fishingActivityEntity.setFaReportDocument(faReportDocumentEntity);
        fishingActivityEntity.setRelatedFishingActivity(relatedfishingActivityEntity);
        return fishingActivityEntity;
    }

    public static FaCatchEntity getFaCatchEntity(FishingActivityEntity fishingActivityEntity, String typeCode, String typeCodeListId, String speciesCode, String speciesCodeListid, Double unitQuantity, Double weightMeasure, String weightMeasureUnitCode,
                                                 String weighingMeansCode, String weighingMeansCodeListId) {
        FaCatchEntity faCatchEntity = new FaCatchEntity();
        faCatchEntity.setFishingActivity(fishingActivityEntity);
        faCatchEntity.setTypeCode(typeCode);
        faCatchEntity.setTypeCodeListId(typeCodeListId);
        faCatchEntity.setSpeciesCode(speciesCode);
        faCatchEntity.setSpeciesCodeListid(speciesCodeListid);
        faCatchEntity.setUnitQuantity(unitQuantity);
        faCatchEntity.setWeightMeasure(weightMeasure);
        faCatchEntity.setWeightMeasureUnitCode(weightMeasureUnitCode);
        faCatchEntity.setWeighingMeansCode(weighingMeansCode);
        faCatchEntity.setWeighingMeansCodeListId(weighingMeansCodeListId);
        return faCatchEntity;
    }

    public static FishingTripEntity getFishingTripEntity(String typeCode, String typeCodeListId, FaCatchEntity faCatchEntity, FishingActivityEntity fishingActivityEntity) {
        FishingTripEntity fishingTripEntity = new FishingTripEntity();
        fishingTripEntity.setTypeCode(typeCode);
        fishingTripEntity.setTypeCodeListId(typeCodeListId);
        fishingTripEntity.getCatchEntities().add(faCatchEntity);
        fishingTripEntity.getFishingActivities().add(fishingActivityEntity);
        return fishingTripEntity;
    }

    public static Set<VesselIdentifierEntity> getVesselIdentifiers(VesselTransportMeansEntity vesselTransportMeansEntity1, String ident_, String scheme_) {
        Set<VesselIdentifierEntity> identifiers = new HashSet<>();
        VesselIdentifierEntity identifier = new VesselIdentifierEntity();
        identifier.setVesselTransportMeans(vesselTransportMeansEntity1);
        identifier.setVesselIdentifierId(ident_);
        identifier.setVesselIdentifierSchemeId(scheme_);
        identifiers.add(identifier);
        return identifiers;
    }
}
