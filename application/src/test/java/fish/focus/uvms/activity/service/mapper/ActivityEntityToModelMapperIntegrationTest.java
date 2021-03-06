/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package fish.focus.uvms.activity.service.mapper;

import fish.focus.uvms.activity.TransactionalTests;
import fish.focus.uvms.commons.message.impl.JAXBUtils;
import fish.focus.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import fish.focus.uvms.activity.fa.utils.FaReportSourceEnum;
import fish.focus.uvms.activity.model.mapper.FANamespaceMapper;
import fish.focus.uvms.activity.service.mapper.ActivityEntityToModelMapper;
import fish.focus.uvms.activity.service.mapper.FluxFaReportMessageMapper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;

@RunWith(Arquillian.class)
public class ActivityEntityToModelMapperIntegrationTest extends TransactionalTests {

    @Inject
    FluxFaReportMessageMapper fluxFaReportMessageMapper;
    
    @Inject
    ActivityEntityToModelMapper activityEntityToModelMapper;

    @Test
    public void testMapToFLUXFAReportMessage() throws Exception {

        for(String resource : resources()) {

            FLUXFAReportMessage fluxfaReportMessage = getMessageFromTestResource(resource);
            FluxFaReportMessageEntity entity = fluxFaReportMessageMapper.mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.MANUAL);

            FLUXFAReportMessage target = activityEntityToModelMapper.mapToFLUXFAReportMessage(new ArrayList<>(entity.getFaReportDocuments()));

            String controlSource = JAXBUtils.marshallJaxBObjectToString(getFirstElement(fluxfaReportMessage), "ISO-8859-15", true, new FANamespaceMapper());
            String testSource = JAXBUtils.marshallJaxBObjectToString(getFirstElement(target), "ISO-8859-15", true, new FANamespaceMapper());

            String clearControlSource = clearEmptyTags(controlSource);
            String clearTestSource = clearEmptyTags(testSource);

            List<String> doubleValueNodeList = new ArrayList<>();
            doubleValueNodeList.add("ValueMeasure");
            doubleValueNodeList.add("WeightMeasure");

            Diff myDiffSimilar = DiffBuilder
                    .compare(clearControlSource)
                    .withTest(clearTestSource)
                    .withNodeFilter(node -> (!node.getNodeName().equalsIgnoreCase("ram:SpecifiedDelimitedPeriod") && !node.getNodeName().equalsIgnoreCase("ram:TypeCode")))
                    .ignoreWhitespace()
                    .ignoreComments()
                    .checkForSimilar()
                    .withNodeMatcher(
                            new DefaultNodeMatcher(ElementSelectors.and(
                                    ElementSelectors.byNameAndText,
                                    ElementSelectors.byNameAndAllAttributes)))
                    .build();

            assertFalse("XML similar " + myDiffSimilar.toString(), myDiffSimilar.hasDifferences());

        }
    }

    public static String[] resources() {
        return new String[] {
                "fa_flux_message4.xml",
                "fa_flux_message5.xml",
                "fa_flux_message6.xml",
                "fa_flux_message7.xml",
                "fa_flux_message8.xml",
                "UNFA_IRCS6_14_ARRIVAL_NOT_ESFNE_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_15_CANCEL_ARRIVAL_NOT_ESFNE_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_19_LANDING_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_18_ARRIVAL_DCL_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_07_DELETE_FOP4_withError_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_09_EXIT_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS5_08B_TRA-LOAD_EST-TRP-20170531000000000001.xml",
                "UNFA_IRCS4_08_ARRIVAL_DCL_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS4_07_ARRIVAL_NOT_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS4_06_BFT-TRANSFER_DECL_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS4_04_BFT-TRANSFER_NOT_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS6_08A_TRA-UNL_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_01_DEPARTURE_COB_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_02_FOP1_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_03_ENTRY_CYP-TRP-20170608000000000010.xml",
                //"UNFA_IRCS6_04_FOP2PAIR_CYP-TRP-20170608000000000010.xml",
                "multipleReports.xml",
                "multipleReports2.xml"
        };
    }

    private String clearEmptyTags(String testSource) {
        testSource = testSource
                .replaceAll("<([a-zA-Z][a-zA-Z0-9]*)[^>]*/>", "") // clear tags like  <udt:IndicatorString/>
                .replaceAll("\n?\\s*<(\\w+)></\\1>", "")
                .replaceAll("<ram:SpecifiedPhysicalFLUXGeographicalCoordinate>\\s*</ram:SpecifiedPhysicalFLUXGeographicalCoordinate>", "")
                .replaceAll("<ram:ValueIndicator>\\s*</ram:ValueIndicator>","")
                .replaceAll("(?m)^[ \t]*\r?\n", "")// clear empty lines
                .replace(".0", ""); // Ok, so this is pretty dumb but our conversion from unit to double leaves trailing .0, for this test remove them.

        return testSource;
    }

    private FAReportDocument getFirstElement(FLUXFAReportMessage source) {
        return source.getFAReportDocuments().get(0);
    }

    private FLUXFAReportMessage getMessageFromTestResource(String fileName) throws IOException, JAXBException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = contextClassLoader.getResourceAsStream(fileName)) {
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                BufferedReader reader = new BufferedReader(isr);
                String fileAsString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                return JAXBUtils.unMarshallMessage(fileAsString, FLUXFAReportMessage.class);
            }
        }
    }

}
