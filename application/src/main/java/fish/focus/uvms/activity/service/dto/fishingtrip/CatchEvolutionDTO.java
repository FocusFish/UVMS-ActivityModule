package fish.focus.uvms.activity.service.dto.fishingtrip;

import javax.json.bind.annotation.JsonbProperty;
import fish.focus.uvms.activity.service.dto.view.TripWidgetDto;
import java.util.List;

public class CatchEvolutionDTO {
    @JsonbProperty("catchProgress")
    private List<CatchEvolutionProgressDTO> catchEvolutionProgress;
    @JsonbProperty("tripDetails")
    private TripWidgetDto tripDetails;

    public List<CatchEvolutionProgressDTO> getCatchEvolutionProgress() {
        return catchEvolutionProgress;
    }

    public void setCatchEvolutionProgress(List<CatchEvolutionProgressDTO> catchEvolutionProgress) {
        this.catchEvolutionProgress = catchEvolutionProgress;
    }

    public TripWidgetDto getTripDetails() {
        return tripDetails;
    }

    public void setTripDetails(TripWidgetDto tripDetails) {
        this.tripDetails = tripDetails;
    }
}
