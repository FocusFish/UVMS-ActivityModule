package fish.focus.uvms.activity.service.bean;

import fish.focus.uvms.asset.client.AssetClient;
import fish.focus.uvms.asset.client.model.AssetDTO;
import fish.focus.uvms.asset.client.model.search.SearchBranch;
import fish.focus.uvms.activity.fa.entities.VesselIdentifierEntity;
import fish.focus.uvms.activity.service.bean.AssetModuleServiceBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetModuleServiceBeanTest {

    @Mock
    private AssetClient assetClient;

    @InjectMocks
    private AssetModuleServiceBean assetModuleServiceBean;

    @Test
    public void getAssetGuids_withCFR() {
        // Given
        VesselIdentifierEntity vesselIdentifier = new VesselIdentifierEntity();
        vesselIdentifier.setVesselIdentifierId("vessel-id-123");
        vesselIdentifier.setVesselIdentifierSchemeId("cfr");

        List<VesselIdentifierEntity> vesselIdentifiers = new ArrayList<>();
        vesselIdentifiers.add(vesselIdentifier);

        UUID vesselId = UUID.randomUUID();

        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setId(vesselId);
        assetDTO.setCfr("vessel-id-123");

        List<AssetDTO> assetList = new ArrayList<>();
        assetList.add(assetDTO);

        when(assetClient.getAssetList(any(SearchBranch.class))).thenReturn(assetList);

        // When
        List<String> assetGuids = assetModuleServiceBean.getAssetGuids(vesselIdentifiers);

        // Then
        assertEquals(1, assetGuids.size());
        assertEquals(vesselId.toString(), assetGuids.get(0));
    }
}
