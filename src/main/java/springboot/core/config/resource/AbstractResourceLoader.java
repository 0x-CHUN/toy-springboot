package springboot.core.config.resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractResourceLoader implements ResourceLoader {
    protected abstract Map<String, String> loadResources(Path path) throws IOException;

    @Override
    public Map<String, String> loadResource(Path path) throws IOException {
        return loadResources(path);
    }
}
