package online.polp;

import lombok.Data;

@Data
public class FirstLineHttpResponse {
    private final String version;
    private final int statusCode;

    public String toString() {
        StatusCode status = StatusCode.fromCode(statusCode);

        return String.format("%s %d %s", version, statusCode, status.getMessage());
    }
}
