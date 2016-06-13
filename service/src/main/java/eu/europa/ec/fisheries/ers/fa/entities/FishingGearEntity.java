package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_fishing_gear")
public class FishingGearEntity implements Serializable {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gear_problem_id")
	private GearProblemEntity gearProblem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fa_catch_id")
	private FaCatchEntity faCatch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fishing_activity_id")
	private FishingActivityEntity fishingActivity;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "role_code", nullable = false)
	private String roleCode;

	@Column(name = "role_code_list_id", nullable = false)
	private String roleCodeListId;


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingGear")
	private Set<GearCharacteristicEntity> gearCharacteristics = new HashSet<GearCharacteristicEntity>(0);

	public FishingGearEntity() {
	}

	public FishingGearEntity(String typeCode, String typeCodeListId,
							 String roleCode, String roleCodeListId) {
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
	}

	public FishingGearEntity(GearProblemEntity gearProblem,
							 FaCatchEntity faCatch,
							 FishingActivityEntity fishingActivity, String typeCode,
							 String typeCodeListId, String roleCode, String roleCodeListId,
							 Set<GearCharacteristicEntity> gearCharacteristics) {
		this.gearProblem = gearProblem;
		this.faCatch = faCatch;
		this.fishingActivity = fishingActivity;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.roleCode = roleCode;
		this.roleCodeListId = roleCodeListId;
		this.gearCharacteristics = gearCharacteristics;
	}


	public int getId() {
		return this.id;
	}

	public GearProblemEntity getGearProblem() {
		return this.gearProblem;
	}

	public void setGearProblem(GearProblemEntity gearProblem) {
		this.gearProblem = gearProblem;
	}

	public FaCatchEntity getFaCatch() {
		return this.faCatch;
	}

	public void setFaCatch(FaCatchEntity faCatch) {
		this.faCatch = faCatch;
	}

	public FishingActivityEntity getFishingActivity() {
		return this.fishingActivity;
	}

	public void setFishingActivity(
			FishingActivityEntity fishingActivity) {
		this.fishingActivity = fishingActivity;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeCodeListId() {
		return this.typeCodeListId;
	}

	public void setTypeCodeListId(String typeCodeListId) {
		this.typeCodeListId = typeCodeListId;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleCodeListId() {
		return this.roleCodeListId;
	}

	public void setRoleCodeListId(String roleCodeListId) {
		this.roleCodeListId = roleCodeListId;
	}

	public Set<GearCharacteristicEntity> getGearCharacteristics() {
		return this.gearCharacteristics;
	}

	public void setGearCharacteristics(
			Set<GearCharacteristicEntity> gearCharacteristics) {
		this.gearCharacteristics = gearCharacteristics;
	}

}