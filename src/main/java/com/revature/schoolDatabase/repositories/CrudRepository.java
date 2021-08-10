package com.revature.schoolDatabase.repositories;

public interface CrudRepository<T> {
    T save(T newResource);
    T findById(String id);
    boolean update(T updatedResource);
    boolean deleteById(String id);
}
