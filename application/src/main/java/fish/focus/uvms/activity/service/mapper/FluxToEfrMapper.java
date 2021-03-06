package fish.focus.uvms.activity.service.mapper;

import javax.ejb.Stateless;
import fish.focus.uvms.activity.fa.entities.FaCatchEntity;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.entities.FishingGearEntity;
import fish.focus.uvms.activity.fa.entities.FishingTripEntity;
import fish.focus.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import fish.focus.uvms.activity.fa.entities.GearCharacteristicEntity;
import fish.focus.uvms.activity.fa.entities.LocationEntity;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import fish.focus.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import fish.focus.uvms.activity.service.dto.efrbackend.ArrivalLocation;
import fish.focus.uvms.activity.service.dto.efrbackend.CatchGearSettings;
import fish.focus.uvms.activity.service.dto.efrbackend.CatchSpecies;
import fish.focus.uvms.activity.service.dto.efrbackend.FishingCatch;
import fish.focus.uvms.activity.service.dto.efrbackend.FishingReport;
import fish.focus.uvms.activity.service.dto.efrbackend.PriorNotificationDto;
import fish.focus.uvms.activity.service.dto.efrbackend.PriorNotificationEstimatedCatch;
import fish.focus.uvms.activity.service.dto.efrbackend.UserSpecifiedLocation;
import fish.focus.uvms.activity.service.exception.ModelMapperException;
import fish.focus.uvms.activity.service.util.Utils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 * This maps fishing reports from the FLUX format to the format used by the EFR backend
 */
@Stateless
public class FluxToEfrMapper {
    public FishingReport fluxMessageEntityToEfrFishingReport(FluxFaReportMessageEntity fluxMessage) throws ModelMapperException {
        FishingReport result = new FishingReport();

        try {
            String fishingTripId = getFishingTripId(fluxMessage);
            result.setFishingReportId(UUID.fromString(fishingTripId));
        } catch (IllegalArgumentException e) {
            // TODO log that the report document ID was not a UUID
            return null;
        }

        // TODO timestamps?

        result.setShipCfr(getShipCfr(fluxMessage));
        result.setTargetSpeciesCode(getTargetSpeciesCode(fluxMessage));
        result.setPriorNotification(createPriorNotification(fluxMessage));
        result.setFishingCatches(createFishingCatches(fluxMessage));

        return result;
    }

    private String getFishingTripId(FluxFaReportMessageEntity fluxMessage) throws ModelMapperException {
        Set<String> tripIds = new HashSet<>();

        // Find every fishing trip ID in all activities in all documents
        for (FaReportDocumentEntity faReportDocument : Utils.safeIterable(fluxMessage.getFaReportDocuments())) {
            for (FishingActivityEntity fishingActivity : Utils.safeIterable(faReportDocument.getFishingActivities())) {
                FishingTripEntity fishingTrip = fishingActivity.getFishingTrip();
                if (fishingTrip != null) {
                    if (fishingTrip.getFishingTripKey() != null) {
                        tripIds.add(fishingTrip.getFishingTripKey().getTripId());
                    }
                }
            }
        }

        // remove any null values
        tripIds.remove(null);

        if (tripIds.size() > 1) {
            throw new ModelMapperException("More than one fishing trip ID found in FLUX message");
        }

        if (tripIds.isEmpty()) {
            return null;
        }

        return tripIds.iterator().next();
    }

    private String getShipCfr(FluxFaReportMessageEntity fluxMessage) throws ModelMapperException {
        Set<String> cfrs = new HashSet<>();

        for (FaReportDocumentEntity faReportDocument : fluxMessage.getFaReportDocuments()) {
            if (faReportDocument.getVesselTransportMeans() == null) {
                continue;
            }

            for (VesselTransportMeansEntity vesselTransportMean : faReportDocument.getVesselTransportMeans()) {
                if (vesselTransportMean.getVesselIdentifiersMap() != null) {
                    cfrs.add(vesselTransportMean.getVesselIdentifiersMap().get(VesselIdentifierSchemeIdEnum.CFR));
                }
            }
        }

        if (cfrs.size() > 1) {
            // TODO good enough exception class?
            throw new ModelMapperException("More than one ship CFR found in FLUX message");
        }

        if (cfrs.isEmpty()) {
            return null;
        }

        return cfrs.iterator().next();
    }

    private String getTargetSpeciesCode(FluxFaReportMessageEntity fluxMessage) {
        // find departure fishing activity
        FishingActivityEntity departureFishingActivity = findFishingActivity(fluxMessage, "DEPARTURE");
        return departureFishingActivity != null ? departureFishingActivity.getSpeciesTargetCode() : null;
    }

