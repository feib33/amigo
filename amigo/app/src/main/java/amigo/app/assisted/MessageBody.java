package amigo.app.assisted;

import java.util.Date;

/**
 * This class stands for a chat message component, includes message content, type and timestamp;
 */
public class MessageBody implements Comparable<MessageBody> {

    private String message;

    private TYPE type;
    private Date timestamp;

    public MessageBody(String message, TYPE type, Date timestamp) {

        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public TYPE getType() {
        return this.type;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int compareTo(MessageBody o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }

    enum TYPE {

        Received,

        Sent
    }
}