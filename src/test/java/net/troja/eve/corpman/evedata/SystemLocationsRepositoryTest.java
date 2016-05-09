package net.troja.eve.corpman.evedata;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SystemLocationsRepositoryTest {
    @Test
    public void testResolving() {
        final SystemLocationsRepository systemLocationsRepository = new SystemLocationsRepository();

        assertThat(systemLocationsRepository.getName(30000142L), equalTo("Jita"));
        assertThat(systemLocationsRepository.isInSpace(30000142L), equalTo(true));
        assertThat(systemLocationsRepository.isInSpace(60003760L), equalTo(false));
        assertThat(systemLocationsRepository.getLocationName(30004469L), equalTo("ZS-2LT"));
        assertThat(systemLocationsRepository.getLocationName(66014066L), equalTo("Hasateem VI - Moon 12 - Thukker Mix Factory"));
        assertThat(systemLocationsRepository.getLocationName(1L), equalTo("unknown (1)"));
    }
}