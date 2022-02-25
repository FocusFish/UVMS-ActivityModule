package fish.focus.uvms.activity.service.facatch.evolution;

import java.time.Instant;
import java.util.Comparator;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;

public class FishingActivityCalculatedDateComparator implements Comparator<FishingActivityEntity> {
    @Override
    public int compare(FishingActivityEntity fishingActivity1, FishingActivityEntity fishingActivity2) {
        Instant calculatedStartTime1 = fishingActivity1.getCalculatedStartTime();
        Instant calculatedStartTime2 = fishingActivity2.getCalculatedStartTime();

        if(calculatedStartTime1 != null) {
            return calculatedStartTime1.compareTo(calculatedStartTime2);
        }

        return 1;
    }
}
