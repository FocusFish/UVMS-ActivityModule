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

package fish.focus.uvms.activity.service.search;

import org.apache.commons.lang3.StringUtils;
import fish.focus.uvms.activity.model.schemas.GroupCriteria;
import fish.focus.uvms.activity.model.schemas.SearchFilter;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * Filter Fishing Activity Reports functionality require Query to be generated Dynamically based on Filters provided.
 * This class provides mapping information to Query Builder.
 * </p>
 */
public class FilterMap {

    public static final String LEFT_JOIN = " LEFT JOIN ";

    private static final String OWNER_ID = "ownerId";
    private static final String FROM_ID = "fromId";
    private static final String OCCURRENCE_START_DATE = "startDate";
    private static final String OCCURRENCE_END_DATE = "endDate";
    private static final String VESSEL_IDENTITY_NAME = "vtName";
    private static final String VESSEL_IDENTIFIER = "vtSchemeId";
    private static final String VTM_GUIDS = "guids";
    private static final String PURPOSE_CODE = "purposeCode";
    private static final String STATUS_LIST = "statusList";
    private static final String REPORT_TYPE_CODE = "faReportTypeCode";
    private static final String ACTIVITY_TYPE_CODE = "activityTypecode";
    private static final String AREA_ID = "fluxAreaId";
    private static final String PORT_ID = "fluxPortId";
    private static final String FISHING_GEAR = "fishingGearType";
    private static final String SPECIES_CODE = "speciesCode";
    public static final String QUANTITY_MIN = "minWeight";
    public static final String QUANTITY_MAX = "maxWeight";
    private static final String CONTACT_PERSON_NAME = "agent";
    private static final String TRIP_ID = "tripId";
    private static final String TRIP_SCHEME_ID = "tripSchemeId";
    private static final String CONTACT_ROLE_CODE = "roleCode";
    public static final String VESSEL_TRANSPORT_TABLE_ALIAS = "fa.vesselTransportMeans vt";
    public static final String CONTACT_PARTY_TABLE_ALIAS = "vt.contactParty cparty";
    private static final String FA_CATCH_TABLE_ALIAS = " a.faCatchs faCatch ";
    public static final String FA_REPORT_DOC_TABLE_ALIAS = " fa ";
    public static final String FLUX_PARTY_TABLE_ALIAS = " fa.fluxReportDocument_FluxParty fp  ";
    private static final String GEAR_TYPE_TABLE_ALIAS = " a.fishingGears fg ";
    private static final String FISHING_TRIP_TABLE_ALIAS = " a.fishingTrip fishingTrip ";
    public static final String AAP_PROCESS_TABLE_ALIAS = " faCatch.aapProcesses aprocess ";
    public static final String AAP_PRODUCT_TABLE_ALIAS = " aprocess.aapProducts aprod ";
    private static final String DATASOURCE = "dataSource";
    private static final String FA_REPORT_ID = "faReportId";
    private static final String AREA_GEOM = "areaGeom";
    private static final String FA_CATCH_TERRITORY = "faCatch.territory";
    private static final String FA_CATCH_FAO_AREA = "faCatch.faoArea";
    private static final String FA_CATCH_ICES_STAT_RECTANGLE = "faCatch.icesStatRectangle";
    private static final String FA_CATCH_EFFORT_ZONE = "faCatch.effortZone";
    private static final String FA_CATCH_RMFO = "faCatch.rfmo";
    private static final String FA_CATCH_GFCM_GSA = "faCatch.gfcmGsa";
    private static final String FA_CATCH_GFCM_STAT_RECTANGLE = "faCatch.gfcmStatRectangle";
    public static final String FLUX_REP_MESSAGE_FROM_FA_REP = "fa.fluxFaReportMessage fluxMsg ";
    public static final String FLUX_PARTY_FOR_MESSAGE = "fluxRepDoc.fluxParty fpFrom ";
    // For Sort criteria, which expression should be used
    private static EnumMap<SearchFilter, String> filterSortMappings = new EnumMap<>(SearchFilter.class);
    // Query parameter mapping
    private static EnumMap<SearchFilter, String> filterQueryParameterMappings = new EnumMap<>(SearchFilter.class);
    // Special case for star and end date sorting
    private static EnumMap<SearchFilter, String> filterSortWhereMappings = new EnumMap<>(SearchFilter.class);
    // List of filters which support multiple values
    private static Set<SearchFilter> filtersWhichSupportMultipleValues = new HashSet<>();
    private static EnumMap<GroupCriteria, GroupCriteriaMapper> groupByMapping = new EnumMap<>(GroupCriteria.class);