    private PriorNotificationDto createPriorNotification(FluxFaReportMessageEntity fluxMessage) throws ModelMapperException {
        // find prior notification of arrival fishing activity
        // TODO note this could be an actual arrival, not just prior notification.
        // As of writing we don't create an actual arrival fishing activity, but
        // according to the spec they should have the same type code... Weird.
        FishingActivityEntity priorNotificationActivity = findFishingActivity(fluxMessage, "ARRIVAL");

        if (priorNotificationActivity == null) {
            // TODO good enough exception class?
            throw new ModelMapperException("Found no prior notification fishing activity");
        }

        PriorNotificationDto result = new PriorNotificationDto();
        // TODO result.timestamps?

        result.setEstimatedLandingTime(priorNotificationActivity.getOccurence());
        result.setArrivalLocation(createArrivalLocation(priorNotificationActivity));
        result.setEstimatedCatches(createPriorNotificationCatches(priorNotificationActivity));

        return result;
    }

    private ArrivalLocation createArrivalLocation(FishingActivityEntity priorNotificationActivity) throws ModelMapperException {
        Set<LocationEntity> locations = priorNotificationActivity.getLocations();
        if (locations == null || locations.isEmpty() || locations.size() > 2) {
            throw new ModelMapperException("Didn't find exactly one location in prior notification fishing activity");
        }

        LocationEntity location = locations.iterator().next();

        ArrivalLocation result = new ArrivalLocation();
        result.setPortCode(location.getFluxLocationIdentifier());

        if (location.getName() != null ||
                priorNotificationActivity.getLatitude() != null ||
                priorNotificationActivity.getLongitude() != null) {
            UserSpecifiedLocation userSpecifiedLocation = new UserSpecifiedLocation();
            userSpecifiedLocation.setName(location.getName());
            userSpecifiedLocation.setLatitude(latLongDdToDms(priorNotificationActivity.getLatitude()));
            userSpecifiedLocation.setLongitude(latLongDdToDms(priorNotificationActivity.getLongitude()));
            result.setUserSpecifiedLocation(userSpecifiedLocation);
        }

        return result;
    }

    private Collection<PriorNotificationEstimatedCatch> createPriorNotificationCatches(FishingActivityEntity priorNotificationActivity) {
        Set<FaCatchEntity> fluxCatches = priorNotificationActivity.getFaCatchs();
        if (fluxCatches == null || fluxCatches.isEmpty()) {
            return new HashSet<>();
        }

        Collection<PriorNotificationEstimatedCatch> result = new ArrayList<>();
        for (FaCatchEntity fluxCatch : fluxCatches) {
            if (fluxCatch.getSpeciesCode() == null) {
                continue;
            }

            PriorNotificationEstimatedCatch efrCatch = new PriorNotificationEstimatedCatch();
            efrCatch.setCode(fluxCatch.getSpeciesCode());
            efrCatch.setQuantity(fluxCatch.getUnitQuantity() != null ? fluxCatch.getUnitQuantity().intValue() : null);
            efrCatch.setWeightInKilos(fluxCatch.getWeightMeasure());

            result.add(efrCatch);
        }

        return result;
    }

    private List<FishingCatch> createFishingCatches(FluxFaReportMessageEntity fluxMessage) throws ModelMapperException {
        List<FishingActivityEntity> fishingOperations = findFishingActivities(fluxMessage, "FISHING_OPERATION");
        List<FishingActivityEntity> discardActivities = findFishingActivities(fluxMessage, "DISCARD");
        List<FishingCatch> result = new ArrayList<>();

        // TODO OK to use occurrence as "ID"?
        // sort and pair up fishing operations and discards based on the occurrence:
        // same occurrence = same EFR FishingCatch
        // SortedMap to sort the FishingCatch in chronological order
        SortedMap<Instant, Set<FishingActivityEntity>> sortedFluxActivities = new TreeMap<>();

        for (FishingActivityEntity fishingOperation : fishingOperations) {
            // Get occurrence from fishing operation or it's subactivity TODO make nicer
            Instant occurrence = fishingOperation.getOccurence() != null ? fishingOperation.getOccurence() :
                    (fishingOperation.getRelatedFishingActivity() != null ? fishingOperation.getRelatedFishingActivity().getOccurence() : null);

            sortedFluxActivities.putIfAbsent(occurrence, new HashSet<>());
            sortedFluxActivities.get(occurrence).add(fishingOperation);
        }
        for (FishingActivityEntity discardActivity : discardActivities) {
            // Get occurrence from fishing operation or it's subactivity TODO make nicer
            Instant occurrence = discardActivity.getOccurence() != null ? discardActivity.getOccurence() :
                    (discardActivity.getRelatedFishingActivity() != null ? discardActivity.getRelatedFishingActivity().getOccurence() : null);

            sortedFluxActivities.putIfAbsent(occurrence, new HashSet<>());
            sortedFluxActivities.get(occurrence).add(discardActivity);
        }

        for (Set<FishingActivityEntity> sameOccurrenceActivities : sortedFluxActivities.values()) {
            result.add(createFishingCatch(sameOccurrenceActivities));
        }

        return result;
    }

