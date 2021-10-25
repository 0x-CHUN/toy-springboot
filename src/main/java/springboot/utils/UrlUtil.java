package springboot.utils;

import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;

public class UrlUtil {
    /**
     * Get the decoded path of uri
     *
     * @param uri uri
     * @return decoded path of uri
     */
    public static String getRequestPath(String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        return decoder.path();
    }
}
