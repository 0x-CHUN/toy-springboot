package springboot.core.config;

import lombok.extern.slf4j.Slf4j;
import springboot.core.config.resource.ResourceLoader;
import springboot.core.config.resource.property.PropertiesResourceLoader;
import springboot.core.config.resource.yaml.YamlResourceLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class ConfigurationManager implements Configuration {
    private static final String PROPERTIES_FILE_EXTENSION = ".properties";
    private static final String YAML_FILE_EXTENSION = ".yaml";
    private static final String YML_FILE_EXTENSION = ".yml";

    private final Configuration configuration;

    public ConfigurationManager(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public int getInt(String id) {
        return configuration.getInt(id);
    }

    @Override
    public String getString(String id) {
        return configuration.getString(id);
    }

    @Override
    public Boolean getBoolean(String id) {
        return configuration.getBoolean(id);
    }

    @Override
    public void loadResources(List<Path> paths) {
        try {
            for (Path path : paths) {
                String fileName = path.getFileName().toString();
                if (fileName.endsWith(PROPERTIES_FILE_EXTENSION)) {
                    ResourceLoader loader = new PropertiesResourceLoader();
                    configuration.putAll(loader.loadResource(path));
                } else if (fileName.endsWith(YAML_FILE_EXTENSION) || fileName.endsWith(YML_FILE_EXTENSION)) {
                    ResourceLoader load = new YamlResourceLoader();
                    configuration.putAll(load.loadResource(path));
                }
            }
        } catch (IOException e) {
            log.error("Can not load the resource");
            System.exit(-1);
        }
    }
}
