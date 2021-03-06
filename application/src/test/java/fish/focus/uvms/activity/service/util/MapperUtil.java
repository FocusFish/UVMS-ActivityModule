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

package fish.focus.uvms.activity.service.util;

import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProcess;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPStock;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.DelimitedPeriod;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FACatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLAPDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXGeographicalCoordinate;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXParty;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingGear;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingTrip;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.GearProblem;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationEvent;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.RegistrationLocation;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SalesBatch;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SalesPrice;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.SizeDistribution;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.StructuredAddress;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselCountry;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselStorageCharacteristic;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.VesselTransportMeans;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.AmountType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.DateTimeType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IndicatorType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.MeasureType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.NumericType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.QuantityType;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.TextType;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import fish.focus.uvms.activity.fa.entities.FaCatchEntity;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentRelatedFaReportEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.entities.FishingTripEntity;
import fish.focus.uvms.activity.fa.entities.FishingTripKey;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import fish.focus.uvms.activity.fa.utils.FaReportStatusType;
import fish.focus.uvms.activity.service.dto.config.ActivityConfigDTO;
import fish.focus.uvms.activity.service.dto.config.FishingActivityConfigDTO;
import fish.focus.uvms.activity.service.dto.config.SummaryReportDTO;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

public class MapperUtil {

    public static ActivityConfigDTO getSourceActivityConfigDTO() {
        ActivityConfigDTO activityConfigDTO = new ActivityConfigDTO();
        FishingActivityConfigDTO fishingActivityConfig = new FishingActivityConfigDTO();
        SummaryReportDTO summaryReport = new SummaryReportDTO();
        summaryReport.setValues(Arrays.asList("Report1", "Report2", "Report3"));
        summaryReport.setOrder(Arrays.asList("Report1", "Report2", "Report3"));
        fishingActivityConfig.setSummaryReport(summaryReport);
        activityConfigDTO.setFishingActivityConfig(fishingActivityConfig);
        return activityConfigDTO;
    }

    public static ActivityConfigDTO getTargetActivityConfigDTO() {
        ActivityConfigDTO activityConfigDTO = new ActivityConfigDTO();
        FishingActivityConfigDTO fishingActivityConfig = new FishingActivityConfigDTO();
        SummaryReportDTO summaryReport = new SummaryReportDTO();
        summaryReport.setValues(Arrays.asList("Report1", "Report2", "Report3", "Report3", "Report4"));
        summaryReport.setOrder(Arrays.asList("Report1", "Report2", "Report3", "Report3", "Report4"));
        fishingActivityConfig.setSummaryReport(summaryReport);
        activityConfigDTO.setFishingActivityConfig(fishingActivityConfig);
        return activityConfigDTO;
    }


    public static FishingActivityEntity getFishingActivityEntity() {
          return new FishingActivityEntity();
    }

    public static FishingTripEntity getFishingTripEntity() {
        VesselTransportMeansEntity vesselTransportMeansEntity1 = ActivityDataUtil.getVesselTransportMeansEntity("PAIR_FISHING_PARTNER", "FA_VESSEL_ROLE", "vesselGroup1", null);

        vesselTransportMeansEntity1.setVesselIdentifiers(ActivityDataUtil.getVesselIdentifiers(vesselTransportMeansEntity1, "IDENT_1", "CFR"));

        FaReportDocumentEntity faReportDocumentEntity1 = ActivityDataUtil.getFaReportDocumentEntity(
                "Declaration" ,
                "FLUX_FA_REPORT_TYPE",
                parseDate("2016-06-27 07:47:31"),
                vesselTransportMeansEntity1,
                FaReportStatusType.NEW);

        ActivityDataUtil.addFluxReportFieldsToFaReportDocumentEntity(
                faReportDocumentEntity1,
                "FLUX_REPORT_DOCUMENT1",
                null,
                parseDate("2016-06-27 07:47:31"),
                "PURPOSE",
                "PURPOSE_CODE_LIST",
                null,
                "OWNER_FLUX_ID1",
                "flux1"
        );

        FishingActivityEntity fishingActivityEntity1 = ActivityDataUtil.getFishingActivityEntity("DEPARTURE", "FLUX_FA_TYPE" , parseDate("2014-05-27 07:47:31"), "FISHING", "FIS",faReportDocumentEntity1,null);

        FaCatchEntity faCatchEntity = ActivityDataUtil.getFaCatchEntity(
                fishingActivityEntity1,
                "DEPARTURE",
                "FA_CATCH_TYPE",
                "beagle2",
                "FAO_SPECIES",
                11112D,
                11112.0D,
                "FLUX_UNIT",
                "BFT",
                "WEIGHT_MEANS");

        FishingTripEntity entity = ActivityDataUtil.getFishingTripEntity("JFO", "EU_TRIP_ID", faCatchEntity, fishingActivityEntity1);

        entity.setFishingTripKey(new FishingTripKey("NOR-TRP-20160517234053706", "EU_TRIP_ID"));
        entity.setCalculatedTripEndDate(Instant.parse("2016-01-12T00:00:00Z"));
        entity.setCalculatedTripStartDate(Instant.parse("2013-01-12T00:00:00Z"));
        return entity;
    }

