package online.polp;

import java.util.List;

import lombok.Data;

@Data
public class HTTPResponse {
    public final FirstLineHttpResponse firstLine;
    public final List<String> headers;
    public final byte[] body;
}
