/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package fish.focus.uvms.activity.service.bean;

import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import fish.focus.uvms.activity.fa.dao.FaReportDocumentDao;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.service.FaQueryService;
import fish.focus.uvms.activity.service.mapper.ActivityEntityToModelMapper;
import java.time.Instant;
import java.util.List;

@Stateless
@Slf4j
public class FAQueryServiceBean extends BaseActivityBean implements FaQueryService {

    private FaReportDocumentDao faReportDao;

    @Inject
    ActivityEntityToModelMapper activityEntityToModelMapper;

    @PostConstruct
    public void init() {
        faReportDao = new FaReportDocumentDao(entityManager);
    }

    @Override
    public FLUXFAReportMessage getReportsByCriteria(Instant startDate, Instant endDate) {
        List<FaReportDocumentEntity> faReportDocumentsForTrip = faReportDao.loadReports(null, "N", startDate, endDate);
        return activityEntityToModelMapper.mapToFLUXFAReportMessage(faReportDocumentsForTrip);
    }
}
