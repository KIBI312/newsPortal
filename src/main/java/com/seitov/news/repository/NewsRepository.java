package com.seitov.news.repository;

import com.seitov.news.entity.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {

    List<News> findAllByOrderByPublishedAtDesc(Pageable pageable);

}