    static {
        populateFiltersWhichSupportMultipleValues();
        populateFilterQueryParameterMappings();
        populateFilterSortMappings();
        populateFilterSortWhereMappings();
        populateGroupByMapping();
    }

    public static final String DELIMITED_PERIOD_TABLE_ALIAS = " a.delimitedPeriods dp ";
    // This contains Table Join and Where condition mapping for each Filter
    private EnumMap<SearchFilter, FilterDetails> filterMappings = new EnumMap<>(SearchFilter.class);

    private FilterMap() {
        super();
        populateFilterMappings();
    }

    public static FilterMap createFilterMap() {
        return new FilterMap();
    }

    /**
     * For Sort by start date and End date, it needs special treatment. We need to use subQuery to make sure We pick up
     * only first Start or End date from the list of dates.
     * Below method helps that special case.
     */
    private static void populateFilterSortWhereMappings() {
        filterSortWhereMappings.put(SearchFilter.PERIOD_START, "dp1.startDate");
        filterSortWhereMappings.put(SearchFilter.PERIOD_END, "dp1.endDate");
    }

    /**
     * Below method provides mapping which should be used in order by clause.
     * This will achieve sorting for the criteria.
     */
    private static void populateFilterSortMappings() {
        filterSortMappings.put(SearchFilter.PERIOD_START, "a.calculatedStartTime");
        filterSortMappings.put(SearchFilter.PERIOD_END, "dp.endDate");
        filterSortMappings.put(SearchFilter.REPORT_TYPE, "fa.typeCode");
        filterSortMappings.put(SearchFilter.SOURCE, "fa.source");
        filterSortMappings.put(SearchFilter.ACTIVITY_TYPE, "a.typeCode");
        filterSortMappings.put(SearchFilter.OCCURRENCE, "a.occurence");
        filterSortMappings.put(SearchFilter.PURPOSE, "flux.purposeCode");
        filterSortMappings.put(SearchFilter.FA_STATUS, "fa.status");
    }

    /**
     * To put values in Query, Query Builder needs to know name used in query to be mapped to value.
     * Put that mapping here
     */
    private static void populateFilterQueryParameterMappings() {
        filterQueryParameterMappings.put(SearchFilter.SOURCE, DATASOURCE);
        filterQueryParameterMappings.put(SearchFilter.VESSEL_GUIDS, VTM_GUIDS);
        filterQueryParameterMappings.put(SearchFilter.OWNER, OWNER_ID);
        filterQueryParameterMappings.put(SearchFilter.FROM, FROM_ID);
        filterQueryParameterMappings.put(SearchFilter.PERIOD_START, OCCURRENCE_START_DATE);
        filterQueryParameterMappings.put(SearchFilter.PERIOD_END, OCCURRENCE_END_DATE);
        filterQueryParameterMappings.put(SearchFilter.VESSEL_NAME, VESSEL_IDENTITY_NAME);
        filterQueryParameterMappings.put(SearchFilter.VESSEL_IDENTIFIRE, VESSEL_IDENTIFIER);
        filterQueryParameterMappings.put(SearchFilter.PURPOSE, PURPOSE_CODE);
        filterQueryParameterMappings.put(SearchFilter.FA_STATUS, STATUS_LIST);
        filterQueryParameterMappings.put(SearchFilter.REPORT_TYPE, REPORT_TYPE_CODE);
        filterQueryParameterMappings.put(SearchFilter.ACTIVITY_TYPE, ACTIVITY_TYPE_CODE);
        filterQueryParameterMappings.put(SearchFilter.AREAS, AREA_ID);
        filterQueryParameterMappings.put(SearchFilter.PORT, PORT_ID);
        filterQueryParameterMappings.put(SearchFilter.GEAR, FISHING_GEAR);
        filterQueryParameterMappings.put(SearchFilter.SPECIES, SPECIES_CODE);
        filterQueryParameterMappings.put(SearchFilter.QUANTITY_MIN, QUANTITY_MIN);
        filterQueryParameterMappings.put(SearchFilter.QUANTITY_MAX, QUANTITY_MAX);
        filterQueryParameterMappings.put(SearchFilter.MASTER, CONTACT_PERSON_NAME);
        filterQueryParameterMappings.put(SearchFilter.FA_REPORT_ID, FA_REPORT_ID);
        filterQueryParameterMappings.put(SearchFilter.AREA_GEOM, AREA_GEOM);
        filterQueryParameterMappings.put(SearchFilter.TRIP_ID, TRIP_ID);
        filterQueryParameterMappings.put(SearchFilter.CONTACT_ROLE_CODE, CONTACT_ROLE_CODE);
        filterQueryParameterMappings.put(SearchFilter.FISHING_TRIP_SCHEME_ID, TRIP_SCHEME_ID);
    }

