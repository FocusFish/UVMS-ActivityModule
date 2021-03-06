/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.activity.fa.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.utils.FaReportStatusType;
import fish.focus.uvms.activity.model.schemas.ActivityIDType;
import fish.focus.uvms.activity.model.schemas.ActivityTableType;
import fish.focus.uvms.activity.service.util.Utils;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FaReportDocumentDao {

    private static final String REPORT_ID = "reportId";
    private static final String SCHEME_ID = "schemeId";
    private static final String REPORT_REF_ID = "reportRefId";
    private static final String SCHEME_REF_ID = "schemeRefId";
    private static final String TRIP_ID = "tripId";
    private static final String STATUSES = "statuses";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    private EntityManager em;

    public FaReportDocumentDao(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public List<FaReportDocumentEntity> getHistoryOfFaReport(FaReportDocumentEntity faReportEntity) {
        Set<FaReportDocumentEntity> result = new HashSet<>();
        populateHistoryOfFaReport(faReportEntity, result);

        return new ArrayList<>(result);
    }

    private void populateHistoryOfFaReport(FaReportDocumentEntity faReportEntity, Set<FaReportDocumentEntity> history) {
        history.add(faReportEntity);

        // Find reports that refer to this report
        List<FaReportDocumentEntity> reportsThatRefersToThisOne = findFaReportsThatReferTo(faReportEntity.getFluxReportDocument_Id(), faReportEntity.getFluxReportDocument_IdSchemeId());
        for (FaReportDocumentEntity reportThatRefersToThisOne : reportsThatRefersToThisOne) {
            if (!history.contains(reportThatRefersToThisOne)) {
                populateHistoryOfFaReport(reportThatRefersToThisOne, history);
            }
        }

        // Find report that this report refers to
        String referencedFaReportDocumentId = faReportEntity.getFluxReportDocument_ReferencedFaReportDocumentId();
        String referencedFaReportDocumentSchemeId = faReportEntity.getFluxReportDocument_ReferencedFaReportDocumentSchemeId();
        if (StringUtils.isNotEmpty(referencedFaReportDocumentId) && StringUtils.isNotEmpty(referencedFaReportDocumentSchemeId)) {
            FaReportDocumentEntity referredReport = findFaReportByIdAndScheme(referencedFaReportDocumentId, referencedFaReportDocumentSchemeId);
            if (referredReport != null && !history.contains(referredReport)) {
                populateHistoryOfFaReport(referredReport, history);
            }
        }
    }

    /**
     * Load FaReportDocument by report identifier
     *
     * @param reportId
     * @param schemeId
     * @return FaReportDocumentEntity
     * @throws ServiceException
     */
    public FaReportDocumentEntity findFaReportByIdAndScheme(String reportId, String schemeId) {
        TypedQuery<FaReportDocumentEntity> query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_ID_AND_SCHEME, FaReportDocumentEntity.class);
        query.setParameter(REPORT_ID, reportId);
        query.setParameter(SCHEME_ID, schemeId);

        // note: report ID and scheme ID column is under a uniqueness constraint, so no more than one result
        List<FaReportDocumentEntity> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }

        return resultList.get(0);
    }

    /**
     * Load FaReportDocuments by referenced report identifiers
     *
     * @param reportId
     * @param reportSchemeId
     * @return FaReportDocumentEntity
     */
    public List<FaReportDocumentEntity> findFaReportsThatReferTo(String reportId, String reportSchemeId) {
        TypedQuery<FaReportDocumentEntity> query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_REF_FA_ID_AND_SCHEME, FaReportDocumentEntity.class);
        query.setParameter(REPORT_REF_ID, reportId);
        query.setParameter(SCHEME_REF_ID, reportSchemeId);

        return query.getResultList();
    }

    public List<FaReportDocumentEntity> loadReports(String tripId, String consolidated) {
        return loadReports(tripId, consolidated, null, null);
    }

    public List<FaReportDocumentEntity> loadReports(String tripId, String consolidated, Instant startDate, Instant endDate) {
        Set<String> statuses = new HashSet<>();
        statuses.add(FaReportStatusType.NEW.name());
        if ("N".equals(consolidated) || consolidated == null) {
            statuses.add(FaReportStatusType.UPDATED.name());
            statuses.add(FaReportStatusType.CANCELED.name());
            statuses.add(FaReportStatusType.DELETED.name());
        }
        TypedQuery<FaReportDocumentEntity> query = getEntityManager().createNamedQuery(FaReportDocumentEntity.LOAD_REPORTS, FaReportDocumentEntity.class);
        query.setParameter(TRIP_ID, tripId);
        query.setParameter(STATUSES, statuses);

        query.setParameter(START_DATE, Instant.ofEpochSecond(-30_610_224_000L)); // The year 1000
        query.setParameter(END_DATE, Instant.ofEpochSecond(32_503_680_000L)); // The year 3000

        if (startDate != null) {
            query.setParameter(START_DATE, startDate);
        }
        if (endDate != null) {
            query.setParameter(END_DATE, endDate);
        }

        return query.getResultList();
    }

    public List<FaReportDocumentEntity> findReportsByIdsList(List<Integer> ids) {
        TypedQuery<FaReportDocumentEntity> query = getEntityManager().createNamedQuery(FaReportDocumentEntity.FIND_BY_FA_IDS_LIST, FaReportDocumentEntity.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    public List<FaReportDocumentEntity> loadCanceledAndDeletedReports(List<FaReportDocumentEntity> reports) {
        List<Integer> idsOfCancelledDeletedReports = new ArrayList<>();
        for (FaReportDocumentEntity report : reports) {
            populateDeletingAndCancellationIds(report.getFishingActivities(), idsOfCancelledDeletedReports);
        }

        if (CollectionUtils.isNotEmpty(idsOfCancelledDeletedReports)) {
            return findReportsByIdsList(idsOfCancelledDeletedReports);
        }
        return new ArrayList<>();
    }

    private void populateDeletingAndCancellationIds(Set<FishingActivityEntity> fishingActivities, List<Integer> idsOfCancelledDeletedReports) {
        for (FishingActivityEntity fishingActivity : Utils.safeIterable(fishingActivities)) {
            if (fishingActivity.getCanceledBy() != null) {
                idsOfCancelledDeletedReports.add(fishingActivity.getCanceledBy());
            }
            if (fishingActivity.getDeletedBy() != null) {
                idsOfCancelledDeletedReports.add(fishingActivity.getDeletedBy());
            }
        }
    }

    public List<FaReportDocumentEntity> getMatchingIdentifiers(List<ActivityIDType> ids, ActivityTableType tableType) {
        String namedQueryToSelect = tableType == ActivityTableType.FLUX_REPORT_DOCUMENT_ENTITY ? FaReportDocumentEntity.FIND_MATCHING_IDENTIFIER : FaReportDocumentEntity.FIND_RELATED_MATCHING_IDENTIFIER;
        List<FaReportDocumentEntity> resultList = new ArrayList<>();
        for (ActivityIDType idType : ids) {
            TypedQuery<FaReportDocumentEntity> query = getEntityManager().createNamedQuery(namedQueryToSelect, FaReportDocumentEntity.class);
            query.setParameter(REPORT_ID, idType.getValue());
            query.setParameter(SCHEME_ID, idType.getIdentifierSchemeId());
            resultList.addAll(query.getResultList());
        }
        resultList.removeAll(Collections.singleton(null));
        return resultList;
    }
}
