package net.troja.eve.corpman.evedata;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

public class ReactionsRespositoryTest {
    @Test
    public void testContent() {
        final ReactionsRepository repository = new ReactionsRepository();

        final long crystallineCarbonideReaction = 17963L;
        final long crystallineCarbonide = 16670L;
        final long carbonPolymers = 16659L;
        final long crystalliteAlloy = 16655L;

        final Set<ReactionComponent> inputs = repository.getInputs(crystallineCarbonideReaction);
        assertThat(inputs.size(), equalTo(2));
        assertThat(inputs.contains(new ReactionComponent(crystalliteAlloy, 100)), equalTo(true));
        assertThat(inputs.contains(new ReactionComponent(carbonPolymers, 100)), equalTo(true));

        final Set<ReactionComponent> outputs = repository.getOutputs(crystallineCarbonideReaction);
        assertThat(outputs, contains(new ReactionComponent(crystallineCarbonide, 10000)));
    }
}