    private static void populateFiltersWhichSupportMultipleValues() {
        filtersWhichSupportMultipleValues.add(SearchFilter.VESSEL_NAME);
        filtersWhichSupportMultipleValues.add(SearchFilter.VESSEL_IDENTIFIRE);
        filtersWhichSupportMultipleValues.add(SearchFilter.REPORT_TYPE);
        filtersWhichSupportMultipleValues.add(SearchFilter.ACTIVITY_TYPE);
        filtersWhichSupportMultipleValues.add(SearchFilter.PORT);
        filtersWhichSupportMultipleValues.add(SearchFilter.GEAR);
        filtersWhichSupportMultipleValues.add(SearchFilter.SPECIES);
        filtersWhichSupportMultipleValues.add(SearchFilter.MASTER);
        filtersWhichSupportMultipleValues.add(SearchFilter.PURPOSE);
        filtersWhichSupportMultipleValues.add(SearchFilter.CONTACT_ROLE_CODE);
    }

    public static void populateGroupByMapping() {

        groupByMapping.put(GroupCriteria.DATE_DAY, new GroupCriteriaMapper(" ", "a.calculatedStartTime", "setDay")); // set method belongs to class FaCatchSummaryCustomEntity
        groupByMapping.put(GroupCriteria.DATE_MONTH, new GroupCriteriaMapper(" ", "a.calculatedStartTime", "setMonth")); // set method belongs to class FaCatchSummaryCustomEntity
        groupByMapping.put(GroupCriteria.DATE_YEAR, new GroupCriteriaMapper(" ", "a.calculatedStartTime", "setYear")); // set method belongs to class FaCatchSummaryCustomEntity
        groupByMapping.put(GroupCriteria.DATE, new GroupCriteriaMapper(" ", "a.calculatedStartTime", "setDate")); // set method belongs to class FaCatchSummaryCustomEntity
        groupByMapping.put(GroupCriteria.VESSEL, new GroupCriteriaMapper(" ", "a.vesselTransportGuid", "setVesselTransportGuid"));
        groupByMapping.put(GroupCriteria.SIZE_CLASS, new GroupCriteriaMapper(" ", "faCatch.fishClassCode", "setFishClass"));
        groupByMapping.put(GroupCriteria.FLAG_STATE, new GroupCriteriaMapper(" ", "a.flagState", "setFlagState"));
        groupByMapping.put(GroupCriteria.GEAR_TYPE, new GroupCriteriaMapper(" ", "faCatch.gearTypeCode", "setGearType"));
        groupByMapping.put(GroupCriteria.PRESENTATION, new GroupCriteriaMapper(" ", "faCatch.presentation", "setPresentation"));
        groupByMapping.put(GroupCriteria.SPECIES, new GroupCriteriaMapper(" ", "faCatch.speciesCode", "setSpecies"));
        groupByMapping.put(GroupCriteria.CATCH_TYPE, new GroupCriteriaMapper(" ", "faCatch.typeCode", "setTypeCode"));
        groupByMapping.put(GroupCriteria.TERRITORY, new GroupCriteriaMapper(" ", FA_CATCH_TERRITORY, "setTerritory"));
        groupByMapping.put(GroupCriteria.FAO_AREA, new GroupCriteriaMapper(" ", FA_CATCH_FAO_AREA, "setFaoArea"));
        groupByMapping.put(GroupCriteria.ICES_STAT_RECTANGLE, new GroupCriteriaMapper(" ", FA_CATCH_ICES_STAT_RECTANGLE, "setIcesStatRectangle"));
        groupByMapping.put(GroupCriteria.EFFORT_ZONE, new GroupCriteriaMapper(" ", FA_CATCH_EFFORT_ZONE, "setEffortZone"));
        groupByMapping.put(GroupCriteria.RFMO, new GroupCriteriaMapper(" ", FA_CATCH_RMFO, "setRfmo"));
        groupByMapping.put(GroupCriteria.GFCM_GSA, new GroupCriteriaMapper(" ", FA_CATCH_GFCM_GSA, "setGfcmGsa"));
        groupByMapping.put(GroupCriteria.GFCM_STAT_RECTANGLE, new GroupCriteriaMapper(" ", FA_CATCH_GFCM_STAT_RECTANGLE, "setGfcmStatRectangle"));

    }

