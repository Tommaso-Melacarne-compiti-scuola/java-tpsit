package online.polp.horses;

import java.util.List;
import java.util.stream.IntStream;

public class Main {
    static final int HORSE_COUNT = 10;

    public static void main(String[] args) {
        List<Horse> leaderboard = new Leaderboard(HORSE_COUNT);

        List<Thread> threads = IntStream.range(0, HORSE_COUNT)
                                        .mapToObj(id -> new Horse(id, leaderboard))
                                        .map(Thread::new)
                                        .toList();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(leaderboard);
    }
}