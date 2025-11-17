package online.polp;

public class Message {
    static int NEXT_ID = 0;
    final int id;
    final String author;
    final String text;

    public Message(String author, String text) {
        this.id = NEXT_ID++;
        this.author = author;
        this.text = text;
    }
}
