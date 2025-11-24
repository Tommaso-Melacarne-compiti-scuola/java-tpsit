package online.polp.horses.singletons;

import lombok.Getter;

import java.util.Random;

@Getter
public class RandomSingleton {
    private static Random INSTANCE;

    private RandomSingleton() {
        // Private constructor to prevent instantiation
    }

    public static Random getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Random();
        }
        return INSTANCE;
    }
}