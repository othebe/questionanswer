package repository.store;

public class CacheStore<T> implements IStore<T> {
    public boolean hasData(long id) {
        return false;
    }

    public T read(long id) {
        return null;
    }

    public boolean write(T entity) {
        return false;
    }
}
