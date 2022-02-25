/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package fish.focus.uvms.activity.service.bean;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import fish.focus.uvms.activity.fa.dao.FaReportDocumentDao;
import fish.focus.uvms.activity.fa.entities.FaReportDocumentEntity;
import fish.focus.uvms.activity.model.schemas.ActivityIDType;
import fish.focus.uvms.activity.model.schemas.ActivityTableType;
import fish.focus.uvms.activity.service.bean.ActivityMatchingIdsService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityMatchingIdsServiceTest {

    @InjectMocks
    private ActivityMatchingIdsService matchingBean;

    @Mock
    private FaReportDocumentDao faReportDocumentDao;

    @Test
    public void getMatchingIds_simpleListOfIds() {
        // Given
        when(faReportDocumentDao.getMatchingIdentifiers(anyList(), Mockito.any(ActivityTableType.class))).thenReturn(getMockedIdentifiers());

        List<ActivityIDType> activityIDTypes = new ArrayList<>();
        activityIDTypes.add(new ActivityIDType("46DCC44C-0AE2-434C-BC14-B85D86B29512iiiii", "scheme-idvv"));
        activityIDTypes.add(new ActivityIDType("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb", "scheme-idgg"));

        // When
        List<ActivityIDType> matchingIds = matchingBean.getMatchingIds(activityIDTypes, ActivityTableType.RELATED_FLUX_REPORT_DOCUMENT_ENTITY);

        // Then
        assertEquals(2, matchingIds.size());
    }

    private List<FaReportDocumentEntity> getMockedIdentifiers() {
        FaReportDocumentEntity faReportDocumentEntity1 = new FaReportDocumentEntity();
        FaReportDocumentEntity faReportDocumentEntity2 = new FaReportDocumentEntity();
        faReportDocumentEntity1.setFluxReportDocument_Id("46DCC44C-0AE2-434C-BC14-B85D86B29512iiiii");
        faReportDocumentEntity1.setFluxReportDocument_IdSchemeId("scheme-idvv");
        faReportDocumentEntity2.setFluxReportDocument_Id("46DCC44C-0AE2-434C-BC14-B85D86B29512bbbbb");
        faReportDocumentEntity2.setFluxReportDocument_IdSchemeId("scheme-idqq");
        return Arrays.asList(faReportDocumentEntity1, faReportDocumentEntity2);
    }
}
