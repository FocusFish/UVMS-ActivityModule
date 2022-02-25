package fish.focus.uvms.activity.service.facatch.evolution;

import org.apache.commons.lang3.EnumUtils;
import fish.focus.uvms.activity.fa.entities.FaCatchEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.utils.FaReportDocumentType;
import fish.focus.uvms.activity.model.schemas.FaCatchTypeEnum;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import java.util.Map;

public class ARRCatchEvolutionProgressHandler extends CatchEvolutionProgressHandler {
    @Override
    public CatchEvolutionProgressDTO prepareCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, Map<String, Double> speciesCumulatedWeight) {
        FaReportDocumentType faReportDocumentType = EnumUtils.getEnum(FaReportDocumentType.class, fishingActivity.getFaReportDocument().getTypeCode());

        if (faReportDocumentType != null && faReportDocumentType != FaReportDocumentType.NOTIFICATION) {
            return null;
        }

        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivity, faReportDocumentType, speciesCumulatedWeight);

        for (FaCatchEntity faCatch : fishingActivity.getFaCatchs()) {
            FaCatchTypeEnum faCatchType = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());
            boolean onboardXorUnloaded = (faCatchType == FaCatchTypeEnum.ONBOARD && !isFaCatchTypePresent(fishingActivity.getFaCatchs(), FaCatchTypeEnum.UNLOADED)) ||
                    (faCatchType == FaCatchTypeEnum.UNLOADED && !isFaCatchTypePresent(fishingActivity.getFaCatchs(), FaCatchTypeEnum.ONBOARD));

            if (onboardXorUnloaded) {
                handleOnboardCatch(faCatch, catchEvolutionProgressDTO);
            }
        }

        return catchEvolutionProgressDTO;
    }
}
