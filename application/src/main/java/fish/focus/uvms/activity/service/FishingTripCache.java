package fish.focus.uvms.activity.service;

import java.util.HashMap;
import java.util.Map;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.entities.FishingTripEntity;
import fish.focus.uvms.activity.fa.entities.FishingTripKey;

public class FishingTripCache {
    private Map<FishingTripKey, FishingTripEntity> fishingTripEntityMap = new HashMap<>();

    private FishingTripEntity add(FishingTripEntity fishingTripEntity) {
        FishingTripEntity existingEntity = fishingTripEntityMap.putIfAbsent(fishingTripEntity.getFishingTripKey(), fishingTripEntity);
        if (existingEntity != null) {
            return existingEntity;
        }
        return fishingTripEntity;
    }

    public void addAndUpdateFishingTripOfActivityIfItExists(FishingActivityEntity fishingActivityEntity) {
        FishingTripEntity fishingTrip = fishingActivityEntity.getFishingTrip();
        FishingTripEntity fishingTripEntity = add(fishingTrip);
        fishingActivityEntity.setFishingTrip(fishingTripEntity);
    }
}
