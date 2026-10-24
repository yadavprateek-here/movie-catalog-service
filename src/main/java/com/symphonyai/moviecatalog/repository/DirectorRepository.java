package com.symphonyai.moviecatalog.repository;

import com.symphonyai.moviecatalog.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
}
