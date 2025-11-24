package online.polp.horses;

import java.util.ArrayList;


public class Leaderboard extends ArrayList<Horse> {
    @SuppressWarnings("unused")
    public Leaderboard() {
        super();
    }

    public Leaderboard(int horseCount) {
        super(horseCount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            Horse horse = this.get(i);
            sb.append(String.format("%d. Horse %d\n", i + 1, horse.getId()));
        }
        return sb.toString();
    }
}
