package online.polp;

import lombok.Data;
import lombok.NonNull;

@Data
public class FirstLineHTTPRequest {
    public final Method method;
    @NonNull
    public String path;
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
