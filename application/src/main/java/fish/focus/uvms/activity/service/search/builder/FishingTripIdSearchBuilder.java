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

import org.apache.commons.lang3.StringUtils;
import fish.focus.uvms.activity.service.search.FilterMap;
import fish.focus.uvms.activity.service.search.FishingActivityQuery;

public class FishingTripIdSearchBuilder extends SearchQueryBuilder {

    private static final String FISHING_TRIP_JOIN = "SELECT DISTINCT ft.fishingTripKey.tripId, ft.fishingTripKey.tripSchemeId from FishingTripEntity ft JOIN ft.fishingActivities a LEFT JOIN a.faReportDocument fa ";
    private static final String FISHING_TRIP_COUNT_JOIN = "SELECT COUNT(DISTINCT ft) from FishingTripEntity ft JOIN ft.fishingActivities a LEFT JOIN a.faReportDocument fa ";

    /**
     * For some usecases we need different database column mappings for same filters.
     * We can call method which sets these specific mappings for certain business requirements while calling constructor     *
     */
    public FishingTripIdSearchBuilder() {
        super();
        FilterMap filterMap = FilterMap.createFilterMap();
        filterMap.populateFilterMappingsForFilterFishingTripIds();
        setFilterMap(filterMap);
    }

    @Override
    public StringBuilder createSQL(FishingActivityQuery query) {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();
        sql.append(FISHING_TRIP_JOIN); // Common Join for all filters
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        LOG.debug("SQL : {}", sql);
        return sql;
    }

    public StringBuilder createCountSQL(FishingActivityQuery query) {
        LOG.debug("Start building SQL depending upon Filter Criterias");
        StringBuilder sql = new StringBuilder();
        sql.append(FISHING_TRIP_COUNT_JOIN); // Common Join for all filters
        createJoinTablesPartForQuery(sql, query); // Join only required tables based on filter criteria
        createWherePartForQuery(sql, query);  // Add Where part associated with Filters
        LOG.debug("SQL : {}", sql);
        return sql;
    }

    // Build Where part of the query based on Filter criteria
    @Override
    public void createWherePartForQuery(StringBuilder sql, FishingActivityQuery query) {
        createWherePartForQueryForFilters(sql, query);
    }

    @Override
    protected void appendJoinFetchIfConditionDoesntExist(StringBuilder sql, String valueToFindAndApply) {
        if (sql.indexOf(valueToFindAndApply) == -1) { // Add missing join for required table
            sql.append(JOIN).append(valueToFindAndApply);
        }
    }

    @Override
    protected  void appendJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(JOIN).append(joinString).append(StringUtils.SPACE);
    }

    @Override
    protected void appendLeftJoinFetchString(StringBuilder sql, String joinString) {
        sql.append(LEFT).append(JOIN).append(joinString).append(StringUtils.SPACE);
    }
}
