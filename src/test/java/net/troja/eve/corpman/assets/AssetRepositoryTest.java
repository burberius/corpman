package net.troja.eve.corpman.assets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import net.troja.eve.corpman.EveApiRepository;
import net.troja.eve.corpman.evedata.FlagsRepository;
import net.troja.eve.corpman.evedata.InvTypesRepository;
import net.troja.eve.corpman.evedata.SystemLocationsRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.beimin.eveapi.model.shared.Asset;
import com.beimin.eveapi.response.shared.AssetListResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AssetRepositoryTest {
    private static final int PROTON_XL = 17676;
    private static final int TRISTAN = 593;
    private static final int STATION_CONTAINER = 17366;

    private static List<Asset> assets;
    private static SystemLocationsRepository locationRepository;
    private static InvTypesRepository invTypesRepository;
    private static FlagsRepository flagsRepository;

    private AssetRepository objectToWorkOn;

    @Mock
    private EveApiRepository eveApiRepository;

    @BeforeClass
    public static void loadAssets() throws IOException {
        final InputStream inputStream = AssetRepositoryTest.class.getClass().getResourceAsStream("/assets.txt");
        final ObjectMapper mapper = new ObjectMapper();
        assets = mapper.readValue(inputStream, new TypeReference<List<Asset>>() {
        });
        locationRepository = new SystemLocationsRepository();
        invTypesRepository = new InvTypesRepository();
        flagsRepository = new FlagsRepository();
    }

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        objectToWorkOn = new AssetRepository();
        objectToWorkOn.setEveApiRepository(eveApiRepository);
        objectToWorkOn.setFlagsRepository(flagsRepository);
        objectToWorkOn.setInvTypesRepository(invTypesRepository);
        objectToWorkOn.setLocationRepository(locationRepository);
    }

    @Test
    public void testLabledAsset() {
        final Asset asset = new Asset();
        asset.setTypeID(STATION_CONTAINER); // Station Container
        asset.setSingleton(true);
        assertThat(objectToWorkOn.isLabledAsset(asset), is(true));

        asset.setTypeID(TRISTAN); // Tristan
        assertThat(objectToWorkOn.isLabledAsset(asset), is(true));

        asset.setTypeID(TRISTAN); // Tristan packaged
        asset.setSingleton(false);
        assertThat(objectToWorkOn.isLabledAsset(asset), is(false));

        asset.setTypeID(PROTON_XL); // Proton XL
        asset.setSingleton(true);
        assertThat(objectToWorkOn.isLabledAsset(asset), is(false));
    }

    @Test
    public void testFilterSingleEntry() {
        whenForAssets();

        // Location and Type
        Filter filter = new Filter(12239, null, null, 30004469l, null);
        List<Asset> list = objectToWorkOn.filterAssets(filter);
        assertThat(list.size(), is(1));

        // Location and Group
        filter = new Filter(null, 423, null, 60006511l, null);
        list = objectToWorkOn.filterAssets(filter);
        assertThat(list.size(), is(1));

        // Location and Category
        filter = new Filter(null, null, 4, 61000593l, null);
        list = objectToWorkOn.filterAssets(filter);
        assertThat(list.size(), is(1));
    }

    @Test
    public void testFilterSeveralEntries() {
        whenForAssets();

        final Filter filter = new Filter(null, null, null, null, 117);
        final List<Asset> list = objectToWorkOn.filterAssets(filter);
        assertThat(list.size(), is(40));
    }

    @Test
    public void testFilterContainerAndReturnWholeContent() {
        whenForAssets();

        final Filter filter = new Filter(1016659487157l, null, null, null, null, null);
        final List<Asset> list = objectToWorkOn.filterAssets(filter);
        assertThat(list.size(), is(2));
    }

    @Test
    public void testFilterContainerAndFilterContent() {
        whenForAssets();

        final Filter filter = new Filter(1016659487157l, null, 1136, null, null, null);
        final List<Asset> list = objectToWorkOn.filterAssets(filter);
        assertThat(list.size(), is(1));
    }

    @Test
    public void testFilterSubContainerAndReturnWholeContent() {
        whenForAssets();

        final Filter filter = new Filter(1016961164867l, null, null, null, null, 11);
        final List<Asset> list = objectToWorkOn.filterAssets(filter);
        assertThat(list.size(), is(1));
    }

    @Test
    public void testAssetListHasProperLocations() {
        whenForAssets();

        final List<Asset> list = objectToWorkOn.getAssets();
        assertThat(list.size(), greaterThan(0));
        checkList(list);
    }

    @Test
    public void testCaching() {
        whenForAssets();

        objectToWorkOn.update();
        verify(eveApiRepository, only()).getAssets();
    }

    private void checkList(final List<Asset> list) {
        for (final Asset asset : list) {
            assertThat(asset.getLocationID(), notNullValue());
            if ((asset.getAssets() != null) && (asset.getAssets().size() > 0)) {
                checkList(asset.getAssets());
            }
        }
    }

    private void whenForAssets() {
        final AssetListResponse response = new AssetListResponse();
        for (final Asset asset : assets) {
            response.add(asset);
        }
        response.setCachedUntil(new Date(System.currentTimeMillis() + 1000000));
        when(eveApiRepository.getAssets()).thenReturn(response);

        objectToWorkOn.update();
    }
}
