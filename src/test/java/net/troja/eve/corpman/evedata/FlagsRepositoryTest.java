package net.troja.eve.corpman.evedata;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FlagsRepositoryTest {
    @Test
    public void testResolving() {
        final FlagsRepository flagsRepository = new FlagsRepository();

        assertThat(flagsRepository.getName(63L), equalTo("Locked"));
    }
}
