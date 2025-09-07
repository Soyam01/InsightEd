package com.app.Insighted.repository;

import com.app.Insighted.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.user_id = :id")
    void deleteByUserId(@Param("id") Long id);
}

