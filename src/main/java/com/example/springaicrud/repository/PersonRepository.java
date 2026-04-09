package com.example.springaicrud.repository;

import com.example.springaicrud.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByMobileNo(String mobileNo);
    boolean existsByMobileNo(String mobileNo);

    @Modifying
    @Transactional(propagation =
            org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    @Query("UPDATE Person p SET p.imageUrl = :imageUrl WHERE p.id = :id")
    void updateImageUrl(@Param("id") Long id,
                        @Param("imageUrl") String imageUrl);
}