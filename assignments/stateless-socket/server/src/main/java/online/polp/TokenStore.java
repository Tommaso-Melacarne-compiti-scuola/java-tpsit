package online.polp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TokenStore {
    Map<String, String> tokenStore = Collections.synchronizedMap(new HashMap<>());

    public String newToken(String username) {
        String token = TokenGenerator.generateToken();
        tokenStore.put(token, username);
        return token;
    }

    public String getUsername(String token) {
        return tokenStore.get(token);
    }

    public void invalidateToken(String token) {
        tokenStore.remove(token);
    }
}
