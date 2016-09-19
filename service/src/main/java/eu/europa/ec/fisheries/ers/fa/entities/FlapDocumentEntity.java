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

package eu.europa.ec.fisheries.ers.fa.entities;

import javax.persistence.*;

/**
 * Created by padhyad on 9/16/2016.
 */
@Entity
@Table(name = "activity_flap_document")
public class FlapDocumentEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vessel_transport_means_id")
    private VesselTransportMeansEntity vesselTransportMeans;

    @Column(name = "flap_document_id")
    private String flapDocumentId;

    @Column(name = "flap_document_scheme_id")
    private String flapDocumentSchemeId;

    public int getId() {
        return id;
    }

    public VesselTransportMeansEntity getVesselTransportMeans() {
        return vesselTransportMeans;
    }

    public void setVesselTransportMeans(VesselTransportMeansEntity vesselTransportMeans) {
        this.vesselTransportMeans = vesselTransportMeans;
    }

    public String getFlapDocumentId() {
        return flapDocumentId;
    }

    public void setFlapDocumentId(String flapDocumentId) {
        this.flapDocumentId = flapDocumentId;
    }

    public String getFlapDocumentSchemeId() {
        return flapDocumentSchemeId;
    }

    public void setFlapDocumentSchemeId(String flapDocumentSchemeId) {
        this.flapDocumentSchemeId = flapDocumentSchemeId;
    }
}