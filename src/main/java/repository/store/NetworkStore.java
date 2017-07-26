package repository.store;

public class NetworkStore<T> implements IStore<T> {
    public boolean hasData(long id) {
        // Assume network always has data.
        return true;
    }

    public T read(long id) {
        return null;
    }

    public boolean write(T entity) {
        return false;
    }
}
