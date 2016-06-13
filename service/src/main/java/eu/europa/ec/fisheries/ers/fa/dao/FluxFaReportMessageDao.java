package eu.europa.ec.fisheries.ers.fa.dao;


import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;

import javax.persistence.EntityManager;

/**
 * Created by padhyad on 5/3/2016.
 */
public class FluxFaReportMessageDao extends AbstractDAO<FluxFaReportMessageEntity> {

    private EntityManager em;

    public FluxFaReportMessageDao(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}