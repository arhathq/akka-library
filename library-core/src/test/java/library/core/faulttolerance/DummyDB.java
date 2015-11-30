package library.core.faulttolerance;

import java.util.HashMap;
import java.util.Map;

import static library.core.faulttolerance.StorageApi.*;

/**
 * @author Alexander Kuleshov
 */
public class DummyDB {

    public static final DummyDB instance = new DummyDB();

    private final Map<String, Long> db = new HashMap<>();

    private DummyDB() {
    }

    public synchronized void save(String key, Long value) throws StorageException {
        if (11 <= value && value <= 14)
            throw new StorageException("Simulated store failure " + value);
        db.put(key, value);
    }

    public synchronized Long load(String key) throws StorageException {
        return db.get(key);
    }
}
