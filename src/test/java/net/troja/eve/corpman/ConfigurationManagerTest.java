package net.troja.eve.corpman;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationManagerTest {
    @Mock
    private ConfigurationRepository repository;

    @Mock
    private ConfigurationChangeListener testListener;

    @InjectMocks
    private ConfigurationManager classToTest;

    private Configuration testConfiguration;

    @Before
    public void setUp() {
        testConfiguration = new Configuration();

        when(repository.findOne(1l)).thenReturn(testConfiguration);

        classToTest.setConfigurationRepository(repository);
        classToTest.init();
    }

    @Test
    public void save() {
        final Configuration config = new Configuration();

        classToTest.save(config);

        verify(repository).save(config);
    }

    @Test
    public void idIsSetTo1OnSave() {
        final Configuration config = new Configuration();
        config.setId(10);

        classToTest.save(config);

        assertThat(config.getId(), equalTo(1l));
    }

    @Test
    public void listenersInformedOnInit() {
        classToTest.addChangeListener(testListener);
        classToTest.init();

        verify(testListener).configurationChanged(classToTest.getConfiguration());
    }

    @Test
    public void listenersInformedOnSave() {
        final Configuration config = new Configuration();

        classToTest.addChangeListener(testListener);
        classToTest.save(config);

        verify(testListener).configurationChanged(config);
    }
}
