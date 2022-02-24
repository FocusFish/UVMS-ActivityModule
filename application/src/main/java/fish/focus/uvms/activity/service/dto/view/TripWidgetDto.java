/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/

package fish.focus.uvms.activity.service.dto.view;

import javax.json.bind.annotation.JsonbProperty;
import fish.focus.uvms.activity.service.dto.FlapDocumentDto;
import fish.focus.uvms.activity.service.dto.fareport.details.VesselDetailsDTO;
import java.util.List;
import java.util.Set;

public class TripWidgetDto {

    private VesselDetailsDTO vesselDetails;

    private Set<FlapDocumentDto> flapDocuments;

    private List<TripOverviewDto> trips;

    public VesselDetailsDTO getVesselDetails() {
        return vesselDetails;
    }

    public void setVesselDetails(VesselDetailsDTO vesselDetails) {
        this.vesselDetails = vesselDetails;
    }

    public List<TripOverviewDto> getTrips() {
        return trips;
    }

    public void setTrips(List<TripOverviewDto> trips) {
        this.trips = trips;
    }

    @JsonbProperty("authorizations")
    public Set<FlapDocumentDto> getFlapDocuments() {
        return flapDocuments;
    }

    public void setFlapDocuments(Set<FlapDocumentDto> flapDocuments) {
        this.flapDocuments = flapDocuments;
    }
}
