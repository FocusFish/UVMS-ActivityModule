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

package fish.focus.uvms.activity.service.bean;

import fish.focus.uvms.asset.client.model.AssetDTO;
import fish.focus.uvms.asset.client.model.search.SearchBranch;
import fish.focus.uvms.asset.client.model.search.SearchFields;
import fish.focus.uvms.activity.fa.dao.FaCatchDao;
import fish.focus.uvms.activity.fa.dao.FaReportDocumentDao;
import fish.focus.uvms.activity.fa.dao.FishingActivityDao;
import fish.focus.uvms.activity.fa.dao.FishingTripDao;
import fish.focus.uvms.activity.fa.dao.VesselTransportMeansDao;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.entities.VesselStorageCharacteristicsEntity;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import fish.focus.uvms.activity.fa.utils.FaReportStatusType;
import fish.focus.uvms.activity.fa.utils.FishingActivityTypeEnum;
import fish.focus.uvms.activity.model.schemas.FishingActivitySummary;
import fish.focus.uvms.activity.model.schemas.FishingTripIdWithGeometry;
import fish.focus.uvms.activity.model.schemas.FishingTripResponse;
import fish.focus.uvms.activity.model.schemas.SearchFilter;
import fish.focus.uvms.activity.service.ActivityService;
import fish.focus.uvms.activity.service.AssetModuleService;
import fish.focus.uvms.activity.service.FishingTripService;
import fish.focus.uvms.activity.service.MdrModuleService;
import fish.focus.uvms.activity.service.dto.AssetIdentifierDto;
import fish.focus.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchSummaryListDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.FishingActivityTypeDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.FishingTripSummaryViewDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.MessageCountDTO;
import fish.focus.uvms.activity.service.dto.fishingtrip.ReportDTO;
import fish.focus.uvms.activity.service.dto.view.TripIdDto;
import fish.focus.uvms.activity.service.dto.view.TripOverviewDto;
import fish.focus.uvms.activity.service.dto.view.TripWidgetDto;
import fish.focus.uvms.activity.service.exception.ServiceException;
import fish.focus.uvms.activity.service.facatch.evolution.CatchEvolutionProgressProcessor;
import fish.focus.uvms.activity.service.facatch.evolution.TripCatchEvolutionProgressRegistry;
import fish.focus.uvms.activity.service.mapper.BaseUtil;
import fish.focus.uvms.activity.service.mapper.FaCatchMapper;
import fish.focus.uvms.activity.service.mapper.FishingActivityUtilsMapper;
import fish.focus.uvms.activity.service.mapper.FishingTripIdWithGeometryMapper;
import fish.focus.uvms.activity.service.mapper.VesselStorageCharacteristicsMapper;
import fish.focus.uvms.activity.service.mapper.VesselTransportMeansMapper;
import fish.focus.uvms.activity.service.search.FishingActivityQuery;
import fish.focus.uvms.activity.service.search.FishingTripId;
import fish.focus.uvms.activity.service.search.SortKey;
import fish.focus.uvms.activity.service.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.SerializationUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Stateless
@Local(FishingTripService.class)
@Transactional
@Slf4j
public class FishingTripServiceBean extends BaseActivityBean implements FishingTripService {

    private static final String DECLARATION = "Declaration";
    private static final String NOTIFICATION = "Notification";

    @Inject
    VesselTransportMeansMapper vesselTransportMeansMapper;

    @Inject
    VesselStorageCharacteristicsMapper vesselStorageCharacteristicsMapper;

    @Inject
    FishingActivityUtilsMapper fishingActivityUtilsMapper;

    @Inject
    FishingTripIdWithGeometryMapper fishingTripIdWithGeometryMapper;

    @Inject
    FaCatchMapper faCatchMapper;

    @EJB
    private ActivityService activityServiceBean;

    @EJB
    private AssetModuleService assetModuleService;

    @EJB
    private MdrModuleService mdrModuleService;

    private FaReportDocumentDao faReportDocumentDao;
    private FishingActivityDao fishingActivityDao;
    private VesselTransportMeansDao vesselTransportMeansDao;
    private FishingTripDao fishingTripDao;
    private FaCatchDao faCatchDao;

