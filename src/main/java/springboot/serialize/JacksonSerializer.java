package springboot.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JacksonSerializer implements Serializer {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = new byte[0];
        try {
            bytes = mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("Jackson serialize error :", e);
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T obj = null;
        try {
            obj = mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("Jackson deserialize error :", e);
            e.printStackTrace();
        }
        return obj;
    }
}
