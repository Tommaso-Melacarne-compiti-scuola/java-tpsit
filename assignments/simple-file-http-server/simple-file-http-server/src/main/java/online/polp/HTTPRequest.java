package online.polp;

import java.util.List;

import lombok.Data;

@Data
public class HTTPRequest {
    public final FirstLineHTTPRequest firstLine;
    public final List<String> headers;
    public final String body;
}
