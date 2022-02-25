package fish.focus.uvms.activity.service.facatch.evolution;

import org.apache.commons.lang3.EnumUtils;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.utils.FishingActivityTypeEnum;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatchEvolutionProgressProcessor {
    private CatchEvolutionProgressRegistry catchEvolutionProgressRegistry;

    public CatchEvolutionProgressProcessor(CatchEvolutionProgressRegistry catchEvolutionProgressRegistry) {
        this.catchEvolutionProgressRegistry = catchEvolutionProgressRegistry;
    }

    public void setCatchEvolutionProgressRegistry(CatchEvolutionProgressRegistry catchEvolutionProgressRegistry) {
        this.catchEvolutionProgressRegistry = catchEvolutionProgressRegistry;
    }

    public List<CatchEvolutionProgressDTO> process(List<FishingActivityEntity> fishingActivities) {
        Map<String, Double> speciesCumulatedWeight = new HashMap<>();
        List<CatchEvolutionProgressDTO> catchEvolutionProgressDTOs = new ArrayList<>();
        Collections.sort(fishingActivities, new FishingActivityCalculatedDateComparator());
        int orderId = 1;

        for (FishingActivityEntity fishingActivity : fishingActivities) {
            FishingActivityTypeEnum fishingActivityType = EnumUtils.getEnum(FishingActivityTypeEnum.class, fishingActivity.getTypeCode());

            if (fishingActivityType != null && catchEvolutionProgressRegistry != null && catchEvolutionProgressRegistry.containsHandler(fishingActivityType)) {
                CatchEvolutionProgressHandler catchEvolutionProgressHandler = catchEvolutionProgressRegistry.findHandler(fishingActivityType);
                CatchEvolutionProgressDTO catchEvolutionProgressDTO = catchEvolutionProgressHandler.prepareCatchEvolutionProgressDTO(fishingActivity, speciesCumulatedWeight);

                if (catchEvolutionProgressDTO != null) {
                    catchEvolutionProgressDTO.setOrderId(orderId++);
                    catchEvolutionProgressDTOs.add(catchEvolutionProgressDTO);
                }
            }
        }

        return catchEvolutionProgressDTOs;
    }

}
