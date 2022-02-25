package fish.focus.uvms.activity.service.facatch.evolution;

import org.apache.commons.lang3.EnumUtils;
import fish.focus.uvms.activity.fa.entities.FaCatchEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.utils.FaReportDocumentType;
import fish.focus.uvms.activity.model.schemas.FaCatchTypeEnum;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import java.util.Arrays;
import java.util.Map;

public class RLCCatchEvolutionProgressHandler extends CatchEvolutionProgressHandler {
    @Override
    public CatchEvolutionProgressDTO prepareCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, Map<String, Double> speciesCumulatedWeight) {
        FaReportDocumentType faReportDocumentType = EnumUtils.getEnum(FaReportDocumentType.class, fishingActivity.getFaReportDocument().getTypeCode());

        if (faReportDocumentType != null && faReportDocumentType != FaReportDocumentType.DECLARATION && faReportDocumentType != FaReportDocumentType.NOTIFICATION) {
            return null;
        }

        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivity, faReportDocumentType, speciesCumulatedWeight);

        for (FaCatchEntity faCatch : fishingActivity.getFaCatchs()) {
            FaCatchTypeEnum faCatchType = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());
            boolean loadedXorUnloaded = (faCatchType == FaCatchTypeEnum.LOADED && !isFaCatchTypePresent(fishingActivity.getFaCatchs(), FaCatchTypeEnum.UNLOADED)) ||
                    (faCatchType == FaCatchTypeEnum.UNLOADED && !isFaCatchTypePresent(fishingActivity.getFaCatchs(), FaCatchTypeEnum.LOADED));

            if (loadedXorUnloaded) {
                handleOnboardCatch(faCatch, catchEvolutionProgressDTO);
            }

            if (faReportDocumentType == FaReportDocumentType.DECLARATION) {
                if (faCatchType == FaCatchTypeEnum.UNLOADED) {
                    handleCumulatedCatchWithDeletion(faCatch, catchEvolutionProgressDTO, speciesCumulatedWeight, Arrays.asList(FaCatchTypeEnum.UNLOADED));
                } else if (faCatchType == FaCatchTypeEnum.LOADED) {
                    handleCumulatedCatchNoDeletion(faCatch, catchEvolutionProgressDTO, speciesCumulatedWeight);
                }
            }
        }

        return catchEvolutionProgressDTO;
    }
}
