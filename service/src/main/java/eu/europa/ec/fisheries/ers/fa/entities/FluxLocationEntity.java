package eu.europa.ec.fisheries.ers.fa.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "activity_flux_location")
public class FluxLocationEntity implements Serializable {

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

	@Column(name = "fishing_activity_id")
	private Long fishingActivityId;

	@Column(name = "type_code", nullable = false)
	private String typeCode;

	@Column(name = "type_code_list_id", nullable = false)
	private String typeCodeListId;

	@Column(name = "country_id")
	private String countryId;

	@Column(name = "rfmo_code")
	private String rfmoCode;

	@Column(name = "latitude", precision = 17, scale = 17)
	private Double latitude;

	@Column(name = "flux_location_type", nullable = false)
	private String fluxLocationType;

	@Column(name = "country_id_scheme_id")
	private String countryIdSchemeId;

	@Column(name = "flux_location_identifier")
	private String fluxLocationIdentifier;

	@Column(name = "flux_location_identifier_scheme_id")
	private String fluxLocationIdentifierSchemeId;

	@Column(name = "geopolitical_region_code")
	private String geopoliticalRegionCode;

	@Column(name = "geopolitical_region_code_list_id")
	private String geopoliticalRegionCodeListId;

	@Column(name = "name")
	private String name;

	@Column(name = "sovereign_rights_country_code")
	private String sovereignRightsCountryCode;

	@Column(name = "jurisdiction_country_code")
	private String jurisdictionCountryCode;

	@Column(name = "altitude", precision = 17, scale = 17)
	private Double altitude;

	@Column(name = "system_id")
	private String systemId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxLocation")
	private Set<FluxCharacteristicEntity> fluxCharacteristics = new HashSet<FluxCharacteristicEntity>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fluxLocation")
	private Set<StructuredAddressEntity> structuredAddresses = new HashSet<StructuredAddressEntity>(0);

	public FluxLocationEntity() {
	}

	public FluxLocationEntity(String typeCode, String typeCodeListId,
							  String fluxLocationType) {
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.fluxLocationType = fluxLocationType;
	}

	public FluxLocationEntity(GearProblemEntity gearProblem,
							  FaCatchEntity faCatch, Long fishingActivityId,
							  String typeCode, String typeCodeListId, String countryId,
							  String rfmoCode, Double latitude, String fluxLocationType,
							  String countryIdSchemeId, String fluxLocationIdentifier,
							  String fluxLocationIdentifierSchemeId,
							  String geopoliticalRegionCode, String geopoliticalRegionCodeListId,
							  String name, String sovereignRightsCountryCode,
							  String jurisdictionCountryCode, Double altitude, String systemId,
							  Set<FluxCharacteristicEntity> fluxCharacteristics,
							  Set<StructuredAddressEntity> structuredAddresses) {
		this.gearProblem = gearProblem;
		this.faCatch = faCatch;
		this.fishingActivityId = fishingActivityId;
		this.typeCode = typeCode;
		this.typeCodeListId = typeCodeListId;
		this.countryId = countryId;
		this.rfmoCode = rfmoCode;
		this.latitude = latitude;
		this.fluxLocationType = fluxLocationType;
		this.countryIdSchemeId = countryIdSchemeId;
		this.fluxLocationIdentifier = fluxLocationIdentifier;
		this.fluxLocationIdentifierSchemeId = fluxLocationIdentifierSchemeId;
		this.geopoliticalRegionCode = geopoliticalRegionCode;
		this.geopoliticalRegionCodeListId = geopoliticalRegionCodeListId;
		this.name = name;
		this.sovereignRightsCountryCode = sovereignRightsCountryCode;
		this.jurisdictionCountryCode = jurisdictionCountryCode;
		this.altitude = altitude;
		this.systemId = systemId;
		this.fluxCharacteristics = fluxCharacteristics;
		this.structuredAddresses = structuredAddresses;
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

	public Long getFishingActivityId() {
		return this.fishingActivityId;
	}

	public void setFishingActivityId(Long fishingActivityId) {
		this.fishingActivityId = fishingActivityId;
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

	public String getCountryId() {
		return this.countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getRfmoCode() {
		return this.rfmoCode;
	}

	public void setRfmoCode(String rfmoCode) {
		this.rfmoCode = rfmoCode;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getFluxLocationType() {
		return this.fluxLocationType;
	}

	public void setFluxLocationType(String fluxLocationType) {
		this.fluxLocationType = fluxLocationType;
	}

	public String getCountryIdSchemeId() {
		return this.countryIdSchemeId;
	}

	public void setCountryIdSchemeId(String countryIdSchemeId) {
		this.countryIdSchemeId = countryIdSchemeId;
	}

	public String getFluxLocationIdentifier() {
		return this.fluxLocationIdentifier;
	}

	public void setFluxLocationIdentifier(String fluxLocationIdentifier) {
		this.fluxLocationIdentifier = fluxLocationIdentifier;
	}

	public String getFluxLocationIdentifierSchemeId() {
		return this.fluxLocationIdentifierSchemeId;
	}

	public void setFluxLocationIdentifierSchemeId(
			String fluxLocationIdentifierSchemeId) {
		this.fluxLocationIdentifierSchemeId = fluxLocationIdentifierSchemeId;
	}

	public String getGeopoliticalRegionCode() {
		return this.geopoliticalRegionCode;
	}

	public void setGeopoliticalRegionCode(String geopoliticalRegionCode) {
		this.geopoliticalRegionCode = geopoliticalRegionCode;
	}

	public String getGeopoliticalRegionCodeListId() {
		return this.geopoliticalRegionCodeListId;
	}

	public void setGeopoliticalRegionCodeListId(
			String geopoliticalRegionCodeListId) {
		this.geopoliticalRegionCodeListId = geopoliticalRegionCodeListId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSovereignRightsCountryCode() {
		return this.sovereignRightsCountryCode;
	}

	public void setSovereignRightsCountryCode(String sovereignRightsCountryCode) {
		this.sovereignRightsCountryCode = sovereignRightsCountryCode;
	}

	public String getJurisdictionCountryCode() {
		return this.jurisdictionCountryCode;
	}

	public void setJurisdictionCountryCode(String jurisdictionCountryCode) {
		this.jurisdictionCountryCode = jurisdictionCountryCode;
	}

	public Double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public String getSystemId() {
		return this.systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public Set<FluxCharacteristicEntity> getFluxCharacteristics() {
		return this.fluxCharacteristics;
	}

	public void setFluxCharacteristics(
			Set<FluxCharacteristicEntity> fluxCharacteristics) {
		this.fluxCharacteristics = fluxCharacteristics;
	}

	public Set<StructuredAddressEntity> getStructuredAddresses() {
		return this.structuredAddresses;
	}

	public void setStructuredAddresses(
			Set<StructuredAddressEntity> StructuredAddresses) {
		this.structuredAddresses = structuredAddresses;
	}

}