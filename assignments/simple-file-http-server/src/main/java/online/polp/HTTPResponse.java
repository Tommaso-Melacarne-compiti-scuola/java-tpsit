package online.polp;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HTTPResponse {
    private final String version;
    private final int statusCode;
    public final Map<String, String> headers;
    public final byte[] body;
    
    public String firstLineToString() {
        StatusCode status = StatusCode.fromCode(statusCode);

        return String.format("%s %d %s", version, statusCode, status.getMessage());
    }

    public String headersToString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        return sb.toString();
    }


}
