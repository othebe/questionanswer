package common;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static common.Constants.COUCH_DB_HOST;
import static common.Constants.CACHE_DB_NAME;

public class CouchDBClient {
    public static final CouchDBClient INSTANCE = new CouchDBClient(CACHE_DB_NAME);

    private CouchDbInstance instance;
    public CouchDbConnector db;

    public CouchDBClient(String dbName) {
        try {
            HttpClient httpClient = new StdHttpClient.Builder()
                    .url(COUCH_DB_HOST)
                    .build();
            instance = new StdCouchDbInstance(httpClient);
            db = new StdCouchDbConnector(dbName, instance);

            db.createDatabaseIfNotExists();
        } catch (Exception e) { }
    }

    public void saveData(String id, Object data) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("_id", id);
        dataMap.put("data", data);

        try {
            db.create(dataMap);
        } catch (Exception e) { }
    }

    public <T> T readData(String id, Class<T> klass) {
        try {
            LinkedHashMap<String, Object> entry = db.get(LinkedHashMap.class, id);
            return (T) entry.get("data");
        } catch (Exception e) {
            return null;
        }
    }
}
