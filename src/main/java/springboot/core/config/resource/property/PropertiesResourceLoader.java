package springboot.core.config.resource.property;

import springboot.core.config.resource.AbstractResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesResourceLoader extends AbstractResourceLoader {
    @Override
    protected Map<String, String> loadResources(Path path) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        InputStream stream = null;
        Reader reader = null;
        try {
            Properties properties = new Properties();
            stream = Files.newInputStream(path);
            reader = new InputStreamReader(stream);
            properties.load(reader);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                result.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }
}
