package online.polp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import lombok.Data;

@Data
public class HTTPRequest {
    public final FirstLineHTTPRequest firstLine;
    public final List<String> headers;

    private int getContentLength() {
        for (String header : headers) {
            if (header.toLowerCase().startsWith("content-length:")) {
                String[] headerParts = header.split(" ");
                return Integer.parseInt(headerParts[1]);
            }
        }
        return 0;
    }

    public String readBody(BufferedReader in) throws IOException {
        int contentLength = getContentLength();

        if (contentLength <= 0) {
            return "";
        }
        char[] buf = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int n = in.read(buf, read, contentLength - read);
            if (n == -1) {
                break;
            }
            read += n;
        }
        return new String(buf, 0, read);
    }
}
