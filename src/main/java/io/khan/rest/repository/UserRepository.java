package io.khan.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.khan.rest.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByEmail(String email);
    @Query
    User findByNameIgnoreCase(String name);
    @Query("SELECT COUNT(u) FROM User u WHERE u.email = ?1")
    Long countByEmail(String email);
}