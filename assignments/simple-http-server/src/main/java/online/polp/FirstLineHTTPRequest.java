package online.polp;

import lombok.Data;

@Data
public class FirstLineHTTPRequest {
    public final Method method;
    public final String path;
    public final String version;

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        OPTIONS,
        PATCH,
        TRACE,
        CONNECT
    }

}
