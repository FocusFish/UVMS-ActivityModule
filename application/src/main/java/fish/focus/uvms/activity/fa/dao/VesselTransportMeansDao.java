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

package fish.focus.uvms.activity.fa.dao;

import org.apache.commons.collections.CollectionUtils;
import fish.focus.uvms.activity.fa.entities.VesselTransportMeansEntity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class VesselTransportMeansDao {

    private EntityManager em;

    public VesselTransportMeansDao(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public VesselTransportMeansEntity findLatestVesselByTripId(String tripId) {
        VesselTransportMeansEntity vesselTransportMeansEntity = null;

        TypedQuery<VesselTransportMeansEntity> query = em.createNamedQuery(VesselTransportMeansEntity.FIND_LATEST_VESSEL_BY_TRIP_ID, VesselTransportMeansEntity.class);
        query.setParameter("tripId", tripId);
        query.setMaxResults(1);
        List<VesselTransportMeansEntity> byNamedQuery = query.getResultList();
        if (!CollectionUtils.isEmpty(byNamedQuery)) {
            vesselTransportMeansEntity = byNamedQuery.get(0);
        }
        return vesselTransportMeansEntity;
    }

}
