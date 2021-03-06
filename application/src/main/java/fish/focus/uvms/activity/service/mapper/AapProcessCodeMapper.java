/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package fish.focus.uvms.activity.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import fish.focus.uvms.activity.fa.entities.AapProcessCodeEntity;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.CodeType;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AapProcessCodeMapper {

    @Mapping(target = "value", source = "typeCode")
    @Mapping(target = "listID", source = "typeCodeListId")
    @Mapping(target = "listAgencyID", ignore = true)
    @Mapping(target = "listAgencyName", ignore = true)
    @Mapping(target = "listName", ignore = true)
    @Mapping(target = "listVersionID", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "languageID", ignore = true)
    @Mapping(target = "listURI", ignore = true)
    @Mapping(target = "listSchemeURI", ignore = true)
    CodeType mapToCodeType(AapProcessCodeEntity aapProcessCodeEntity);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aapProcess", ignore = true)
    AapProcessCodeEntity mapToAapProcessCodeEntity(CodeType codeType);
}