    public static Instant parseDate(String dateStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    public static FaReportDocumentEntity getFaReportDocumentEntity() {
        FaReportDocumentEntity faReportDocumentEntity = new FaReportDocumentEntity();
        faReportDocumentEntity.setStatus(FaReportStatusType.UPDATED.name());
        faReportDocumentEntity.setTypeCode("FISHING_OPERATION");
        faReportDocumentEntity.setTypeCodeListId("FLUX_FA_REPORT_TYPE");
        faReportDocumentEntity.setAcceptedDatetime(Instant.now());
        faReportDocumentEntity.setFmcMarker("FMC Marker");
        faReportDocumentEntity.setFmcMarkerListId("FMC Marker list Id");

        FaReportDocumentRelatedFaReportEntity faReportDocumentRelatedFaReportEntity = new FaReportDocumentRelatedFaReportEntity();
        faReportDocumentRelatedFaReportEntity.setFaReportIdentifierId("Identifier Id 1");
        faReportDocumentRelatedFaReportEntity.setFaReportIdentifierSchemeId("57th785-tjf845-tjfui5-tjfuir8");
        faReportDocumentRelatedFaReportEntity.setFaReportDocument(faReportDocumentEntity);
        faReportDocumentEntity.setRelatedFaReportIdentifiers(new HashSet<>(Collections.singletonList(faReportDocumentRelatedFaReportEntity)));

        faReportDocumentEntity.setFluxReportDocument_Id("Report Id 1");
        faReportDocumentEntity.setFluxReportDocument_IdSchemeId("Scheme Id 1");

        faReportDocumentEntity.setFluxParty_identifier("Flux party Id 1");
        faReportDocumentEntity.setFluxParty_schemeId("Flux Scheme Id 1");
        faReportDocumentEntity.setFluxParty_name("Flux party Name 1");
        faReportDocumentEntity.setFluxParty_nameLanguageId("Flux party name language id 1");

        faReportDocumentEntity.setFluxReportDocument_Purpose("Test purpose");
        faReportDocumentEntity.setFluxReportDocument_PurposeCode("5");
        faReportDocumentEntity.setFluxReportDocument_PurposeCodeListId("57thf-58fj88-4d9834-thdue");
        faReportDocumentEntity.setFluxReportDocument_ReferencedFaReportDocumentId("Ref Id 1");

        faReportDocumentEntity.setFluxReportDocument_CreationDatetime(Instant.now());
        return faReportDocumentEntity;
    }

    public static AAPProcess getAapProcess() {
        List<CodeType> codeList = Collections.singletonList(getCodeType("FISH_FRESHNESS", "FLUX_PROCESS_TYPE"));
        NumericType numericType = getNumericType(123);
        return new AAPProcess(codeList, numericType, null, Collections.singletonList(getAapProduct()));
    }

    public static AAPProduct getAapProduct() {
        CodeType speciesCode = getCodeType("Species 1", "qbdcg-3fhr5-rd4kd5-er5tgd5k");
        QuantityType quantityType = getQuantityType(123);
        MeasureType measureType = getMeasureType(123, "C62", "qbdcg-3fhr5-rd4kd5-er5tgd5k");
        CodeType weighingMeansCode = getCodeType("Weighing Means 1", "qbd43cg-3fhr5t65-rd4kd5rt4-er5tgd5k");
        CodeType usageCode = getCodeType("Usage Code 1", "qbd43cg-3fhr5t65-rd4kd5rt4-er5tgd5k");
        QuantityType packagingUnitQuantity = getQuantityType(1234);
        CodeType packagingTypeCode = getCodeType("packaging type 1", "FISH_PACKAGING");
        MeasureType packagingUnitAverageWeightMeasure = getMeasureType(123, "C62", "qbdcg-3fhr5-rd4kd5-er5tgd5k");
        SalesPrice totalSalesPrice = getSalesPrice(getAmountType(123));
        SizeDistribution specifiedSizeDistribution = getSizeDistribution(
                getCodeType("catagory 1", "qbd43cg-3fhr5t65-rd4kd5rt4-er5tgd5k"),
                getCodeType("class code1", "qbd43cg-3fhr5t65-rd45674-er5tgd5k"));

        return new AAPProduct(
                speciesCode,
                quantityType,
                measureType,
                weighingMeansCode,
                usageCode,
                packagingUnitQuantity,
                packagingTypeCode,
                packagingUnitAverageWeightMeasure,
                null,
                totalSalesPrice,
                specifiedSizeDistribution,
                null,
                null);
    }

    public static AAPStock getAapStock() {
        return new AAPStock(getIdType("Id Type1", "4fjgt7-4rifi65-4rjf75-4ru85gf"));
    }

    public static StructuredAddress getStructuredAddress() {
        IDType id = getIdType("ID type 1", "5ryit6-5tj47e-45jfyr-4tu57fg");
        CodeType postcodeCode = getCodeType("Post code 1", "5ryit6-5tj47e-45jfyr-4tu57fg-rt54tgr");
        TextType buildingName = getTextType("Test Building");
        TextType streetName = getTextType("Test Street");
        TextType cityName = getTextType("Test City");
        IDType countryID = getIdType("Test Country", "ryfht53-fht5-6htfur-57thft");
        TextType citySubDivisionName = getTextType("Test city subdivision");
        TextType countryName = getTextType("Test Country");
        TextType countrySubDivisionName = getTextType("Test country subdivision");
        TextType blockName = getTextType("Test Block");
        TextType plotIdentification = getTextType("123");
        TextType postOfficeBox = getTextType("548675");
        TextType buildingNumber = getTextType("12345");
        TextType staircaseNumber = getTextType("3456");
        TextType floorIdentification = getTextType("8888");
        TextType roomIdentification = getTextType("555");
        TextType postalArea = getTextType("123");

        return new StructuredAddress(id, postcodeCode, buildingName,
                streetName, cityName, countryID, citySubDivisionName, countryName, countrySubDivisionName, blockName, plotIdentification,
                postOfficeBox, buildingNumber, staircaseNumber, floorIdentification, roomIdentification,postalArea);
    }

    public static ContactParty getContactParty() {
        ContactParty contactParty = new ContactParty();
        CodeType roleCode = getCodeType("MASTER", "FLUX_CONTACT_ ROLE");
        contactParty.setRoleCodes(Collections.singletonList(roleCode));
        contactParty.setSpecifiedContactPersons(Collections.singletonList(getContactPerson()));
        contactParty.setSpecifiedStructuredAddresses(Collections.singletonList(getStructuredAddress()));
        return contactParty;
    }

    public static ContactPerson getContactPerson() {
        TextType title = getTextType("MR");
        TextType givenName = getTextType("Test Name");
        TextType middleName = getTextType("Test Middle Name");
        TextType familyNamePrefix = getTextType("Test Prefix");
        TextType familyName = getTextType("Test Family Name");
        TextType nameSuffix = getTextType("Test Suffix");
        CodeType genderCode = getCodeType("Gender", "4ryf65-fhtfyd-thfey45-tu5r7ght");
        TextType alias = getTextType("Test Alias");
        return new ContactPerson(title, givenName, middleName, familyNamePrefix,
             familyName, nameSuffix, genderCode, alias, null, null, null, null,null,null,null);
    }

    public static DelimitedPeriod getDelimitedPeriod() {
        DateTimeType startDate = getDateTimeType("2011-07-01 11:15:00");
        DateTimeType endDate = getDateTimeType("2016-07-01 11:15:00");
        MeasureType measureType = getMeasureType(500, "C62", "4rhfy5-fhtydr-tyfr85-ghtyd54");
        return new DelimitedPeriod(startDate, endDate, measureType);
    }

    public static RegistrationLocation getRegistrationLocation() {
        IDType countryID = getIdType("XEU", "TERRITORY");
        List<TextType> descriptions = Collections.singletonList(getTextType("This is Test Text"));
        CodeType geopoliticalRegionCode = getCodeType("Region Code 1", "57tug6-tfu576-5tud75-t57e5td-56tdwe");
        List<IDType> ids = Collections.singletonList(getIdType("ID 2", "fhtyr8-45jrf-5784fhrt-thf75"));
        List<TextType> names = Collections.singletonList(getTextType("This is Test Name"));
        CodeType typeCode = getCodeType("Code type 1", "475rhf-587trhdy-thgy576-thfr64");
        return new RegistrationLocation(countryID, descriptions, geopoliticalRegionCode, ids, names, typeCode, null);
    }

    public static RegistrationEvent getRegistrationEvent() {
        List<TextType> descriptions = Collections.singletonList(getTextType("This is test Text"));
        DateTimeType occurrenceDateTime = getDateTimeType("2016-07-01 11:15:00");
        RegistrationLocation relatedRegistrationLocation = getRegistrationLocation();
        return new RegistrationEvent(descriptions, occurrenceDateTime, relatedRegistrationLocation);
    }

    public static VesselTransportMeans getVesselTransportMeans() {
        VesselTransportMeans vesselTransportMeans = new VesselTransportMeans();
        CodeType roleCode = getCodeType("CATCHING_VESSEL", "FA_VESSEL_ROLE");
        List<TextType> names = Collections.singletonList(getTextType("Test Name"));
        List<FLAPDocument> grantedFLAPDocuments = Collections.singletonList(getFlapDocument());
        List<IDType> ids = Collections.singletonList(getIdType("ID 1", "CFR"));
        List<ContactParty> specifiedContactParties = Collections.singletonList(getContactParty());
        List<RegistrationEvent> specifiedRegistrationEvents = Collections.singletonList(getRegistrationEvent());
        VesselCountry vesselCounty = new VesselCountry(getIdType("Country Id 1", "tu587r-5jt85-tjfur7-tjgut7"));
        vesselTransportMeans.setRoleCode(roleCode);
        vesselTransportMeans.setNames(names);
        vesselTransportMeans.setGrantedFLAPDocuments(grantedFLAPDocuments);
        vesselTransportMeans.setIDS(ids);
        vesselTransportMeans.setSpecifiedContactParties(specifiedContactParties);
        vesselTransportMeans.setSpecifiedRegistrationEvents(specifiedRegistrationEvents);
        vesselTransportMeans.setRegistrationVesselCountry(vesselCounty);
        return vesselTransportMeans;
    }

    public static VesselStorageCharacteristic getVesselStorageCharacteristic() {
        VesselStorageCharacteristic vesselStorageCharacteristic = new VesselStorageCharacteristic();
        List<CodeType> typeCodes = Collections.singletonList(getCodeType("CONTAINER", "VESSEL_STORAGE_TYPE"));
        IDType id = getIdType("ID 1", "687yu5-tught6-thfyr-5yt74e");
        vesselStorageCharacteristic.setID(id);
        vesselStorageCharacteristic.setTypeCodes(typeCodes);
        return vesselStorageCharacteristic;
    }

    public static SizeDistribution getSizeDistribution() {
        CodeType categoryCode = getCodeType("S6", "FA_BFT_SIZE_CATEGORY");
        List<CodeType> classCodes = Collections.singletonList(getCodeType("LSC", "FISH_SIZE_CLASS"));
        return new SizeDistribution(categoryCode, classCodes);
    }

    public static GearCharacteristic getGearCharacteristics() {
        CodeType typeCode = getCodeType("Code 1", "57t3yf-ght43yrf-ght56yru-ght7565h");
        List<TextType> descriptions = Collections.singletonList(getTextType("This is sample text"));
        MeasureType valueMeasure = getMeasureType(123, "C62", "57t3yf-ght43yrf-ght56yru-ght7565h");
        DateTimeType valueDateTime = getDateTimeType("2016-07-01 11:15:00");
        IndicatorType valueIndicator = getIndicatorType();
        CodeType valueCode = getCodeType("Code type 1", "4fhry5-thfyr85-67thf-5htr84");
        TextType value = getTextType("This is sample Text");
        QuantityType valueQuantity = getQuantityType(123);

        List<FLUXLocation> specifiedFluxLocations = Collections.singletonList(getFluxLocation());
        return new GearCharacteristic(typeCode, descriptions, valueMeasure, valueDateTime, valueIndicator, valueCode, value, valueQuantity,specifiedFluxLocations);
    }

    public static FLUXCharacteristic getFluxCharacteristics() {
        CodeType typeCode = getCodeType("Code 1", "57t3yf-ght43yrf-ght56yru-ght7565h");
        List<TextType> descriptions = Collections.singletonList(getTextType("This is test description"));
        MeasureType valueMeasure = getMeasureType(333, "C62", "57t3yf-ght43yrf-ght56yru-ght7565h");
        DateTimeType valueDateTime = getDateTimeType("2016-07-01 11:15:00");
        IndicatorType valueIndicator = getIndicatorType();
        CodeType valueCode = getCodeType("Code Value 1", "57tr4t3yf-ght43yrf-ght56yr5u-ght75365h");
        List<TextType> values = Collections.singletonList(getTextType("This is sample value"));
        QuantityType valueQuantity = getQuantityType(123);
        List<FLUXLocation> specifiedFLUXLocations=null;
         List<FLAPDocument> relatedFLAPDocuments = Collections.singletonList(getFlapDocument());
        return new FLUXCharacteristic(typeCode, descriptions, valueMeasure, valueDateTime, valueIndicator, valueCode, values, valueQuantity,specifiedFLUXLocations,relatedFLAPDocuments);
    }

    public static FishingGear getFishingGear() {
        FishingGear fishingGear = new FishingGear();
        CodeType typeCode = getCodeType("Code Type 1", "57t3yf-ght43yrf-ght56yru-ght7565h");
        List<CodeType> roleCodes = Collections.singletonList(getCodeType("Role Code 1", "57t3yf-g43yrf-ght56ru-ght65h"));
        fishingGear.setTypeCode(typeCode);
        fishingGear.setRoleCodes(roleCodes);
        fishingGear.setApplicableGearCharacteristics(Collections.singletonList(getGearCharacteristics()));
        return fishingGear;
    }

    public static FishingTrip getFishingTrip() {
        List<IDType> ids = Collections.singletonList(getIdType("ID 1", "fhty58-gh586t-5tjf8-t58rjewe"));
        CodeType typeCode = getCodeType("Code Type 1", "57t3yf-ght43yrf-ght56yru-ght7565h");
        List<DelimitedPeriod> specifiedDelimitedPeriods = Collections.singletonList(getDelimitedPeriod());
        return new FishingTrip(ids, typeCode, specifiedDelimitedPeriods);
    }

    public static GearProblem getGearProblem() {
        CodeType typeCode = getCodeType("Code Type 1", "fhty58-gh586t-5tjf8-t58rjewe");
        QuantityType affectedQuantity = getQuantityType(222);
        List<CodeType> recoveryMeasureCodes = Collections.singletonList(getCodeType("Quantity Code 1", "57t3yf-ght43yrf-ght56yru-ght7565h"));
        List<FishingGear> relatedFishingGears = Collections.singletonList(getFishingGear());
        return new GearProblem(typeCode, affectedQuantity, recoveryMeasureCodes, Collections.singletonList(getFluxLocation()), relatedFishingGears);
    }

    public static FLUXLocation getFluxLocation() {
        FLUXLocation fluxLocation = new FLUXLocation();
        CodeType typeCode = getCodeType("AREA", "FLUX_LOCATION_TYPE");
        IDType countryID = getIdType("XEU", "TERRITORY");
        CodeType regionalFisheriesManagementOrganizationCode = getCodeType("RFMO1", "fhty58-gh586t-5tjf8-t58rjewe");
        FLUXGeographicalCoordinate specifiedPhysicalFLUXGeographicalCoordinate = getFluxGeographicalCoordinate();
        IDType id = getIdType("25.5b", "FAO_AREA");
        CodeType geopoliticalRegionCode = getCodeType("Code type 2", "fhty258-g3h586t-5t4jf8-t58rjew5e");
        List<TextType> names = Collections.singletonList(getTextType("This is sample name"));
        IDType sovereignRightsCountryID = getIdType("sovereign rights id 1", "fhty58-gh5486t-5t5jf8-t58rjewe");
        IDType jurisdictionCountryID = getIdType("jurisdiction country id 1", "fht1y58-gh5876t-5t3jf8-t58rjewe");
        List<FLUXCharacteristic> applicableFLUXCharacteristics = null;
        List<StructuredAddress> postalStructuredAddresses = Collections.singletonList(getStructuredAddress());
        StructuredAddress physicalStructuredAddress = getStructuredAddress();

        fluxLocation.setTypeCode(typeCode);
        fluxLocation.setCountryID(countryID);
        fluxLocation.setRegionalFisheriesManagementOrganizationCode(regionalFisheriesManagementOrganizationCode);
        fluxLocation.setSpecifiedPhysicalFLUXGeographicalCoordinate(specifiedPhysicalFLUXGeographicalCoordinate);
        fluxLocation.setID(id);
        fluxLocation.setGeopoliticalRegionCode(geopoliticalRegionCode);
        fluxLocation.setNames(names);
        fluxLocation.setSovereignRightsCountryID(sovereignRightsCountryID);
        fluxLocation.setJurisdictionCountryID(jurisdictionCountryID);
        fluxLocation.setApplicableFLUXCharacteristics(applicableFLUXCharacteristics);
        fluxLocation.setPostalStructuredAddresses(postalStructuredAddresses);
        fluxLocation.setPhysicalStructuredAddress(physicalStructuredAddress);
        return fluxLocation;
    }

    public static FLUXReportDocument getFluxReportDocumentCorrection() {
        List<IDType> ids = Collections.singletonList(getIdType("flux_report_doc_1", "fhty58-gh586t-5tjf8-t58rjewe"));
        IDType referencedID = getIdType("Ref ID 1", "fhty58-gh586t-5tjf8-t58rjewe");
        DateTimeType creationDateTime = getDateTimeType("2016-07-01 11:15:00");
        CodeType purposeCode = getCodeType("5", "FLUX_GP_PURPOSE");
        final TextType purpose = getTextType("Purpose Text");
        CodeType typeCode = getCodeType("FluxReportTypeCode", "fhty58-gh586t-5tjf8-t58rjewe");
        FLUXParty ownerFLUXParty = new FLUXParty(Collections.singletonList(getIdType("Owner flux party id 1", "58fjrut-tjfuri-586jte-5jfur")),
                Collections.singletonList(getTextType("This is sample text for owner flux party")));
        return new FLUXReportDocument(ids, referencedID, creationDateTime, purposeCode, purpose, typeCode, ownerFLUXParty);
    }

    public static FACatch getFaCatch() {
        CodeType speciesCode = getCodeType("ONBOARD", "FAO_SPECIES");
        QuantityType unitQuantity = getQuantityType(100);
        MeasureType weightMeasure = getMeasureType(123, "C62", "586jhg-5htuf95-5jfit-5jtier8");
        CodeType weighingMeansCode = getCodeType("Weighing means code 1", "5854tt5-gjtdir-5j85tui-589git");
        CodeType usageCode = getCodeType("Usage code 1", "58thft-58fjd8-gt85eje-hjgute8");
        CodeType typeCode = getCodeType("Type code 1", "FA_CATCH_TYPE");
        final List<FishingTrip> relatedFishingTrips = Collections.singletonList(getFishingTrip());
        SizeDistribution specifiedSizeDistribution = getSizeDistribution();
        List<AAPStock> relatedAAPStocks = Collections.singletonList(getAapStock());
        List<AAPProcess> appliedAAPProcesses = Collections.singletonList(getAapProcess());
        List<SalesBatch> relatedSalesBatches = null;
        List<FLUXLocation> specifiedFLUXLocations = Collections.singletonList(getFluxLocation());
        List<FishingGear> usedFishingGears = Collections.singletonList(getFishingGear());


        List<FLUXCharacteristic> applicableFLUXCharacteristics = Collections.singletonList(getFluxCharacteristics());
        List<FLUXLocation> destinationFLUXLocations = Collections.singletonList(getFluxLocation());
        return new FACatch(speciesCode, unitQuantity, weightMeasure, weighingMeansCode, usageCode,
                typeCode, relatedFishingTrips, specifiedSizeDistribution, relatedAAPStocks, appliedAAPProcesses, relatedSalesBatches,
                specifiedFLUXLocations, usedFishingGears, applicableFLUXCharacteristics, destinationFLUXLocations);
    }

    public static FishingActivity getFishingActivity() {
        FishingActivity fishingActivity = getStandardFishingActivity();
        List<FishingActivity> relatedFishingActivities = Collections.singletonList(getStandardFishingActivity());
        fishingActivity.setRelatedFishingActivities(relatedFishingActivities);
        return fishingActivity;
    }

    public static FLUXFAReportMessage getFLUXFAReportMessage() {
        return new FLUXFAReportMessage(getFluxReportDocumentNew(), Collections.singletonList(getFaReportDocument()));
    }

    public static FLUXReportDocument getFluxReportDocumentNew(){
        new FLUXReportDocument();
        List<IDType> ids = Collections.singletonList(getIdType("FLUX_REPORT_ID_1", "FLUX_SCHEME_ID1"));
        IDType referenceId=getIdType("REF_ID 1", "47rfh-5hry4-thfur75-4hf743");
        DateTimeType creationDateTime = getDateTimeType("2016-07-01 11:15:00");
        CodeType purposeCode = getCodeType("9", "FLUX_GP_PURPOSE");
        CodeType typeCode = getCodeType("type Code1", "fhr574fh-thrud754-kgitjf754-gjtufe89");
        List<IDType> ownerFluxPartyId = Collections.singletonList(getIdType("Owner_flux_party_id_1", "flux_Party_scheme_id"));
        List<TextType> names = Collections.singletonList(getTextType("fluxPartyOwnerName 1"));
        FLUXParty fluxParty = new FLUXParty(ownerFluxPartyId,names);

        return new FLUXReportDocument(ids, referenceId, creationDateTime, purposeCode, getTextType("Purpose"), typeCode, fluxParty);
    }

    public static FAReportDocument getFaReportDocument() {
        CodeType typeCode = getCodeType("DECLARATION", "FLUX_FA_REPORT_TYPE");
        CodeType fmcMarkerCode = getCodeType("Fmz marker 1", "h49rh-fhrus33-fj84hjs82-4h84hw82");
        List<IDType> relatedReportIDs = Collections.singletonList(getIdType("ID 1", "47rfh-5hry4-thfur75-4hf743"));
        DateTimeType acceptanceDateTime = getDateTimeType("2016-07-01 11:15:00");
        FLUXReportDocument relatedFLUXReportDocument = getFluxReportDocumentCorrection();
        List<FishingActivity> specifiedFishingActivities = Collections.singletonList(getFishingActivity());
        VesselTransportMeans specifiedVesselTransportMeans = getVesselTransportMeans();
        return new FAReportDocument(typeCode, fmcMarkerCode, relatedReportIDs, acceptanceDateTime,
                relatedFLUXReportDocument, specifiedFishingActivities, specifiedVesselTransportMeans);
    }

    private static FishingActivity getStandardFishingActivity() {
        List<IDType> ids = Collections.singletonList(getIdType("Id_1", "fhr574fh-thrud754-kgitjf754-gjtufe89"));
        CodeType typeCode = getCodeType("FISHING_OPERATION", "FLUX_FA_TYPE");
        DateTimeType occurrenceDateTime = getDateTimeType("2016-07-01 11:15:00");
        CodeType reasonCode = getCodeType("Reason_code_1", "FA_REASON_DEPARTURE");
        CodeType vesselRelatedActivityCode = getCodeType("Vessel activity 1", "58thft-58fjd8-gt85eje-hjgute8");
        CodeType fisheryTypeCode = getCodeType("Fishing_Type_code 1", "FA_FISHERY");
        CodeType speciesTargetCode = getCodeType("Species code 1", "FAO_SPECIES");
        QuantityType operationsQuantity = getQuantityType(100);
        MeasureType fishingDurationMeasure = getMeasureType(500, "C62", "4hr2yf0-t583thf-6jgttue8-6jtie844");
        List<FLAPDocument> specifiedFLAPDocument = Collections.singletonList(getFlapDocument());
        VesselStorageCharacteristic sourceVesselStorageCharacteristic = getVesselStorageCharacteristic();
        VesselStorageCharacteristic destinationVesselStorageCharacteristic = getVesselStorageCharacteristic();
        List<FACatch> specifiedFACatches = Collections.singletonList(getFaCatch());
        List<FLUXLocation> relatedFLUXLocations = Collections.singletonList(getFluxLocation());
        List<GearProblem> specifiedGearProblems = Collections.singletonList(getGearProblem());
        List<FLUXCharacteristic> specifiedFLUXCharacteristics = Collections.singletonList(getFluxCharacteristics());
        List<FishingGear> specifiedFishingGears = Collections.singletonList(getFishingGear());
        List<DelimitedPeriod> specifiedDelimitedPeriods = Collections.singletonList(getDelimitedPeriod());
        FishingTrip specifiedFishingTrip = getFishingTrip();
        List<VesselTransportMeans> relatedVesselTransportMeans = Collections.singletonList(getVesselTransportMeans());

        return new FishingActivity (ids, typeCode, occurrenceDateTime, reasonCode, vesselRelatedActivityCode, fisheryTypeCode, speciesTargetCode, operationsQuantity, fishingDurationMeasure,
                specifiedFACatches, relatedFLUXLocations, specifiedGearProblems, specifiedFLUXCharacteristics,
                specifiedFishingGears, sourceVesselStorageCharacteristic, destinationVesselStorageCharacteristic,
        null, specifiedFLAPDocument, specifiedDelimitedPeriods, specifiedFishingTrip,
                relatedVesselTransportMeans);
    }

    private static FLUXGeographicalCoordinate getFluxGeographicalCoordinate() {
        return new FLUXGeographicalCoordinate(
                getMeasureType(123, "longitude", "fhty58-gh586t-5tjf8-t58rjewe"),
                getMeasureType(234, "latitude", "fhty258-g3h586t-5t4jf8-t58rjew5e"),
                getMeasureType(345, "altitude", "fhty58-gh5686t-5tjf8-t58rjew8e"),
                getIdType("System ID1", "fhty58-gh58666t-5tjf438-t58rjewe")
        );
    }

    private static IndicatorType getIndicatorType() {
        IndicatorType indicatorType = new IndicatorType();
        indicatorType.setIndicator(true);
        indicatorType.setIndicatorString(new IndicatorType.IndicatorString("Test value", "Test format"));
        return indicatorType;
    }

    private static FLAPDocument getFlapDocument() {
        FLAPDocument flapDocument = new FLAPDocument();
        IDType id = getIdType("ID 1", "fhtg56-5thd75-thf6t93-thfrye");
        flapDocument.setID(id);
        return flapDocument;
    }

    public static DateTimeType getDateTimeType(String value) {
        try {
            DateTimeType dateTimeType = new DateTimeType();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            GregorianCalendar cal = new GregorianCalendar();
            Date parse = df.parse(value);
            cal.setTime(parse);
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar xmlDate = datatypeFactory.newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), DatatypeConstants.FIELD_UNDEFINED, 0);
            XMLGregorianCalendar normalize = xmlDate.normalize();
            dateTimeType.setDateTime(normalize);
            return dateTimeType;
        } catch (Exception e) {
            return null;
        }
    }

    private static TextType getTextType(String value) {
        TextType textType = new TextType();
        textType.setValue(value);
        return textType;
    }

    public static IDType getIdType(String type, String schemeId) {
        IDType idType = new IDType();
        idType.setValue(type);
        idType.setSchemeID(schemeId);
        return idType;
    }

    private static SizeDistribution getSizeDistribution(CodeType catagoryCode, CodeType classCode) {
        SizeDistribution sizeDistribution = new SizeDistribution();
        sizeDistribution.setCategoryCode(catagoryCode);
        sizeDistribution.setClassCodes(Collections.singletonList(classCode));
        return sizeDistribution;
    }
    private static SalesPrice getSalesPrice(AmountType amountType) {
        SalesPrice salesPrice = new SalesPrice();
        salesPrice.setChargeAmounts(Collections.singletonList(amountType));
        return salesPrice;
    }

    private static AmountType getAmountType(int value) {
        AmountType amountType = new AmountType();
        amountType.setValue(new BigDecimal(value));
        amountType.setCurrencyCodeListVersionID("qbd43cg-3fhr5t65-rd4kd5rt4-er5tgd5k");
        amountType.setCurrencyID("1");
        return amountType;
    }

    public static CodeType getCodeType(String value, String listId) {
        CodeType codeType = new CodeType();
        codeType.setValue(value);
        codeType.setListID(listId);
        return codeType;
    }

    private static NumericType getNumericType(int value) {
        NumericType numericType = new NumericType();
        numericType.setValue(new BigDecimal(value));
        return numericType;
    }

    private static QuantityType getQuantityType(int value) {
        QuantityType quantityType = new QuantityType();
        quantityType.setValue(new BigDecimal(value));
        quantityType.setUnitCode("C62");
        quantityType.setUnitCodeListID("thfy586-gjtur95-tjgut49-f58f93j");
        return quantityType;
    }

    private static MeasureType getMeasureType(int value, String unitCode, String listId) {
        MeasureType measureType = new MeasureType();
        measureType.setValue(new BigDecimal(value));
        measureType.setUnitCode(unitCode);
        measureType.setUnitCodeListVersionID(listId);
        return measureType;
    }
}
