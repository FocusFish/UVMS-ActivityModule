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

package fish.focus.uvms.activity.service.search.builder;

import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.activity.model.schemas.SearchFilter;
import fish.focus.uvms.activity.service.exception.ServiceException;
import fish.focus.uvms.activity.service.search.FilterDetails;
import fish.focus.uvms.activity.service.search.FilterMap;
import fish.focus.uvms.activity.service.search.FishingActivityQuery;
import fish.focus.uvms.activity.service.search.SortKey;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import static fish.focus.uvms.activity.service.util.GeomUtil.DEFAULT_EPSG_SRID;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class SearchQueryBuilder {
    protected static final Logger LOG = LoggerFactory.getLogger(SearchQueryBuilder.class);
    private static final String JOIN_FETCH = " JOIN FETCH ";
    static final String LEFT = " LEFT ";
    private static final String RIGHT = " RIGHT ";
    protected static final String JOIN = " JOIN ";

    private Map<SearchFilter, String> queryParameterMappings = FilterMap.getFilterQueryParameterMappings();
    private FilterMap filterMap = FilterMap.createFilterMap();

    // Assumption for the weight is, calculated_weight_measure is in Kg.
    // IF we get WEIGHT MEASURE as TON, we need to convert the input value to Kilograms.
    private static Double normalizeWeightValue(String value, String weightMeasure) {
        Double valueConverted = Double.parseDouble(value);
        if ("TNE".equals(weightMeasure)) {
            valueConverted = 1000D * valueConverted;
        }
        return valueConverted;
    }

    public FilterMap getFilterMap() {
        return filterMap;
    }

    void setFilterMap(FilterMap filterMap) {
        this.filterMap = filterMap;
    }

    /**
     * Create SQL dynamically based on Filter criteria
     *
     * @param query
     * @throws ServiceException
     */
    public abstract StringBuilder createSQL(FishingActivityQuery query) throws ServiceException;

    /**
     * Create Table Joins based on Filters provided by user. Avoid joining unnecessary tables
     *
     * @param sql
     * @param query
     */
    void createJoinTablesPartForQuery(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Create Join Tables part of Query");
        Map<SearchFilter, FilterDetails> filterMappings = filterMap.getFilterMappings();
        SortedSet<SearchFilter> keySet = new TreeSet<>();
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMap())) {
            keySet.addAll(query.getSearchCriteriaMap().keySet());
        }
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMapMultipleValues())) {
            keySet.addAll(query.getSearchCriteriaMapMultipleValues().keySet());
        }
        for (SearchFilter key : keySet) {
            FilterDetails details = filterMappings.get(key);
            String joinString = null;
            if (details != null) {
                joinString = details.getJoinString();
            }
            if (joinString == null || sql.indexOf(joinString) != -1) {// If the Table join for the Filter is already present in SQL, do not join the table again
                continue;
            }
            completeQueryDependingOnKey(sql, key, joinString);
        }
        getJoinPartForSortingOptions(sql, query);
    }

    private void completeQueryDependingOnKey(StringBuilder sql, SearchFilter key, String joinString) {
        switch (key) {
            case MASTER:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.CONTACT_PARTY_TABLE_ALIAS);
                appendJoinFetchString(sql, joinString);
                break;
            case VESSEL_IDENTIFIRE:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                appendJoinString(sql, joinString);
                break;
            case OWNER:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_PARTY_TABLE_ALIAS); // Add missing join for required table
                appendJoinFetchString(sql, joinString);
                break;
            case FROM:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_REP_MESSAGE_FROM_FA_REP);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.FLUX_PARTY_FOR_MESSAGE);
                appendJoinFetchString(sql, joinString);
                break;
            case AREAS: /* We need to do Right join here as one activity can have multiple areas and in the resultDTO we want to show all the areas for the activity*/
                appendRightJoinString(sql, joinString);
                break;
            case SPECIES: /* We need to do Right join here as one activity can have multiple catches and in the resultDTO we want to show all the species for the activity*/
                appendRightJoinString(sql, joinString);
                break;
            case PERIOD_END:
                appendLeftJoinFetchString(sql, joinString);
                break;
            case CONTACT_ROLE_CODE:
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.VESSEL_TRANSPORT_TABLE_ALIAS);
                appendJoinFetchIfConditionDoesntExist(sql, FilterMap.CONTACT_PARTY_TABLE_ALIAS);
                appendJoinFetchString(sql, joinString);
                break;
            default:
                appendJoinFetchString(sql, joinString);
                break;
        }
    }

    void appendJoinString(StringBuilder sql, String joinString) {
        sql.append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    protected void appendLeftJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(LEFT).append(JOIN_FETCH).append(joinString).append(StringUtils.SPACE);
    }

    private void appendRightJoinString(StringBuilder sql, String joinString) {
        sql.append(RIGHT).append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    protected void appendJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(JOIN_FETCH).append(joinString).append(StringUtils.SPACE);
    }

    /**
     * Add missing join for required table if doesn't already exist in the query;
     *
     * @param sql
     * @param valueToFindAndApply
     */
    protected void appendJoinFetchIfConditionDoesntExist(StringBuilder sql, String valueToFindAndApply) {
        if (sql.indexOf(valueToFindAndApply) == -1) { // Add missing join for required table
            sql.append(JOIN_FETCH).append(valueToFindAndApply);
        }
    }

    /**
     * This method makes sure that Table join is present for the Filter for which sorting has been requested.
     *
     * @param sql
     * @param query
     */
    private StringBuilder getJoinPartForSortingOptions(StringBuilder sql, FishingActivityQuery query) {
        SortKey sort = query.getSorting();
        // IF sorting has been requested and
        if (sort == null) {
            return sql;
        }
        SearchFilter field = sort.getSortBy();
        if (field == null) {
            return sql;
        }
        // Make sure that the field which we want to sort, table Join is present for it.
        if (SearchFilter.PERIOD_END.equals(field) && sql.indexOf(FilterMap.DELIMITED_PERIOD_TABLE_ALIAS) == -1) {
            appendLeftJoinFetch(sql);
        }
        return sql;
    }

    protected void appendLeftJoinFetch(StringBuilder sql) {
        sql.append(LEFT).append(JOIN_FETCH).append(FilterMap.DELIMITED_PERIOD_TABLE_ALIAS);
    }

    void createWherePartForQueryForFilters(StringBuilder sql, FishingActivityQuery query) {
        LOG.debug("Creating Where part of Query");
        sql.append(" WHERE ");
        Map<SearchFilter, FilterDetails> filterMappings = filterMap.getFilterMappings();
        SortedSet<SearchFilter> keySet = new TreeSet<>();
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMap())) {
            keySet.addAll(query.getSearchCriteriaMap().keySet());
        }
        if (MapUtils.isNotEmpty(query.getSearchCriteriaMapMultipleValues())) {
            keySet.addAll(query.getSearchCriteriaMapMultipleValues().keySet());
        }
        // Create Where part of SQL Query
        if(query.getShowOnlyLatest() != null){
            sql.append(" a.latest=:latest ").append(" AND ");
        }
        boolean hasAppendedAtLeastOne = false;
        for (SearchFilter key : keySet) {
            hasAppendedAtLeastOne |= appendWhereQueryPart(sql, filterMappings, keySet, hasAppendedAtLeastOne, key);
        }
        LOG.debug("Generated Query after WHERE : {}", sql);
    }

    private boolean appendWhereQueryPart(StringBuilder sql, Map<SearchFilter, FilterDetails> filterMappings, Set<SearchFilter> keySet, boolean hasAppendedAtLeastOne, SearchFilter key) {
        if ((SearchFilter.QUANTITY_MIN.equals(key) && keySet.contains(SearchFilter.QUANTITY_MAX)) || (filterMappings.get(key) == null)) { // skip this as MIN and MAX both are required to form where part. Treat it differently
            return false;
        }
        String mapping = filterMappings.get(key).getCondition();
        if (hasAppendedAtLeastOne) {
            sql.append(" AND ");
        }
        if (SearchFilter.QUANTITY_MIN.equals(key)) {
            sql.append("((faCatch.calculatedWeightMeasure >= :").append(FilterMap.QUANTITY_MIN).append(" OR aprod.calculatedWeightMeasure >= :")
                    .append(FilterMap.QUANTITY_MIN).append(" ))");

        } else if (SearchFilter.QUANTITY_MAX.equals(key)) {
            sql.append(" ( ");
            sql.append(filterMappings.get(SearchFilter.QUANTITY_MIN).getCondition()).append(" AND ").append(mapping);
            sql.append(" OR (aprod.calculatedWeightMeasure BETWEEN :").append(FilterMap.QUANTITY_MIN).append(" AND :").append(FilterMap.QUANTITY_MAX + ")");
            sql.append(" ) ");
        } else {
            sql.append(mapping);
        }
        return true;
    }

    /**
     * Build Where part of the query based on Filter criterias
     *
     * @param sql
     * @param query
     */
    public abstract void createWherePartForQuery(StringBuilder sql, FishingActivityQuery query);

    /**
     * Create sorting part for the Query
     *
     * @param sql
     * @param query
     * @throws ServiceException
     */
    void createSortPartForQuery(StringBuilder sql, FishingActivityQuery query) throws ServiceException {
        LOG.debug("Create Sorting part of Query");
        SortKey sort = query.getSorting();
        if (sort != null && sort.getSortBy() != null) {
            SearchFilter field = sort.getSortBy();
            if (SearchFilter.PERIOD_END.equals(field)) {
                getSqlForStartAndEndDateSorting(sql, field, query);
            }
            String orderby = " ASC ";
            if (sort.isReversed()) {
                orderby = " DESC ";
            }
            String sortFieldMapping = FilterMap.getFilterSortMappings().get(field);
            if (sortFieldMapping == null) {
                throw new ServiceException("Information about which database field to be used for sorting is unavailable");
            }
            sql.append(" ORDER BY ").append(sortFieldMapping).append(orderby);
        } else {
            sql.append(" ORDER BY fa.acceptedDatetime ASC ");
        }
    }

    /**
     * Special treatment for date sorting . In the resultset, One record can have multiple dates. But We need to consider only one date from the list. and then sort that selected date across resultset
     *
     * @param sql
     * @param filter
     * @param query
     */
    private void getSqlForStartAndEndDateSorting(StringBuilder sql, SearchFilter filter, FishingActivityQuery query) {
        Map<SearchFilter, String> searchCriteriaMap = query.getSearchCriteriaMap();
        if (searchCriteriaMap == null) {
            return;
        }
        sql.append(" AND(  ");
        sql.append(FilterMap.getFilterSortMappings().get(filter));
        sql.append(" =(SELECT MAX(").append(FilterMap.getFilterSortWhereMappings().get(filter)).append(") FROM a.delimitedPeriods dp1  ");
        sql.append(" ) ");
        sql.append(" OR dp IS null ) ");
    }

    public Query fillInValuesForQuery(FishingActivityQuery fishingActivityQuery, Query query) throws ServiceException {
        Map<SearchFilter, String> searchCriteriaMap = fishingActivityQuery.getSearchCriteriaMap();
        Map<SearchFilter, List<String>> searchForMultipleValues = fishingActivityQuery.getSearchCriteriaMapMultipleValues();
        if (MapUtils.isNotEmpty(searchCriteriaMap)) {
            applySingleValuesToQuery(searchCriteriaMap, query);
        }
        if (MapUtils.isNotEmpty(searchForMultipleValues)) {
            applyListValuesToQuery(searchForMultipleValues, query);
        }
        if (fishingActivityQuery.getShowOnlyLatest() != null) {
            query.setParameter("latest",fishingActivityQuery.getShowOnlyLatest());
        }
        return query;
    }

    private void applySingleValuesToQuery(Map<SearchFilter, String> searchCriteriaMap, Query typedQuery) throws ServiceException {
        // Assign values to created SQL Query
        for (Map.Entry<SearchFilter, String> entry : searchCriteriaMap.entrySet()) {
            SearchFilter key = entry.getKey();
            String value = entry.getValue();
            //For WeightMeasure there is no mapping present, In that case
            if (queryParameterMappings.get(key) == null) {
                continue;
            }
            if (StringUtils.isEmpty(value)) {
                throw new ServiceException("Value for filter " + key + " is null or empty");
            }
            applyValueDependingOnKey(searchCriteriaMap, typedQuery, key, value);
        }
    }

    private Instant parseToInstant(String value) {
        LocalDateTime localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_UI_FORMAT));
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime.toLocalDate(), localDateTime.toLocalTime(), ZoneId.of("UTC"));
        return zonedDateTime.toInstant();
    }

    private void applyValueDependingOnKey(Map<SearchFilter, String> searchCriteriaMap, Query typedQuery, SearchFilter key, String value) throws ServiceException {
        switch (key) {
            case PERIOD_START:
            case PERIOD_END:
                typedQuery.setParameter(queryParameterMappings.get(key), parseToInstant(value));
                break;
            case QUANTITY_MIN:
            case QUANTITY_MAX:
                typedQuery.setParameter(queryParameterMappings.get(key), SearchQueryBuilder.normalizeWeightValue(value, searchCriteriaMap.get(SearchFilter.WEIGHT_MEASURE)));
                break;
            case MASTER:
                typedQuery.setParameter(queryParameterMappings.get(key), value.toUpperCase());
                break;
            case FA_REPORT_ID:
                typedQuery.setParameter(queryParameterMappings.get(key), Integer.parseInt(value));
                break;
            case AREA_GEOM:
                Geometry geom;
                try {
                    geom =  new WKTReader().read(value);
                    geom.setSRID(DEFAULT_EPSG_SRID);
                } catch (ParseException e) {
                    throw new ServiceException(e.getMessage(), e);
                }
                typedQuery.setParameter(queryParameterMappings.get(key), geom);
                break;
            default:
                typedQuery.setParameter(queryParameterMappings.get(key), value);
                break;
        }
    }

    /**
     * Applies the values stored in the searchCriteriaMapMultipleValues map to the query
     *
     * @param searchCriteriaMap
     * @param typedQuery
     * @throws ServiceException
     */
    private void applyListValuesToQuery(Map<SearchFilter, List<String>> searchCriteriaMap, Query typedQuery) throws ServiceException {
        // Assign values to created SQL Query
        for (Map.Entry<SearchFilter, List<String>> entry : searchCriteriaMap.entrySet()) {

            SearchFilter key = entry.getKey();
            List<String> valueList = entry.getValue();
            //For WeightMeasure there is no mapping present, In that case
            if (queryParameterMappings.get(key) == null) {
                continue;
            }
            if (valueList == null || valueList.isEmpty()) {
                throw new ServiceException("valueList for filter " + key + " is null or empty");
            }
            if (key == SearchFilter.MASTER) {
                List<String> uppperCaseValList = new ArrayList<>();
                for (String val : valueList) {
                    uppperCaseValList.add(val.toUpperCase());
                }
                typedQuery.setParameter(queryParameterMappings.get(key), uppperCaseValList);
            } else {
                typedQuery.setParameter(queryParameterMappings.get(key), valueList);
            }
        }
    }
}
