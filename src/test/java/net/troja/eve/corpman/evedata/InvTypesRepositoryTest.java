package net.troja.eve.corpman.evedata;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class InvTypesRepositoryTest {
    @Test
    public void testResolving() {
        final InvTypesRepository invTypesRepository = new InvTypesRepository();

        assertThat(invTypesRepository.getInvType(12236L).getTypeName(), equalTo("Gallente Control Tower"));
        assertThat(invTypesRepository.getName(14343L), equalTo("Silo"));
        assertThat(invTypesRepository.getVolume(12236L), equalTo(8000d));
    }
}
