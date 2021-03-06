/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package fish.focus.uvms.activity.service.dto;

import javax.json.bind.annotation.JsonbTransient;
import fish.focus.uvms.activity.service.dto.view.IdentifierDto;

public class FlapDocumentDto extends IdentifierDto {

    @JsonbTransient
    private int id;

    @JsonbTransient
    private String flapTypeCode;

    @JsonbTransient
    private String flapTypeCodeListId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlapTypeCode() {
        return flapTypeCode;
    }

    public void setFlapTypeCode(String flapTypeCode) {
        this.flapTypeCode = flapTypeCode;
    }

    public String getFlapTypeCodeListId() {
        return flapTypeCodeListId;
    }

    public void setFlapTypeCodeListId(String flapTypeCodeListId) {
        this.flapTypeCodeListId = flapTypeCodeListId;
    }
}
