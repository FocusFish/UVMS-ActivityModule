/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package fish.focus.uvms.activity.service.dto;

import lombok.EqualsAndHashCode;

import javax.json.bind.annotation.JsonbProperty;
import fish.focus.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import fish.focus.uvms.activity.service.dto.view.IdentifierDto;

@EqualsAndHashCode(callSuper = true)
public class AssetIdentifierDto extends IdentifierDto {

    @JsonbProperty("fromAssets")
    private Boolean fromAssets = false;

    @JsonbProperty("vesselSchemeId")
    private VesselIdentifierSchemeIdEnum identifierSchemeId;

    public AssetIdentifierDto() {
    }

    public AssetIdentifierDto(VesselIdentifierSchemeIdEnum identifierSchemeId, String faIdentifierId) {
        this.identifierSchemeId = identifierSchemeId;
        this.setFaIdentifierId(faIdentifierId);
    }

    public AssetIdentifierDto(VesselIdentifierSchemeIdEnum identifierSchemeId) {
        this.identifierSchemeId = identifierSchemeId;
    }

    public Boolean isFromAssets() {
        return fromAssets;
    }

    public void setFromAssets(Boolean fromAssets) {
        this.fromAssets = fromAssets;
    }

    public VesselIdentifierSchemeIdEnum getIdentifierSchemeId() {
        return identifierSchemeId;
    }

    public void setIdentifierSchemeId(VesselIdentifierSchemeIdEnum identifierSchemeId) {
        this.identifierSchemeId = identifierSchemeId;
    }
}
