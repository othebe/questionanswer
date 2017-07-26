package repository.store;

public interface IStore<T> {
    // Determines if the store has the entity with ID.
    boolean hasData(long id);

    // Reads given entity.
    T read(long id);

    // Write given entity.
    boolean write(T entity);
}
