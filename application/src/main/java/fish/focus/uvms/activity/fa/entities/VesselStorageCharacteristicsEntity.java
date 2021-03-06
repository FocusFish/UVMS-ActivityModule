/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.activity.fa.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "activity_vessel_storage_characteristics")
@Data
@EqualsAndHashCode(exclude = {"fishingActivitiesForDestVesselCharId", "fishingActivitiesForSourceVesselCharId", "vesselStorageCharCode"})
@ToString(exclude = {"fishingActivitiesForDestVesselCharId", "fishingActivitiesForSourceVesselCharId", "vesselStorageCharCode"})
@NoArgsConstructor
public class VesselStorageCharacteristicsEntity implements Serializable {

	@Id
	@Column(unique = true, nullable = false)
    @SequenceGenerator(name = "SEQ_GEN", sequenceName = "str_char_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    private int id;

	@Column(name = "vessel_id")
	private String vesselId;

	@Column(name = "vessel_scheme_id")
	private String vesselSchemaId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "destVesselCharId")
	private FishingActivityEntity fishingActivitiesForDestVesselCharId;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "sourceVesselCharId")
	private FishingActivityEntity fishingActivitiesForSourceVesselCharId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "vesselStorageCharacteristics", cascade = CascadeType.ALL)
	private Set<VesselStorageCharCodeEntity> vesselStorageCharCode;

    public VesselStorageCharCodeEntity getFirstVesselStorageCharCode() {
        VesselStorageCharCodeEntity firstElement = null;
        if (!CollectionUtils.isEmpty(vesselStorageCharCode)) {
            firstElement = this.vesselStorageCharCode.iterator().next();
        }
        return firstElement;
    }
}
