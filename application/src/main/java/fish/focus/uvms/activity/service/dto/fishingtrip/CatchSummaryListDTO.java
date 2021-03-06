/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package fish.focus.uvms.activity.service.dto.fishingtrip;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.List;

public class CatchSummaryListDTO {

    @JsonbProperty("speciesList")
    private List<SpeciesQuantityDTO> speciesList;

    @JsonbProperty("total")
    private Double total;

    public CatchSummaryListDTO(){
        this.speciesList = new ArrayList<>();
        total = 0.0;
    }

    @JsonbTransient
    public void addSpecieAndQuantity(String speciesCode, Double weight,String areaName){
        SpeciesQuantityDTO speciesQuantityDTO = new SpeciesQuantityDTO(speciesCode);
        if(speciesList.contains(speciesQuantityDTO)){
            int index= speciesList.indexOf(speciesQuantityDTO);
            SpeciesQuantityDTO existingObject= speciesList.get(index);
            existingObject.addToAreaInfo( areaName,weight );
            speciesList.set(index,existingObject);
        }else {
            speciesQuantityDTO.addToAreaInfo( areaName,weight );
            speciesList.add(speciesQuantityDTO);
        }
        this.setTotal(total + weight);
    }

    public List<SpeciesQuantityDTO> getSpeciesList() {
        return speciesList;
    }
    public void setSpeciesList(List<SpeciesQuantityDTO> speciesList) {
        this.speciesList = speciesList;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
}
