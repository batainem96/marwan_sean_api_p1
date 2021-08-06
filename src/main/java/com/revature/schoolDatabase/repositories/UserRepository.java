package com.revature.schoolDatabase.repositories;

import com.revature.schoolDatabase.models.Person;

public class UserRepository implements CrudRepository<Person> {


    @Override
    public Person findById(int id) {
        return null;
    }

    @Override
    public Person save(Person newResource) {
        return null;
    }

    @Override
    public boolean update(Person updatedResource) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}
