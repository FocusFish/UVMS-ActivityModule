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
import fish.focus.uvms.activity.fa.entities.ContactPersonEntity;
import fish.focus.uvms.activity.service.dto.fareport.details.ContactPersonDetailsDTO;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.ContactPerson;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ContactPersonMapper {

    @Mapping(target = "title", source = "title.value")
    @Mapping(target = "givenName", source = "givenName.value")
    @Mapping(target = "middleName", source = "middleName.value")
    @Mapping(target = "familyName", source = "familyName.value")
    @Mapping(target = "familyNamePrefix", source = "familyNamePrefix.value")
    @Mapping(target = "nameSuffix", source = "nameSuffix.value")
    @Mapping(target = "gender", source = "genderCode.value")
    @Mapping(target = "alias", source = "alias.value")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contactParty", ignore = true)
    ContactPersonEntity mapToContactPersonEntity(ContactPerson contactPerson);

    @InheritInverseConfiguration
    @Mapping(target = "birthDateTime", ignore = true)
    @Mapping(target = "birthplaceName", ignore = true)
    @Mapping(target = "telephoneTelecommunicationCommunication", ignore = true)
    @Mapping(target = "faxTelecommunicationCommunication", ignore = true)
    @Mapping(target = "emailURIEmailCommunication", ignore = true)
    @Mapping(target = "websiteURIWebsiteCommunication", ignore = true)
    @Mapping(target = "specifiedUniversalCommunications", ignore = true)
    ContactPerson mapToContactPerson(ContactPersonEntity contactPerson);

    @Mapping(target = "characteristicsMap", ignore = true)
    ContactPersonDetailsDTO mapToContactPersonDetailsDTO(ContactPersonEntity contactPersonEntity);
}