    private static final CatchEvolutionProgressProcessor catchEvolutionProgressProcessor =
            new CatchEvolutionProgressProcessor(new TripCatchEvolutionProgressRegistry());

    @PostConstruct
    public void init() {
        fishingActivityDao = new FishingActivityDao(entityManager);
        faReportDocumentDao = new FaReportDocumentDao(entityManager);
        faCatchDao = new FaCatchDao(entityManager);
        fishingTripDao = new FishingTripDao(entityManager);
        vesselTransportMeansDao = new VesselTransportMeansDao(entityManager);
    }

    @Override
    public VesselDetailsDTO getVesselDetailsForFishingTrip(final String fishingTripId) {
        if (fishingTripId == null) {
            throw new IllegalArgumentException("PARAMETER CANNOT BE NULL");
        }
        VesselTransportMeansEntity latestVesselByTripId = vesselTransportMeansDao.findLatestVesselByTripId(fishingTripId);
        if (latestVesselByTripId != null) {
            FishingActivityEntity parent = latestVesselByTripId.getFishingActivity();
            return getVesselDetailsDTO(latestVesselByTripId, parent);
        }

        return null;
    }

    private VesselDetailsDTO getVesselDetailsDTO(VesselTransportMeansEntity vesselTransportMeansEntity, FishingActivityEntity fishingActivityEntity) {
        VesselDetailsDTO detailsDTO;
        detailsDTO = vesselTransportMeansMapper.map(vesselTransportMeansEntity);

        getMdrCodesEnrichWithAssetsModuleDataIfNeeded(detailsDTO);

        if (fishingActivityEntity != null) {
            VesselStorageCharacteristicsEntity sourceVesselCharId = fishingActivityEntity.getSourceVesselCharId();
            if (detailsDTO != null) {
                detailsDTO.setStorageDto(vesselStorageCharacteristicsMapper.mapToStorageDto(sourceVesselCharId));
            }
        }
        return detailsDTO;
    }

    //To process MDR code list and compare with  database:vesselTransportMeansDao and then enrich with asset module
    private void getMdrCodesEnrichWithAssetsModuleDataIfNeeded(VesselDetailsDTO vesselDetailsDTO) {
        final String ACRONYM = "FLUX_VESSEL_ID_TYPE";
        final String filter = "*";
        final List<String> columnsList = new ArrayList<>();
        Integer nrOfResults = 1000;
        if (vesselDetailsDTO == null) {
            return;
        }
        List<String> codeList;
        codeList = mdrModuleService.getAcronymFromMdr(ACRONYM, filter, columnsList, nrOfResults, "code").get("code");
        Set<AssetIdentifierDto> vesselIdentifiers = vesselDetailsDTO.getVesselIdentifiers();
        if (vesselIdentifiers == null || codeList == null) {
            return;
        }

        SearchBranch query = new SearchBranch();
        query.setLogicalAnd(false);
        for (AssetIdentifierDto assetIdentifierDto : vesselIdentifiers) {
            if (codeList.contains(assetIdentifierDto.getIdentifierSchemeId().name())) {
                switch (assetIdentifierDto.getIdentifierSchemeId()) {
                    case CFR:
                        query.addNewSearchLeaf(SearchFields.CFR, assetIdentifierDto.getFaIdentifierId());
                        break;
                    case EXT_MARK:
                        query.addNewSearchLeaf(SearchFields.EXTERNAL_MARKING, assetIdentifierDto.getFaIdentifierId());
                        break;
                    case GFCM:
                        query.addNewSearchLeaf(SearchFields.GFCM, assetIdentifierDto.getFaIdentifierId());
                        break;
                    case ICCAT:
                        query.addNewSearchLeaf(SearchFields.ICCAT, assetIdentifierDto.getFaIdentifierId());
                        break;
                    case IRCS:
                        query.addNewSearchLeaf(SearchFields.IRCS, assetIdentifierDto.getFaIdentifierId());
                        break;
                    case UVI:
                        query.addNewSearchLeaf(SearchFields.UVI, assetIdentifierDto.getFaIdentifierId());
                        break;
                    default:
                }
            }
        }
        List<AssetDTO> assetList = assetModuleService.getAssets(query);
        if (!CollectionUtils.isEmpty(assetList)) {
            vesselDetailsDTO.enrichVesselIdentifiersFromAsset(assetList.get(0));
        }
    }

