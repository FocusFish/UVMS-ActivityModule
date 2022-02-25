package fish.focus.uvms.activity.service.facatch.evolution;

import org.apache.commons.lang3.EnumUtils;
import fish.focus.uvms.activity.fa.entities.FaCatchEntity;
import fish.focus.uvms.activity.fa.entities.FishingActivityEntity;
import fish.focus.uvms.activity.fa.utils.FaReportDocumentType;
import fish.focus.uvms.activity.model.schemas.FaCatchTypeEnum;
import fish.focus.uvms.activity.service.dto.fishingtrip.CatchEvolutionProgressDTO;
import java.util.Map;

public class ENTRYCatchEvolutionProgressHandler extends CatchEvolutionProgressHandler {
    @Override
    public CatchEvolutionProgressDTO prepareCatchEvolutionProgressDTO(FishingActivityEntity fishingActivity, Map<String, Double> speciesCumulatedWeight) {
        FaReportDocumentType faReportDocumentType = EnumUtils.getEnum(FaReportDocumentType.class, fishingActivity.getFaReportDocument().getTypeCode());

        if (faReportDocumentType != null && faReportDocumentType != FaReportDocumentType.DECLARATION && faReportDocumentType != FaReportDocumentType.NOTIFICATION) {
            return null;
        }

        CatchEvolutionProgressDTO catchEvolutionProgressDTO = initCatchEvolutionProgressDTO(fishingActivity, faReportDocumentType, speciesCumulatedWeight);
        boolean cumulativeCatchContainsWeight = new WeightCalculator(speciesCumulatedWeight).containsWeight();

        for (FaCatchEntity faCatch : fishingActivity.getFaCatchs()) {
            FaCatchTypeEnum faCatchType = EnumUtils.getEnum(FaCatchTypeEnum.class, faCatch.getTypeCode());

            if (faCatchType == FaCatchTypeEnum.ONBOARD || faCatchType == FaCatchTypeEnum.KEPT_IN_NET || faCatchType == FaCatchTypeEnum.BY_CATCH) {
                handleOnboardCatch(faCatch, catchEvolutionProgressDTO);

                if (!cumulativeCatchContainsWeight) {
                    handleCumulatedCatchNoDeletion(faCatch, catchEvolutionProgressDTO, speciesCumulatedWeight);
                }
            }
        }

        return catchEvolutionProgressDTO;
    }

    private class WeightCalculator {
        private Map<String, Double> speciesCumulatedWeight;

        public WeightCalculator(Map<String, Double> speciesCumulatedWeight) {
            this.speciesCumulatedWeight = speciesCumulatedWeight;
        }

        public boolean containsWeight() {
            for (Map.Entry<String, Double> entry : speciesCumulatedWeight.entrySet()) {
                if (entry.getValue() != null && entry.getValue() > 0) {
                    return true;
                }
            }

            return false;
        }

        public Double getTotalWeight() {
            double totalWeight = 0;

            for (Map.Entry<String, Double> entry : speciesCumulatedWeight.entrySet()) {
                totalWeight += entry.getValue() != null ? entry.getValue().doubleValue() : 0;
            }

            return Double.valueOf(totalWeight);
        }
    }
}
