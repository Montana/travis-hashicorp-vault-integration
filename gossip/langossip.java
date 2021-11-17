public enum Verb {
    
    GOSSIP_DIGEST_SYN    (1, 0, GossipDigestSynVerbHandler.instance),
    GOSSIP_DIGEST_ACK    (2, 0, GossipDigestAckVerbHandler.instance),
    GOSSIP_DIGEST_ACK2   (3, 0, GossipDigestAck2VerbHandler.instance);

    public final int id;
    public final int priority;
    private final IVerbHandler handler;
    Verb(int id, int priority, IVerbHandler handler){
        if (id < 0)
            throw new IllegalArgumentException(
              "Verb id must be non-negative, got " + id + " for payload " + name());
        this.id = id;
        this.priority = priority;
        this.handler = handler;
    }

}


import java.io.Serializable;
import java.util.Map;

public class Header implements Serializable {
    public final long id;
    public final Verb verb;
    public final InetAddressAndPort from;
    public final long createdAtNanos;
    public final long expiresAtNanos;
    public Header(long id, Verb verb, InetAddressAndPort from, 
                  long createdAtNanos, long expiresAtNanos)
    {
        this.id = id;
        this.verb = verb;
        this.from = from;
        this.createdAtNanos = createdAtNanos;
        this.expiresAtNanos = expiresAtNanos;
    }
}


import java.io.Serializable;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Message<T> implements Serializable {

    public final Header header;
    public final T payload;
    private Message(Header header, T payload) {
        this.header = header;
        this.payload = payload;
    }

    public InetAddressAndPort from() {
        return header.from;
    }

    public long id() {
        return header.id;
    }

    public Verb verb() {
        return header.verb;
    }

    public static <T> Message<T> getNewMessage(Verb verb, T payload){
        return getNewMessage(nextId(), verb, 0, payload, null, null);
    }

    private static <T> Message<T> getNewMessage(long id, Verb verb, 
                                                long expiresAtNanos, T payload) 
    {

        if (payload == null)
            throw new IllegalArgumentException();

        Date date = new Date();
        InetAddressAndPort from = NetworkUtils.getBroadcastAddressAndPort();
        long createdAtNanos = date.getTime();
        if (expiresAtNanos == 0)
            expiresAtNanos = 100;

        return new Message<T>(new Header(id, verb, from, createdAtNanos, expiresAtNanos, payload);
    }

    private static final long NO_ID = 0L; 
    private static final AtomicInteger nextId = new AtomicInteger(0);

    private static long nextId() {
        long id;
        do {
            id = nextId.incrementAndGet();
        } while (id == NO_ID);
        return id;
    }
}
