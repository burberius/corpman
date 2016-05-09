package net.troja.eve.corpman.pos;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.troja.eve.corpman.EveApiRepository;
import net.troja.eve.corpman.evedata.InvTypesRepository;
import net.troja.eve.corpman.evedata.SystemLocationsRepository;
import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.PosModuleRepository;
import net.troja.eve.corpman.pos.db.PosRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;

import com.beimin.eveapi.model.corporation.Starbase;
import com.beimin.eveapi.response.corporation.StarbaseDetailResponse;

public class PosManagingControllerTest {
    private static final int FUEL_QUANTITY = 100;
    private static final int LOCATION = 30000142;
    private static final int MOON = 40009087;
    private static final String LOCATION_NAME = "Jita";
    private static final int LOCATION_ID = 1234567890;
    private static final String MOON_NAME = "Jita IV - Moon 4";
    private static final long ITEM_ID = 123456789;
    private static final Date ONLINE_DATE = new Date(1234567243L);
    private static final Date STATE_DATE = new Date(1234599999L);
    private static final Date CACHED_DATE = new Date(System.currentTimeMillis());
    private static final int TYPE_ID = 27536;
    private static final String TYPE_NAME = "Serpentis Control Tower";

    @Mock
    private EveApiRepository eveApiRepository;
    @Mock
    private PosRepository posRepository;
    @Mock
    private SystemLocationsRepository locationRepository;
    @Mock
    private InvTypesRepository invTypesRepository;
    @Mock
    private PosModuleRepository posModuleRepository;

    private PosManagingController objectToTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        objectToTest = new PosManagingController();
        objectToTest.setEveApiRepository(eveApiRepository);
        objectToTest.setInvTypesRepository(invTypesRepository);
        objectToTest.setLocationRepository(locationRepository);
        objectToTest.setPosRepository(posRepository);
        objectToTest.setPosModuleRepository(posModuleRepository);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreate() {
        // Prepare
        when(eveApiRepository.getPosList()).thenReturn(getStarbaseList());
        when(locationRepository.getName(LOCATION)).thenReturn(LOCATION_NAME);
        when(locationRepository.getName(MOON)).thenReturn(MOON_NAME);
        when(eveApiRepository.getPosDetails(ITEM_ID)).thenReturn(getStarbaseDetails());
        when(invTypesRepository.getName(TYPE_ID)).thenReturn(TYPE_NAME);
        when(posRepository.findAll()).thenReturn(null);
        when(posRepository.getMinCachedUntil()).thenReturn(null);

        final Pos pos = new Pos(ITEM_ID, TYPE_NAME, TYPE_ID, LOCATION_NAME, LOCATION_ID, "IV - 4", 1, STATE_DATE, ONLINE_DATE);
        pos.setCachedUntil(CACHED_DATE);
        pos.setFuel(FUEL_QUANTITY);
        pos.setStrontium(FUEL_QUANTITY);

        // Run
        objectToTest.update();

        // Verify
        final ArgumentCaptor<List<Pos>> captor = ArgumentCaptor.forClass(List.class);
        verify(posRepository).save(captor.capture());
        final List<Pos> list = captor.getValue();
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0), equalTo(pos));
    }

    @Test
    public void testUpdate() {
        final Pos posPre = new Pos(ITEM_ID, TYPE_NAME, TYPE_ID, LOCATION_NAME, LOCATION_ID, "IV - 4", 1, STATE_DATE, ONLINE_DATE);
        posPre.setCachedUntil(CACHED_DATE);
        posPre.setFuel(FUEL_QUANTITY * 4);
        posPre.setStrontium(FUEL_QUANTITY * 4);

        // Prepare
        when(eveApiRepository.getPosList()).thenReturn(getStarbaseList());
        when(locationRepository.getName(LOCATION)).thenReturn(LOCATION_NAME);
        when(locationRepository.getName(MOON)).thenReturn(MOON_NAME);
        when(eveApiRepository.getPosDetails(ITEM_ID)).thenReturn(getStarbaseDetails());
        when(invTypesRepository.getName(TYPE_ID)).thenReturn(TYPE_NAME);
        when(posRepository.findAll()).thenReturn(Arrays.asList(posPre));
        when(posRepository.getMinCachedUntil()).thenReturn(new Date(System.currentTimeMillis() - 1000));

        final Pos pos = new Pos(ITEM_ID, TYPE_NAME, TYPE_ID, LOCATION_NAME, LOCATION_ID, "IV - 4", 1, STATE_DATE, ONLINE_DATE);
        pos.setCachedUntil(CACHED_DATE);
        pos.setFuel(FUEL_QUANTITY);
        pos.setStrontium(FUEL_QUANTITY);

        // Run
        objectToTest.update();

        // Verify
        verify(posRepository).save(pos);
    }

    @Test
    public void testDelete() {
        final Pos pos = new Pos(ITEM_ID, TYPE_NAME, TYPE_ID, LOCATION_NAME, LOCATION_ID, "IV - 4", 1, STATE_DATE, ONLINE_DATE);
        pos.setCachedUntil(CACHED_DATE);
        pos.setFuel(FUEL_QUANTITY * 4);
        pos.setStrontium(FUEL_QUANTITY * 4);

        // Prepare
        when(eveApiRepository.getPosList()).thenReturn(new HashSet<Starbase>());
        when(posRepository.findAll()).thenReturn(Arrays.asList(pos));
        when(posRepository.getMinCachedUntil()).thenReturn(new Date(System.currentTimeMillis() - 1000));

        // Run
        objectToTest.update();

        // Verify
        verify(posModuleRepository).findByPos(pos);
        verify(posRepository).delete(pos);
    }

    @Test
    public void testError() {
        final Pos pos = new Pos(ITEM_ID, TYPE_NAME, TYPE_ID, LOCATION_NAME, LOCATION_ID, "IV - 4", 1, STATE_DATE, ONLINE_DATE);
        pos.setCachedUntil(CACHED_DATE);
        pos.setFuel(FUEL_QUANTITY * 4);
        pos.setStrontium(FUEL_QUANTITY * 4);

        // Prepare
        when(eveApiRepository.getPosList()).thenReturn(null);
        when(posRepository.findAll()).thenReturn(Arrays.asList(pos));
        when(posRepository.getMinCachedUntil()).thenReturn(new Date(System.currentTimeMillis() - 1000));

        // Run
        objectToTest.update();

        // Verify
        verify(posRepository, never()).delete(pos);
    }

    private StarbaseDetailResponse getStarbaseDetails() {
        final StarbaseDetailResponse response = new StarbaseDetailResponse();
        response.addFuelLevel(PosTypeIds.TYPEID_STRONTIUM, FUEL_QUANTITY);
        response.addFuelLevel(1234, FUEL_QUANTITY);
        response.setCachedUntil(CACHED_DATE);
        return response;
    }

    private Set<Starbase> getStarbaseList() {
        final Starbase starbase = new Starbase();
        starbase.setLocationID(LOCATION);
        starbase.setMoonID(MOON);
        starbase.setItemID(ITEM_ID);
        starbase.setOnlineTimestamp(ONLINE_DATE);
        starbase.setStateTimestamp(STATE_DATE);
        starbase.setState(1);
        starbase.setTypeID(TYPE_ID);
        return Sets.newSet(starbase);
    }
}
