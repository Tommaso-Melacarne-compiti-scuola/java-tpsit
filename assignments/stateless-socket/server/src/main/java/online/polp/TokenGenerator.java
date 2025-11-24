package online.polp;

public class TokenGenerator {
    public static String generateToken() {
        StringBuilder token = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 16; i++) {
            int index = (int) (Math.random() * chars.length());
            token.append(chars.charAt(index));
        }
        return token.toString();
    }
}