    private FishingCatch createFishingCatch(Set<FishingActivityEntity> sameOccurrenceActivities) {
        boolean allEmptyCatches = true;
        for (FishingActivityEntity fluxActivity : sameOccurrenceActivities) {
            allEmptyCatches &= fluxActivity.getFaCatchs() == null || fluxActivity.getFaCatchs().isEmpty();
        }

        FishingCatch result = new FishingCatch();

        if (allEmptyCatches) {
            result.setEmptyCatch(true);
        } else {
            result.setSpecies(createCatchSpecies(sameOccurrenceActivities));
        }

        FishingGearEntity fluxGear = null;
        for (FishingActivityEntity fluxActivity : sameOccurrenceActivities) {
            if (fluxActivity.getFishingGears() != null && !fluxActivity.getFishingGears().isEmpty()) {
                fluxGear = fluxActivity.getFishingGears().iterator().next();
                break;
            }
        }

        // FLUX Gear can be null if we have only discards at this occurrence
        if (fluxGear != null) {
            result.setGearCode(fluxGear.getTypeCode());

            CatchGearSettings catchGearSettings = new CatchGearSettings();
            for (GearCharacteristicEntity gearCharacteristic : Utils.safeIterable(fluxGear.getGearCharacteristics())) {
                // ME = Mesh size
                if (GearCharacteristicConstants.GEAR_CHARAC_TYPE_CODE_ME.equals(gearCharacteristic.getTypeCode())) {
                    catchGearSettings.setMeshSizeInMillimeter(gearCharacteristic.getValueMeasure().intValue());
                }

                // TODO set quantity, but we don't know yet whether it is meters of net or number of gear
            }

            if (fluxGear.getFishingActivity() != null && fluxGear.getFishingActivity().getRelatedFishingActivity() != null) {
                FishingActivityEntity subActivity = fluxGear.getFishingActivity().getRelatedFishingActivity();
                UserSpecifiedLocation userSpecifiedLocation = new UserSpecifiedLocation();

                String locationName = null;
                for (LocationEntity subActivityLocation : Utils.safeIterable(subActivity.getLocations())) {
                    if (subActivityLocation.getName() != null) {
                        locationName = subActivityLocation.getName();
                        break;
                    }
                }

                userSpecifiedLocation.setName(locationName);
                userSpecifiedLocation.setLatitude(latLongDdToDms(subActivity.getLatitude()));
                userSpecifiedLocation.setLongitude(latLongDdToDms(subActivity.getLongitude()));
                catchGearSettings.setGearLocation(userSpecifiedLocation);
            }

            result.setCatchGearSettings(catchGearSettings);
        }

        // TODO what to do? There is no place in the FLUX message to store the original UUID
        result.setId(UUID.randomUUID());

        return result;
    }

    private List<CatchSpecies> createCatchSpecies(Set<FishingActivityEntity> sameOccurrenceActivities) {
        // Sorted map so that we can deterministically return the species based on their code
        SortedMap<String, CatchSpecies> speciesMap = new TreeMap<>();
        for (FishingActivityEntity fluxActivity : sameOccurrenceActivities) {
            for (FaCatchEntity fluxCatch : fluxActivity.getFaCatchs()) {
                String speciesCode = fluxCatch.getSpeciesCode();

                if (!speciesMap.containsKey(speciesCode)) {
                    CatchSpecies efrCatchSpecies = new CatchSpecies();
                    efrCatchSpecies.setCode(speciesCode);
                    // TODO what about variants?
                    // TODO what about timestamps?
                    speciesMap.put(speciesCode, efrCatchSpecies);
                }

                addToCatchSpeciesWeights(fluxCatch, speciesMap.get(speciesCode));
            }
        }

        // The values are sorted going into the array list
        return new ArrayList<>(speciesMap.values());
    }

