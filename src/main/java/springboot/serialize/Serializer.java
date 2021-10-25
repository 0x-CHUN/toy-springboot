package springboot.serialize;

public interface Serializer {
    /**
     * Java object to byte array
     *
     * @param obj java object
     * @return byte array
     */
    byte[] serialize(Object obj);

    /**
     * byte array to specific java object
     *
     * @param bytes byte array
     * @param clazz java class
     * @param <T>   specific
     * @return java object
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
