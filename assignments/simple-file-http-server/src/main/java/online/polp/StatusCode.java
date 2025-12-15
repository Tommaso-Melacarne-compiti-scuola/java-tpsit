package online.polp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum StatusCode {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed");

    private final int code;
    private final String message;

    public static StatusCode fromCode(int code) {
        return Arrays.stream(StatusCode.values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status code: " + code));
    }
}
