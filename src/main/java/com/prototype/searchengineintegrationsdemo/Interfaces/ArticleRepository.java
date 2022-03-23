package com.prototype.searchengineintegrationsdemo.Interfaces;

import com.prototype.searchengineintegrationsdemo.Models.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

//    Page<Article> findByAuthorsName(String name, Pageable pageable);

    Page<Article> findByTitle(String title, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}
