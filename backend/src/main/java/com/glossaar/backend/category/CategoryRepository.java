package com.glossaar.backend.category;

import com.glossaar.backend.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByNameAndUser(String name, UserEntity user);

    @Query("""
    SELECT c FROM CategoryEntity c
    WHERE c.user = :user
    ORDER BY LOWER(c.name) ASC
""")
    List<CategoryEntity> findAllByUserOrdered(@Param("user") UserEntity user);

    Optional<CategoryEntity> findByIdAndUser(Long id, UserEntity user);
}
