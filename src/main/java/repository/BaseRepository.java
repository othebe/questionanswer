package repository;

import repository.store.CacheStore;
import repository.store.IStore;
import repository.store.NetworkStore;

public abstract class BaseRepository<T> {
    private IStore<T> cacheStore;
    private IStore<T> networkStore;

    protected BaseRepository() {
        this.cacheStore = new CacheStore<T>();
        this.networkStore = new NetworkStore<T>();
    }

    abstract String getUrl(long id);

    public T read(long id) {
        T data = null;

        if (!cacheStore.hasData(id)) {
            data = networkStore.read(id);
            cacheStore.write(data);
        } else {
            data = cacheStore.read(id);
        }

        return data;
    }
}
