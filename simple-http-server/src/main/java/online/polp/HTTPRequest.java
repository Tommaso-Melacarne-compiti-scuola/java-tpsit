package online.polp;

import lombok.Data;

@Data
public class HTTPRequest {
    public final FirstLineHTTPRequest firstLine;
    public final String[] headers;
    public final String body;
}
