package library.storage.akka.message;

/**
 * @author Alexander Kuleshov
 */
public class StorageMonitoringMessage {
    public final long eventId;
    public final boolean success;
    private final String routerId;

    public StorageMonitoringMessage(long eventId, boolean success, String routerId) {
        this.eventId = eventId;
        this.success = success;
        this.routerId = routerId;
    }
}