    public static Map<SearchFilter, String> getFilterSortMappings() {
        return filterSortMappings;
    }

    public static Map<SearchFilter, String> getFilterSortWhereMappings() {
        return filterSortWhereMappings;
    }

    public static Map<SearchFilter, String> getFilterQueryParameterMappings() {
        return filterQueryParameterMappings;
    }

    public static Set<SearchFilter> getFiltersWhichSupportMultipleValues() {
        return filtersWhichSupportMultipleValues;
    }

    public static Map<GroupCriteria, GroupCriteriaMapper> getGroupByMapping() {
        return groupByMapping;
    }

    /**
     * Below method stores mapping for each Filter criteria. Mapping will provide information on table joins
     * required for the criteria and Where conditions which needs to be applied for the criteria
     */
    public void populateFilterMappings() {
        filterMappings.put(SearchFilter.SOURCE, new FilterDetails(StringUtils.SPACE, "fa.source =:" + DATASOURCE));
        filterMappings.put(SearchFilter.OWNER, new FilterDetails(" fp.fluxPartyIdentifiers fpi", "fpi.fluxPartyIdentifierId =:" + OWNER_ID + StringUtils.SPACE));
        filterMappings.put(SearchFilter.FROM, new FilterDetails(" fpFrom.fluxPartyIdentifiers fpiFrom", "fpiFrom.fluxPartyIdentifierId =:" + FROM_ID + StringUtils.SPACE));
        // filterMappings.put(SearchFilter.PERIOD_START, new FilterDetails(DELIMITED_PERIOD_TABLE_ALIAS, "( dp.startDate >= :" + OCCURENCE_START_DATE + "  OR a.occurence  >= :" + OCCURENCE_START_DATE + " )"));
        filterMappings.put(SearchFilter.PERIOD_START, new FilterDetails(" ", "   a.calculatedStartTime  >= :" + OCCURRENCE_START_DATE + " "));
        filterMappings.put(SearchFilter.PERIOD_END, new FilterDetails(DELIMITED_PERIOD_TABLE_ALIAS, " (dp.endDate <= :" + OCCURRENCE_END_DATE +" OR  a.calculatedStartTime <= :"+ OCCURRENCE_END_DATE +")") );
        filterMappings.put(SearchFilter.VESSEL_NAME, new FilterDetails(VESSEL_TRANSPORT_TABLE_ALIAS, "vt.name IN (:" + VESSEL_IDENTITY_NAME + ")"));
        filterMappings.put(SearchFilter.VESSEL_IDENTIFIRE, new FilterDetails("vt.vesselIdentifiers vi", "vi.vesselIdentifierId IN (:" + VESSEL_IDENTIFIER + ")"));
        filterMappings.put(SearchFilter.VESSEL_GUIDS, new FilterDetails("fa.vesselTransportMeans vtMeans", "vtMeans.guid IN (:" + VTM_GUIDS + ")"));
        filterMappings.put(SearchFilter.PURPOSE, new FilterDetails(" ", "fa.fluxReportDocument_PurposeCode IN (:" + PURPOSE_CODE + ")"));
        filterMappings.put(SearchFilter.FA_STATUS, new FilterDetails(FA_REPORT_DOC_TABLE_ALIAS, "fa.status IN (:" + STATUS_LIST + ")"));
        filterMappings.put(SearchFilter.REPORT_TYPE, new FilterDetails(StringUtils.SPACE, "fa.typeCode IN (:" + REPORT_TYPE_CODE + ")"));
        filterMappings.put(SearchFilter.ACTIVITY_TYPE, new FilterDetails(StringUtils.SPACE, "a.typeCode IN (:" + ACTIVITY_TYPE_CODE + ")"));
        filterMappings.put(SearchFilter.AREAS, new FilterDetails("a.locations fluxLoc", "( fluxLoc.typeCode IN ('AREA') and fluxLoc.fluxLocationIdentifier =:" + AREA_ID + " )"));
        filterMappings.put(SearchFilter.PORT, new FilterDetails("a.locations fluxLoc", " (fluxLoc.typeCode IN ('LOCATION') and fluxLoc.fluxLocationIdentifier =:" + PORT_ID  + " )"));
        filterMappings.put(SearchFilter.GEAR, new FilterDetails(GEAR_TYPE_TABLE_ALIAS, "fg.typeCode IN (:" + FISHING_GEAR + ")"));
        filterMappings.put(SearchFilter.SPECIES, new FilterDetails(FA_CATCH_TABLE_ALIAS + LEFT_JOIN + AAP_PROCESS_TABLE_ALIAS + LEFT_JOIN + AAP_PRODUCT_TABLE_ALIAS, "( faCatch.speciesCode IN (:" + SPECIES_CODE + ") " + " OR aprod.speciesCode IN (:" + SPECIES_CODE + "))"));
        filterMappings.put(SearchFilter.QUANTITY_MIN, new FilterDetails(FA_CATCH_TABLE_ALIAS + LEFT_JOIN + " FETCH " + AAP_PROCESS_TABLE_ALIAS + LEFT_JOIN + " FETCH " + AAP_PRODUCT_TABLE_ALIAS, " (faCatch.calculatedWeightMeasure  BETWEEN :" + QUANTITY_MIN));
        filterMappings.put(SearchFilter.QUANTITY_MAX, new FilterDetails(" ", "  :" + QUANTITY_MAX + ") "));
        filterMappings.put(SearchFilter.MASTER, new FilterDetails(" cparty.contactPerson cPerson", "(UPPER(cPerson.title) IN (:" + CONTACT_PERSON_NAME + ") " + " or " +
                "UPPER(cPerson.givenName) IN (:" + CONTACT_PERSON_NAME + ") " + " or UPPER(cPerson.middleName) IN (:" + CONTACT_PERSON_NAME + ") " + " or UPPER(cPerson.familyName) IN (:" + CONTACT_PERSON_NAME + ") " + StringUtils.SPACE +
                "or UPPER(cPerson.familyNamePrefix) IN (:" + CONTACT_PERSON_NAME + ") " + " or UPPER(cPerson.nameSuffix) IN (:" + CONTACT_PERSON_NAME + ") " + " or UPPER(cPerson.alias) IN (:" + CONTACT_PERSON_NAME + ") " + ")"));
        filterMappings.put(SearchFilter.FA_REPORT_ID, new FilterDetails(StringUtils.SPACE, "fa.id =:" + FA_REPORT_ID));
        filterMappings.put(SearchFilter.AREA_GEOM, new FilterDetails(StringUtils.SPACE, "intersects(fa.geom, :" + AREA_GEOM + ") = true "));
        filterMappings.put(SearchFilter.TRIP_ID, new FilterDetails(StringUtils.SPACE + FISHING_TRIP_TABLE_ALIAS, "fishingTrip.fishingTripKey.tripId =:" + TRIP_ID + StringUtils.SPACE));
        filterMappings.put(SearchFilter.FISHING_TRIP_SCHEME_ID, new FilterDetails(StringUtils.SPACE + FISHING_TRIP_TABLE_ALIAS, "fishingTrip.fishingTripKey.tripSchemeId =:" + TRIP_SCHEME_ID + StringUtils.SPACE));
    }

