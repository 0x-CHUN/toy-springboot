package springboot.core.config.resource.yaml;

import org.yaml.snakeyaml.Yaml;
import springboot.core.config.resource.AbstractResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlResourceLoader extends AbstractResourceLoader {
    @Override
    protected Map<String, String> loadResources(Path path) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        InputStream stream = null;
        Reader reader = null;
        try {
            Yaml yaml = new Yaml();
            stream = Files.newInputStream(path);
            reader = new InputStreamReader(stream);
            Map<String, Object> content = toMap(yaml.load(reader));
            flattenMap(result, content, null);
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

    private void flattenMap(Map<String, String> result, Map<String, Object> content, String path) {
        content.forEach((k, v) -> {
            if (path != null && !path.isEmpty()) {
                k = path + '.' + k;
            }
            if (v instanceof String) {
                result.put(k, String.valueOf(v));
            } else if (v instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) v;
                flattenMap(result, map, k);
            } else {
                result.put(k, (v != null ? String.valueOf(v) : ""));
            }
        });
    }

    private Map<String, Object> toMap(Object content) {
        Map<String, Object> res = new LinkedHashMap<>();
        Map<Object, Object> map = (Map<Object, Object>) content;
        map.forEach((k, v) -> {
            if (v instanceof Map) {
                v = toMap(v);
            }
            res.put(k.toString(), v);
        });
        return res;
    }
}
