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
import fish.focus.uvms.activity.fa.entities.AapProcessEntity;
import fish.focus.uvms.activity.fa.entities.AapProductEntity;
import fish.focus.uvms.activity.fa.entities.LocationEntity;
import fish.focus.uvms.activity.fa.utils.LocationEnum;
import fish.focus.uvms.activity.service.dto.view.ProcessingProductsDto;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.AAPProduct;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class AapProductMapper {

    public static final String FISH_PRESENTATION = "FISH_PRESENTATION";

    public static final String FISH_PRESERVATION = "FISH_PRESERVATION";

    public static final String FISH_FRESHNESS = "FISH_FRESHNESS";


    @Mapping(target = "packagingTypeCode", source = "packagingTypeCode.value")
    @Mapping(target = "packagingTypeCodeListId", source = "packagingTypeCode.listID")
    @Mapping(target = "packagingUnitAverageWeight", source = "packagingUnitAverageWeightMeasure.value")
    @Mapping(target = "packagingWeightUnitCode", source = "packagingUnitAverageWeightMeasure.unitCode")
    @Mapping(target = "packagingUnitCount", source = "packagingUnitQuantity.value")
    @Mapping(target = "packagingUnitCountCode", source = "packagingUnitQuantity.unitCode")
    @Mapping(target = "speciesCode", source = "speciesCode.value")
    @Mapping(target = "speciesCodeListId", source = "speciesCode.listID")
    @Mapping(target = "unitQuantity", source = "unitQuantity.value")
    @Mapping(target = "unitQuantityCode", source = "unitQuantity.unitCode")
    @Mapping(target = "weightMeasure", source = "weightMeasure.value")
    @Mapping(target = "weightMeasureUnitCode", source = "weightMeasure.unitCode")
    @Mapping(target = "weighingMeansCode", source = "weighingMeansCode.value")
    @Mapping(target = "weighingMeansCodeListId", source = "weighingMeansCode.listID")
    @Mapping(target = "usageCode", source = "usageCode.value")
    @Mapping(target = "usageCodeListId", source = "usageCode.listID")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "calculatedPackagingWeight", ignore = true)
    @Mapping(target = "calculatedPackagingUnitCount", ignore = true)
    @Mapping(target = "calculatedUnitQuantity", ignore = true)
    @Mapping(target = "calculatedWeightMeasure", ignore = true)
    @Mapping(target = "aapProcess", ignore = true)
    public abstract AapProductEntity mapToAapProductEntity(AAPProduct aapProduct);


    @InheritInverseConfiguration
    @Mapping(target = "totalSalesPrice", ignore = true)
    @Mapping(target = "specifiedSizeDistribution", ignore = true)
    @Mapping(target = "originFishingActivity", ignore = true)
    @Mapping(target = "appliedAAPProcesses", ignore = true)
    @Mapping(target = "originFLUXLocations", ignore = true)
    public abstract AAPProduct mapToAapProduct(AapProductEntity aapProductEntity);


    @Mapping(target = "type", source = "aapProcess.faCatch.typeCode")
    @Mapping(target = "locations", expression = "java(getDenormalizedLocations(aapProduct))")
    @Mapping(target = "species", source = "aapProcess.faCatch.speciesCode")
    @Mapping(target = "gear", source = "aapProcess.faCatch.gearTypeCode")
    @Mapping(target = "presentation", expression = "java(getAapProcessTypeByCode(aapProduct.getAapProcess(), FISH_PRESENTATION))")
    @Mapping(target = "preservation", expression = "java(getAapProcessTypeByCode(aapProduct.getAapProcess(), FISH_PRESERVATION))")
    @Mapping(target = "freshness", expression = "java(getAapProcessTypeByCode(aapProduct.getAapProcess(), FISH_FRESHNESS))")
    @Mapping(target = "conversionFactor", source = "aapProcess.conversionFactor")
    @Mapping(target = "weight", source = "weightMeasure")
    @Mapping(target = "quantity", source = "unitQuantity")
    @Mapping(target = "packageWeight", source = "packagingUnitAverageWeight")
    @Mapping(target = "packageQuantity", source = "packagingUnitCount")
    @Mapping(target = "packagingType", source = "packagingTypeCode")
    public abstract ProcessingProductsDto mapToProcessingProduct(AapProductEntity aapProduct);

    protected Map<String, String> getDenormalizedLocations(AapProductEntity aapProduct) {
        Map<String, String> locations = new HashMap<>();
        for(LocationEntity location : aapProduct.getAapProcess().getFaCatch().getLocations()) {
            if(location.getTypeCode().equals(LocationEnum.AREA)) {
                locations.put(location.getFluxLocationIdentifierSchemeId(), location.getFluxLocationIdentifier());
            }
        }
        return locations;
    }

    protected String getAapProcessTypeByCode(AapProcessEntity aapProcess, String typeCode) {
        String fishPresentation = null;
        for(AapProcessCodeEntity aapProcessCode : aapProcess.getAapProcessCode()) {
            if (aapProcessCode.getTypeCodeListId().equalsIgnoreCase(typeCode)) {
                fishPresentation = aapProcessCode.getTypeCode();
            }
        }
        return fishPresentation;
    }
}
