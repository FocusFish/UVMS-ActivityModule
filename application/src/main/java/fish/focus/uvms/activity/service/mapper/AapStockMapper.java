/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.activity.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import fish.focus.uvms.activity.fa.entities.AapStockEntity;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPStock;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AapStockMapper {

    @Mapping(target = "stockId", source = "ID.value")
    @Mapping(target = "stockSchemeId", source = "ID.schemeID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faCatch", ignore = true)
    AapStockEntity mapToAapStockEntity(AAPStock aapStock);
}
