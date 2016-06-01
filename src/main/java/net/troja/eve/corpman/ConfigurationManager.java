package net.troja.eve.corpman;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationManager {
    @Autowired
    private ConfigurationRepository repository;

    private Configuration currentConfiguration;
    private final Set<ConfigurationChangeListener> listeners = new HashSet<>();

    @PostConstruct
    void init() {
        currentConfiguration = repository.findOne(1l);
        informListeners();
    }

    public Configuration getConfiguration() {
        return currentConfiguration;
    }

    public void save(final Configuration configuration) {
        configuration.setId(1);
        repository.save(configuration);
        currentConfiguration = configuration;
        informListeners();
    }

    public void addChangeListener(final ConfigurationChangeListener listener) {
        listeners.add(listener);
    }

    private void informListeners() {
        for (final ConfigurationChangeListener listener : listeners) {
            listener.configurationChanged(currentConfiguration);
        }
    }

    public void setConfigurationRepository(final ConfigurationRepository repository) {
        this.repository = repository;
    }
}
