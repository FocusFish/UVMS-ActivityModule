package fish.focus.uvms.activity.service;

import java.util.List;
import java.util.Map;

public interface MdrModuleService {

    /**
     * API to call MDR Module by JMS and get the VesselIdentifier type code
     *
     * @param acronym as a pramter
     * @return list of vesselidentifiertype codes
     */
    Map<String, List<String>> getAcronymFromMdr(String acronym, String filter, List<String> columns, Integer nrOfResults, String... returnColumns);
}