    @Override
    public FishingTripSummaryViewDTO getFishingTripSummaryAndReports(String fishingTripId) throws ServiceException {
        List<ReportDTO> reportDTOList = new ArrayList<>();
        Map<String, FishingActivityTypeDTO> summary = populateFishingActivityReportListAndFishingTripSummary(fishingTripId, reportDTOList, false);
        return populateFishingTripSummary(fishingTripId, reportDTOList, summary);
    }

    /**
     * Populates and return a FishingTripSummaryViewDTO with the inputParameters values.
     *
     * @param fishingTripId
     * @param reportDTOList
     * @param summary
     * @return fishingTripSummaryViewDTO
     */
    private FishingTripSummaryViewDTO populateFishingTripSummary(String fishingTripId, List<ReportDTO> reportDTOList, Map<String, FishingActivityTypeDTO> summary) {
        FishingTripSummaryViewDTO fishingTripSummaryViewDTO = new FishingTripSummaryViewDTO();
        fishingTripSummaryViewDTO.setActivityReports(reportDTOList);
        fishingTripSummaryViewDTO.setSummary(summary);
        fishingTripSummaryViewDTO.setFishingTripId(fishingTripId);
        return fishingTripSummaryViewDTO;
    }

    /**
     * @param fishingTripId     - Fishing trip summary will be collected for this tripID
     * @param reportDTOList     - This DTO will have details about Fishing Activities.Method will process and populate data into this list     *
     * @param isOnlyTripSummary - This method you could reuse to only get Fishing trip summary as well
     * @throws ServiceException
     */
    private Map<String, FishingActivityTypeDTO> populateFishingActivityReportListAndFishingTripSummary(String fishingTripId, List<ReportDTO> reportDTOList, boolean isOnlyTripSummary) throws ServiceException {
        List<FishingActivityEntity> fishingActivityList = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId);
        fishingActivityList.addAll(getReportsThatWereCancelledOrDeleted(fishingActivityList));
        Map<String, FishingActivityTypeDTO> tripSummary = new HashMap<>();
        for (FishingActivityEntity activityEntity : Utils.safeIterable(fishingActivityList)) {
            if (!isOnlyTripSummary) {
                ReportDTO reportDTO = fishingActivityUtilsMapper.mapToReportDTO(activityEntity);
                reportDTOList.add(reportDTO);
            }
            if (activityEntity != null && activityEntity.getFaReportDocument() != null && DECLARATION.equalsIgnoreCase(activityEntity.getFaReportDocument().getTypeCode())) {
                // FA Report should be of type Declaration. And Fishing Activity type should be Either Departure,Arrival or Landing
                populateFishingTripSummary(activityEntity, tripSummary);
            }
        }
        return tripSummary;
    }

    private List<FishingActivityEntity> getReportsThatWereCancelledOrDeleted(List<FishingActivityEntity> fishingActivityList) {
        Map<FaReportDocumentEntity, FishingActivityEntity> responseMap = new HashMap<>();
        List<Integer> cancelledByDeletedByList = new ArrayList<>();
        List<FishingActivityEntity> cancelledOrDeletedActivities = new ArrayList<>();
        if (CollectionUtils.isEmpty(fishingActivityList)) {
            return new ArrayList<>();
        }
        for (FishingActivityEntity fishingActivityEntity : fishingActivityList) {
            if (fishingActivityEntity.getCanceledBy() != null) {
                cancelledByDeletedByList.add(fishingActivityEntity.getCanceledBy());
                cancelledOrDeletedActivities.add(fishingActivityEntity);
            }
            if (fishingActivityEntity.getDeletedBy() != null) {
                cancelledByDeletedByList.add(fishingActivityEntity.getDeletedBy());
                cancelledOrDeletedActivities.add(fishingActivityEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(cancelledByDeletedByList)) {
            List<FaReportDocumentEntity> reportsByIdsList = faReportDocumentDao.findReportsByIdsList(cancelledByDeletedByList);
            if (CollectionUtils.isNotEmpty(reportsByIdsList)) {
                mapFishActToFaReportCounterpart(responseMap, cancelledOrDeletedActivities, reportsByIdsList);
            }
        }
        return mapToFishingActivitiesList(responseMap);
    }

    private List<FishingActivityEntity> mapToFishingActivitiesList(Map<FaReportDocumentEntity, FishingActivityEntity> fishActFaRepMap) {
        List<FishingActivityEntity> fishingActivityEntities = new ArrayList<>();
        if (MapUtils.isEmpty(fishActFaRepMap)) {
            return fishingActivityEntities;
        }
        for (Map.Entry<FaReportDocumentEntity, FishingActivityEntity> entry : fishActFaRepMap.entrySet()) {
            FaReportDocumentEntity faRep = entry.getKey();
            FishingActivityEntity fishAct = entry.getValue();
            FishingActivityEntity cloneActivity = SerializationUtils.clone(fishAct);
            cloneActivity.setFaReportDocument(faRep);
            cloneActivity.setFishingGears(fishAct.getFishingGears());
            cloneActivity.setFaCatchs(fishAct.getFaCatchs());
            cloneActivity.setAllRelatedFishingActivities(fishAct.getAllRelatedFishingActivities());
            cloneActivity.setFisheryTypeCodeListId(fishAct.getFisheryTypeCodeListId());
            cloneActivity.setFluxCharacteristics(fishAct.getFluxCharacteristics());
            fishingActivityEntities.add(cloneActivity);
        }
        return fishingActivityEntities;
    }

    private void mapFishActToFaReportCounterpart(Map<FaReportDocumentEntity, FishingActivityEntity> responseMap, List<FishingActivityEntity> cancelledOrDeletedActivities, List<FaReportDocumentEntity> reportsByIdsList) {
        for (FishingActivityEntity fishingActivityEntity : cancelledOrDeletedActivities) {
            Integer canceledBy = fishingActivityEntity.getCanceledBy();
            Integer deletedBy = fishingActivityEntity.getDeletedBy();
            for (FaReportDocumentEntity faReportDocumentEntity : reportsByIdsList) {
                if (Objects.equals(canceledBy, faReportDocumentEntity.getId()) ||
                    Objects.equals(deletedBy, faReportDocumentEntity.getId())) {
                    responseMap.put(faReportDocumentEntity, fishingActivityEntity);
                }
            }
        }
    }

    private void populateFishingTripSummary(FishingActivityEntity activityEntity, Map<String, FishingActivityTypeDTO> summary) {
        String activityTypeCode = activityEntity.getTypeCode();
        if (FishingActivityTypeEnum.DEPARTURE.toString().equalsIgnoreCase(activityTypeCode)
                || FishingActivityTypeEnum.ARRIVAL.toString().equalsIgnoreCase(activityTypeCode)
                || FishingActivityTypeEnum.LANDING.toString().equalsIgnoreCase(activityTypeCode)) {
            Instant occurrence = activityEntity.getOccurence();
            boolean isCorrection = BaseUtil.getCorrection(activityEntity);
            FishingActivityTypeDTO fishingActivityTypeDTO = summary.get(activityTypeCode);
            if (occurrence != null && (
                    (fishingActivityTypeDTO == null)
                            || (isCorrection
                            && fishingActivityTypeDTO.getDate() != null
                            && occurrence.compareTo(fishingActivityTypeDTO.getDate().toInstant()) > 0))) {
                fishingActivityTypeDTO = new FishingActivityTypeDTO();
                fishingActivityTypeDTO.setDate(Date.from(occurrence));
                summary.put(activityTypeCode, fishingActivityTypeDTO);
            }
        }
    }

    /**
     * Gets the counters for the trip with this tripId.
     *
     * @param tripId
     */
    @Override
    public MessageCountDTO getMessageCountersForTripId(String tripId) {
        List<FaReportDocumentEntity> reports = faReportDocumentDao.loadReports(tripId, "N");
        if (CollectionUtils.isNotEmpty(reports)) {
            List<FaReportDocumentEntity> canceledDeletedReports = faReportDocumentDao.loadCanceledAndDeletedReports(reports);
            if (CollectionUtils.isNotEmpty(canceledDeletedReports)) {
                reports.addAll(canceledDeletedReports);
            }
        }
        return createMessageCounter(reports);
    }

    /**
     * Populates the MessageCounter adding to the right counter 1 unit depending on the type of report (typeCode, purposeCode, size()).
     *
     * @param faReportDocumentList
     */
    private MessageCountDTO createMessageCounter(List<FaReportDocumentEntity> faReportDocumentList) {

        MessageCountDTO messagesCounter = new MessageCountDTO();
        if (CollectionUtils.isEmpty(faReportDocumentList)) {
            return messagesCounter;
        }

        // Reports total
        messagesCounter.setNoOfReports(faReportDocumentList.size());

        for (FaReportDocumentEntity faReport : faReportDocumentList) {
            String faDocumentType = faReport.getTypeCode();
            FaReportStatusType status = FaReportStatusType.valueOf(faReport.getStatus());

            // Declarations / Notifications
            if (DECLARATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfDeclarations(messagesCounter.getNoOfDeclarations() + 1);
            } else if (NOTIFICATION.equalsIgnoreCase(faDocumentType)) {
                messagesCounter.setNoOfNotifications(messagesCounter.getNoOfNotifications() + 1);
            }

            // Fishing operations
            Set<FishingActivityEntity> faEntitiyList = faReport.getFishingActivities();
            for (FishingActivityEntity faEntity : Utils.safeIterable(faEntitiyList)) {
                if (FishingActivityTypeEnum.FISHING_OPERATION.toString().equalsIgnoreCase(faEntity.getTypeCode())) {
                    messagesCounter.setNoOfFishingOperations(messagesCounter.getNoOfFishingOperations() + 1);
                }
            }

            // PurposeCode : Deletions / Cancellations / Corrections
            if (FaReportStatusType.DELETED.equals(status)) {
                messagesCounter.setNoOfDeletions(messagesCounter.getNoOfDeletions() + 1);
            } else if (FaReportStatusType.CANCELED.equals(status)) {
                messagesCounter.setNoOfCancellations(messagesCounter.getNoOfCancellations() + 1);
            } else if (FaReportStatusType.UPDATED.equals(status)) {
                messagesCounter.setNoOfCorrections(messagesCounter.getNoOfCorrections() + 1);
            }
        }
        return messagesCounter;
    }

    /**
     * Retrieves all the catches for the given fishing trip;
     *
     * @param fishingTripId
     */
    @Override
    public Map<String, CatchSummaryListDTO> retrieveFaCatchesForFishingTrip(String fishingTripId) {
        return faCatchMapper.mapCatchesToSummaryDTO(faCatchDao.findFaCatchesByFishingTrip(fishingTripId));
    }

    /**
     * This method filters fishing Trips for Activity tab
     */
    @Override
    public FishingTripResponse filterFishingTrips(FishingActivityQuery query) throws ServiceException {
        log.info("getFishingTripResponse For Filter");
        if ((MapUtils.isEmpty(query.getSearchCriteriaMap()) && MapUtils.isEmpty(query.getSearchCriteriaMapMultipleValues()))
                || activityServiceBean.checkAndEnrichIfVesselFiltersArePresent(query)) {
            return new FishingTripResponse();
        }
        Set<FishingTripId> fishingTripIds = fishingTripDao.getFishingTripIdsForMatchingFilterCriteria(query);
        Integer totalCountOfRecords = fishingTripDao.getCountOfFishingTripsForMatchingFilterCriteria(query);
        log.debug("Total count of records: {} ", totalCountOfRecords);
        FishingTripResponse fishingTripResponse = buildFishingTripSearchRespose(fishingTripIds);
        fishingTripResponse.setTotalCountOfRecords(BigInteger.valueOf(totalCountOfRecords));
        return fishingTripResponse;
    }


    /**
     * This method builds FishingTripSerachReponse objectc for FishingTripIds passed to the method
     * collectFishingActivities : If the value is TRUE, all fishing Activities for every fishing Trip would be sent in the response.
     * If the value is FALSE, No fishing activities would be sent in the response.
     */
    protected FishingTripResponse buildFishingTripSearchRespose(Set<FishingTripId> fishingTripIds) throws ServiceException {
        if (fishingTripIds == null || fishingTripIds.isEmpty()) {
            return new FishingTripResponse();
        }
        List<FishingActivitySummary> fishingActivitySummaries = new ArrayList<>();

        List<FishingTripIdWithGeometry> fishingTripIdLists = new ArrayList<>();
        for (FishingTripId fishingTripId : fishingTripIds) {

            FishingActivityQuery query = new FishingActivityQuery();
            Map<SearchFilter, String> searchCriteriaMap = new EnumMap<>(SearchFilter.class);
            searchCriteriaMap.put(SearchFilter.TRIP_ID, fishingTripId.getTripId());
            searchCriteriaMap.put(SearchFilter.FISHING_TRIP_SCHEME_ID, fishingTripId.getSchemeID());
            query.setSearchCriteriaMap(searchCriteriaMap);
            SortKey sortKey = new SortKey();
            sortKey.setSortBy(SearchFilter.PERIOD_START); // this is important to find out first and last fishing activity for the Fishing Trip
            sortKey.setReversed(false);
            query.setSorting(sortKey);
            List<FishingActivityEntity> fishingActivityEntityList = fishingActivityDao.getFishingActivityListByQuery(query);

            FishingTripIdWithGeometry fishingTripIdWithGeometry = fishingTripIdWithGeometryMapper.mapToFishingTripIdWithDetails(fishingTripId, fishingActivityEntityList);
            fishingTripIdLists.add(fishingTripIdWithGeometry);
        }

        // populate response object
        FishingTripResponse response = new FishingTripResponse();
        response.setFishingActivityLists(fishingActivitySummaries);
        response.setFishingTripIdLists(fishingTripIdLists);
        return response;
    }

    private TripWidgetDto getTripWidgetDto(FishingActivityEntity activityEntity, String tripId) {
        if (activityEntity == null) {
            return null;
        }
        TripWidgetDto tripWidgetDto = new TripWidgetDto();
        try {
            if (tripId != null) {
                log.debug("Trip Id found for Fishing Activity. Get TripWidget information for tripID :" + tripId);
                TripOverviewDto tripOverviewDto = getTripOverviewDto(activityEntity, tripId);
                List<TripOverviewDto> tripOverviewDtoList = new ArrayList<>();
                tripOverviewDtoList.add(tripOverviewDto);
                tripWidgetDto.setTrips(tripOverviewDtoList);
                if (activityEntity.getFaReportDocument() != null && CollectionUtils.isNotEmpty(activityEntity.getFaReportDocument().getVesselTransportMeans())) {
                    Set<VesselTransportMeansEntity> vesselTransportMeansEntities = activityEntity.getFaReportDocument().getVesselTransportMeans();
                    for (VesselTransportMeansEntity vesselTransportMeansEntity : vesselTransportMeansEntities) {
                        if (vesselTransportMeansEntity.getFishingActivity() == null) {
                            tripWidgetDto.setVesselDetails(getVesselDetailsDTO(vesselTransportMeansEntity, activityEntity));
                            break;
                        }
                    }
                }
                log.debug("tripWidgetDto set for tripID :" + tripId);
            } else {
                log.debug("TripId is not received for the screen. Try to get TripSummary information for all the tripIds specified for FishingActivity:" + activityEntity.getId());
                return createTripWidgetDtoWithFishingActivity(activityEntity);
            }
        } catch (ServiceException e) {
            log.error("Error while creating TripWidgetDto.", e);
        }
        return tripWidgetDto;
    }

    @Override
    public CatchEvolutionDTO retrieveCatchEvolutionForFishingTrip(String fishingTripId) throws ServiceException {
        CatchEvolutionDTO catchEvolution = new CatchEvolutionDTO();
        List<FishingActivityEntity> fishingActivities = fishingActivityDao.getFishingActivityListForFishingTrip(fishingTripId);
        FishingActivityEntity activityEntity = fishingActivities.isEmpty() ? null : fishingActivities.get(0);
        catchEvolution.setTripDetails(getTripWidgetDto(activityEntity, fishingTripId));
        catchEvolution.setCatchEvolutionProgress(prepareCatchEvolutionProgress(fishingActivities));

        return catchEvolution;
    }

    private List<CatchEvolutionProgressDTO> prepareCatchEvolutionProgress(List<FishingActivityEntity> fishingActivities) {
        List<CatchEvolutionProgressDTO> catchEvolutionProgress = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(fishingActivities)) {
            catchEvolutionProgress = catchEvolutionProgressProcessor.process(fishingActivities);
        }

        return catchEvolutionProgress;
    }

    private TripWidgetDto createTripWidgetDtoWithFishingActivity(FishingActivityEntity activityEntity) throws ServiceException {
        TripWidgetDto tripWidgetDto = new TripWidgetDto();
        List<TripOverviewDto> tripOverviewDtoList = new ArrayList<>();
        tripOverviewDtoList.add(getTripOverviewDto(activityEntity, activityEntity.getFishingTrip().getFishingTripKey().getTripId()));
        tripWidgetDto.setTrips(tripOverviewDtoList);
        //As per new requirement, vessel should always be the one associated with fishing Activity in the trip widget
        if (activityEntity.getFaReportDocument() != null && CollectionUtils.isNotEmpty(activityEntity.getFaReportDocument().getVesselTransportMeans())) {
            Set<VesselTransportMeansEntity> vesselTransportMeansEntities = activityEntity.getFaReportDocument().getVesselTransportMeans();
            for (VesselTransportMeansEntity vesselTransportMeansEntity : vesselTransportMeansEntities) {
                if (vesselTransportMeansEntity.getFishingActivity() == null) {
                    tripWidgetDto.setVesselDetails(getVesselDetailsDTO(vesselTransportMeansEntity, activityEntity));
                    break;
                }
            }

        }
        return tripWidgetDto;
    }

    private TripOverviewDto getTripOverviewDto(FishingActivityEntity activityEntity, String tripId) throws ServiceException {
        Map<String, FishingActivityTypeDTO> typeDTOMap = populateFishingActivityReportListAndFishingTripSummary(tripId, null, true);
        TripOverviewDto tripOverviewDto = new TripOverviewDto();
        TripIdDto tripIdDto = new TripIdDto();
        tripIdDto.setId(tripId);
        tripIdDto.setSchemeId(activityEntity.getFishingTrip().getFishingTripKey().getTripSchemeId());
        List<TripIdDto> tripIdList = new ArrayList<>();
        tripIdList.add(tripIdDto);
        tripOverviewDto.setTripId(tripIdList);
        tripOverviewDto.setTypeCode(activityEntity.getFishingTrip().getTypeCode());
        populateTripOverviewDto(typeDTOMap, tripOverviewDto);
        return tripOverviewDto;
    }

    private void populateTripOverviewDto(Map<String, FishingActivityTypeDTO> typeDTOMap, TripOverviewDto tripOverviewDto) {
        for (Map.Entry<String, FishingActivityTypeDTO> entry : typeDTOMap.entrySet()) {
            String key = entry.getKey();
            FishingActivityTypeDTO fishingActivityTypeDTO = entry.getValue();
            switch (FishingActivityTypeEnum.valueOf(key)) {
                case DEPARTURE:
                    tripOverviewDto.setDepartureTime(fishingActivityTypeDTO.getDate());
                    break;
                case ARRIVAL:
                    tripOverviewDto.setArrivalTime(fishingActivityTypeDTO.getDate());
                    break;
                case LANDING:
                    tripOverviewDto.setLandingTime(fishingActivityTypeDTO.getDate());
                    break;
                default:
                    log.debug("Fishing Activity type found is :" + key);
                    break;
            }
        }
    }
}
