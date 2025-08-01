package com.example.poc_finops.tagging.repository;

import com.example.poc_finops.tagging.domain.entity.TagStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagStatusRepository extends JpaRepository<TagStatus, UUID> {
    
    List<TagStatus> findByTagsId(UUID tagId);
    
    List<TagStatus> findByName(String name);
    
    List<TagStatus> findByValue(String value);
    
    @Query("SELECT ts FROM TagStatus ts WHERE ts.tags.id = :tagId AND ts.name = :name")
    Optional<TagStatus> findByTagIdAndName(@Param("tagId") UUID tagId, @Param("name") String name);
    
    @Query("SELECT ts FROM TagStatus ts WHERE ts.tags.id = :tagId AND ts.name = :name AND ts.value = :value")
    Optional<TagStatus> findByTagIdAndNameAndValue(@Param("tagId") UUID tagId, 
                                                   @Param("name") String name, 
                                                   @Param("value") String value);
}