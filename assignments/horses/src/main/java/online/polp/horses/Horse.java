package online.polp.horses;

import lombok.Data;
import online.polp.horses.singletons.RandomSingleton;

import java.util.List;
import java.util.Random;

@Data
public class Horse implements Runnable {
    private static final int RANDOM_SLEEP_MIN = 500;
    private static final int RANDOM_SLEEP_MAX = 1000;

    final int id;
    final List<Horse> leaderboard;

    @Override
    public void run() {
        Random random = RandomSingleton.getInstance();

        long sleepTime = random.nextLong(RANDOM_SLEEP_MIN, RANDOM_SLEEP_MAX);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (leaderboard) {
            leaderboard.add(this);
        }
    }
}