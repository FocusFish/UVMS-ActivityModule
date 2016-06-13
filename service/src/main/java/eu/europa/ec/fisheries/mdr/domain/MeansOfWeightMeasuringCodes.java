package eu.europa.ec.fisheries.mdr.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.FieldType;

/**
 * @author kovian
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "means_of_weight_measuring_codes")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MeansOfWeightMeasuringCodes extends MasterDataRegistry {

	
	@Override
	public String getAcronym() {
		return "MEANS_OF_WEIGHT_MEASURING";
	}
	
	@Override
	public void populate(List<FieldType> fields) {
		for(FieldType field : fields){
			String fieldName  = field.getFieldName().getValue();
			String fieldValue = field.getFieldValue().getValue();
			if(StringUtils.equalsIgnoreCase(fieldName, "")){
			} else if(StringUtils.equalsIgnoreCase(fieldName, "")){
			}
		}	
	}
}