    public void populateFilterMappingsForFilterFishingTripIds() {

        filterMappings.put(SearchFilter.CONTACT_ROLE_CODE, new FilterDetails(" cparty.contactPartyRole cRole ",
                "cRole.roleCode IN (:" + CONTACT_ROLE_CODE + ") "));

        filterMappings.put(SearchFilter.QUANTITY_MIN, new FilterDetails(FA_CATCH_TABLE_ALIAS + LEFT_JOIN + AAP_PROCESS_TABLE_ALIAS + LEFT_JOIN + AAP_PRODUCT_TABLE_ALIAS, " (faCatch.calculatedWeightMeasure  BETWEEN :" + QUANTITY_MIN));

        filterMappings.put(SearchFilter.TRIP_ID, new FilterDetails(StringUtils.SPACE + FISHING_TRIP_TABLE_ALIAS, "fishingTrip.fishingTripKey.tripId =:" + TRIP_ID + StringUtils.SPACE));
        filterMappings.put(SearchFilter.FISHING_TRIP_SCHEME_ID, new FilterDetails(StringUtils.SPACE + FISHING_TRIP_TABLE_ALIAS, "fishingTrip.fishingTripKey.tripSchemeId =:" + TRIP_SCHEME_ID + StringUtils.SPACE));
    }

    public Map<SearchFilter, FilterDetails> getFilterMappings() {
        return filterMappings;
    }

}