    private void addToCatchSpeciesWeights(FaCatchEntity fluxCatch, CatchSpecies efrCatchSpecies) {
        Integer fluxQuantity = fluxCatch.getUnitQuantity() != null ? fluxCatch.getUnitQuantity().intValue() : null;
        Double fluxWeight = fluxCatch.getWeightMeasure();
        String classCode = fluxCatch.getSizeDistributionClassCode();
        // TODO assert that weight unit is "KGM"?

        Integer oldQuantity = null;
        Double oldWeight = null;

        switch (classCode) {
            case "BMS":
                oldQuantity = efrCatchSpecies.getBmsQuantity();
                oldWeight = efrCatchSpecies.getBmsWeightInKg();
                break;
            case "DIM":
                oldQuantity = efrCatchSpecies.getDimQuantity();
                oldWeight = efrCatchSpecies.getDimWeightInKg();
                break;
            case "DIS":
                oldQuantity = efrCatchSpecies.getDisQuantity();
                oldWeight = efrCatchSpecies.getDisWeightInKg();
                break;
            case "LSC":
                oldQuantity = efrCatchSpecies.getLscQuantity();
                oldWeight = efrCatchSpecies.getLscWeightInKg();
                break;
            case "ROV":
                oldQuantity = efrCatchSpecies.getRovQuantity();
                oldWeight = efrCatchSpecies.getRovWeightInKg();
                break;
            default:
                // TODO throw mapping exception?
        }

        Integer newQuantity = oldQuantity;
        Double newWeight = oldWeight;

        if (fluxQuantity != null) {
            newQuantity = fluxQuantity + (oldQuantity != null ? oldQuantity : 0);
        }
        if (fluxWeight != null) {
            newWeight = fluxWeight + (oldWeight != null ? oldWeight : 0.0);
        }

        switch (classCode) {
            case "BMS":
                efrCatchSpecies.setBmsQuantity(newQuantity);
                efrCatchSpecies.setBmsWeightInKg(newWeight);
                break;
            case "DIM":
                efrCatchSpecies.setDimQuantity(newQuantity);
                efrCatchSpecies.setDimWeightInKg(newWeight);
                break;
            case "DIS":
                efrCatchSpecies.setDisQuantity(newQuantity);
                efrCatchSpecies.setDisWeightInKg(newWeight);
                break;
            case "LSC":
                efrCatchSpecies.setLscQuantity(newQuantity);
                efrCatchSpecies.setLscWeightInKg(newWeight);
                break;
            case "ROV":
                efrCatchSpecies.setRovQuantity(newQuantity);
                efrCatchSpecies.setRovWeightInKg(newWeight);
                break;
            default:
                // do nothing
        }
    }

    private FishingActivityEntity findFishingActivity(FluxFaReportMessageEntity fluxMessage, String typeCode) {
        for (FaReportDocumentEntity faReportDocument : fluxMessage.getFaReportDocuments()) {
            for (FishingActivityEntity fishingActivity : faReportDocument.getFishingActivities()) {
                if (typeCode.equals(fishingActivity.getTypeCode())) {
                    return fishingActivity;
                }
            }
        }

        return null;
    }

    private List<FishingActivityEntity> findFishingActivities(FluxFaReportMessageEntity fluxMessage, String typeCode) {
        List<FishingActivityEntity> result = new ArrayList<>();

        for (FaReportDocumentEntity faReportDocument : fluxMessage.getFaReportDocuments()) {
            for (FishingActivityEntity fishingActivity : faReportDocument.getFishingActivities()) {
                if (typeCode.equals(fishingActivity.getTypeCode())) {
                    result.add(fishingActivity);
                }
            }
        }

        return result;
    }

    /**
     *
     * @param latOrLong Latitude or longitude in decimal degree format
     * @return The same but in degrees-minutes-seconds format
     */
    private static String latLongDdToDms(Double latOrLong) {
        if (latOrLong == null) {
            return null;
        }

        double fractionDegrees = latOrLong % 1;
        int wholeDegrees = (int)(latOrLong - fractionDegrees);
        fractionDegrees = Math.abs(fractionDegrees);

        double minutes = fractionDegrees * 60.0;
        double fractionMinutes = minutes % 1;
        int wholeMinutes = (int)(minutes - fractionMinutes);

        int seconds = (int)Math.round(fractionMinutes * 60.0);

        return wholeDegrees + " " + wholeMinutes + " " + seconds;
    }
}
