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

import fish.focus.uvms.activity.service.util.MapperUtil;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentRelatedFaReportEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import fish.focus.uvms.activity.fa.utils.FaReportSourceEnum;
import fish.focus.uvms.activity.fa.utils.FaReportStatusType;
import fish.focus.uvms.activity.service.FishingTripCache;
import fish.focus.uvms.activity.service.dto.fareport.FaReportCorrectionDTO;
import fish.focus.uvms.activity.service.dto.view.RelatedReportDto;
import fish.focus.uvms.activity.service.dto.view.ReportDocumentDto;
import fish.focus.uvms.activity.service.mapper.FaReportDocumentMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FaReportDocumentMapperTest {

    FaReportDocumentMapper faReportDocumentMapper = Mappers.getMapper(FaReportDocumentMapper.class);
    
    @Test
    public void mapToFaReportCorrection() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        FaReportCorrectionDTO faReportCorrectionDTO = faReportDocumentMapper.mapToFaReportCorrectionDto(faReportDocumentEntity);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_CreationDatetime().toEpochMilli(), faReportCorrectionDTO.getCreationDate().toEpochMilli());

        assertEquals(faReportDocumentEntity.getFluxReportDocument_Id(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_IdSchemeId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxParty_name(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }

    @Test
    public void mapToFaReportCorrectionList() {
        FaReportDocumentEntity faReportDocumentEntity = MapperUtil.getFaReportDocumentEntity();
        List<FaReportCorrectionDTO> faReportCorrectionDTOs = faReportDocumentMapper.mapToFaReportCorrectionDtoList(Arrays.asList(faReportDocumentEntity));
        FaReportCorrectionDTO faReportCorrectionDTO = faReportCorrectionDTOs.get(0);

        assertEquals(faReportDocumentEntity.getStatus(), faReportCorrectionDTO.getCorrectionType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_CreationDatetime().toEpochMilli(), faReportCorrectionDTO.getCreationDate().toEpochMilli());

        assertEquals(faReportDocumentEntity.getFluxReportDocument_Id(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getKey());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_IdSchemeId(), faReportCorrectionDTO.getFaReportIdentifiers().entrySet().iterator().next().getValue());
        assertEquals(faReportDocumentEntity.getFluxParty_name(), faReportCorrectionDTO.getOwnerFluxPartyName());
    }

    @Test
    public void faReportDocumentMapper() {
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentMapper.mapToFAReportDocumentEntity(faReportDocument, FaReportSourceEnum.FLUX);
        assertFaReportDocumentFields(faReportDocument, faReportDocumentEntity);
        assertNotNull(faReportDocumentEntity);
        assertNotNull(faReportDocumentEntity.getFluxReportDocument_Id());
    }

    @Test
    public void faReportDocumentMapperNullReturns(){
        Set<FishingActivityEntity> fishingActivityEntities = faReportDocumentMapper.mapFishingActivityEntities(null, new FaReportDocumentEntity(), null, new FishingTripCache());
        assertEquals(0, fishingActivityEntities.size());
        Set<VesselTransportMeansEntity> vesselTransportMeansEntityList = faReportDocumentMapper.mapVesselTransportMeansEntity(null, new FaReportDocumentEntity());
        assertNull(vesselTransportMeansEntityList);
        Set<FishingActivityEntity> fishingActivityEntities1 = faReportDocumentMapper.mapFishingActivityEntities(null, new FaReportDocumentEntity(), null, new FishingTripCache());
        assertEquals(0, fishingActivityEntities1.size());
    }

    @Test
    public void mapFaReportDocumentToReportDocumentDto() {
        // Given
        FAReportDocument faReportDocument = MapperUtil.getFaReportDocument();
        FaReportDocumentEntity faReportDocumentEntity = faReportDocumentMapper.mapToFAReportDocumentEntity(faReportDocument, FaReportSourceEnum.FLUX);

        // When
        ReportDocumentDto dto = faReportDocumentMapper.mapFaReportDocumentToReportDocumentDto(faReportDocumentEntity);

        // Then
        assertEquals(faReportDocumentEntity.getTypeCode(), dto.getType());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_PurposeCode(), dto.getPurposeCode());
        assertEquals(faReportDocumentEntity.getFluxReportDocument_ReferencedFaReportDocumentId(), dto.getReferencedFaReportDocumentId());
        assertEquals(faReportDocumentEntity.getFmcMarker(), dto.getFmcMark());

        assertEquals("2016-07-01T11:15:00", dto.getAcceptedDate());
        assertEquals("2016-07-01T11:15:00", dto.getCreationDate());

        FaReportDocumentRelatedFaReportEntity relatedFaReportIdentifier = faReportDocumentEntity.getRelatedFaReportIdentifiers().iterator().next();
        assertEquals(1, dto.getRelatedReports().size());
        RelatedReportDto relatedReport = dto.getRelatedReports().get(0);
        assertEquals(relatedFaReportIdentifier.getFaReportIdentifierId(), relatedReport.getId());
        assertEquals(relatedFaReportIdentifier.getFaReportIdentifierSchemeId(), relatedReport.getSchemeId());
    }

    private void assertFaReportDocumentFields(FAReportDocument faReportDocument, FaReportDocumentEntity faReportDocumentEntity) {
        assertEquals(faReportDocument.getTypeCode().getValue(), faReportDocumentEntity.getTypeCode());
        assertEquals(faReportDocument.getTypeCode().getListID(), faReportDocumentEntity.getTypeCodeListId());
        assertEquals(faReportDocument.getAcceptanceDateTime().getDateTime().toGregorianCalendar().toInstant(), faReportDocumentEntity.getAcceptedDatetime());
        assertEquals(faReportDocument.getFMCMarkerCode().getValue(), faReportDocumentEntity.getFmcMarker());
        assertEquals(faReportDocument.getFMCMarkerCode().getListID(), faReportDocumentEntity.getFmcMarkerListId());
        assertEquals(FaReportStatusType.NEW.name(), faReportDocumentEntity.getStatus());
        assertEquals(FaReportSourceEnum.FLUX.getSourceType(), faReportDocumentEntity.getSource());
    }
}
