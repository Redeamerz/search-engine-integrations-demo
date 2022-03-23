package com.prototype.searchengineintegrationsdemo.Controllers;

import com.prototype.searchengineintegrationsdemo.Interfaces.ArticleRepository;
import com.prototype.searchengineintegrationsdemo.Models.Article;
import com.prototype.searchengineintegrationsdemo.Models.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@RestController
public class BaseController {

    private ArticleRepository articleRepository;
    private ElasticsearchRestTemplate elasticsearchTemplate;

    public BaseController(ArticleRepository articleRepository, ElasticsearchRestTemplate elasticsearchTemplate){
        this.articleRepository = articleRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @GetMapping("/")
    public ResponseEntity<Page<Article>> GetAll(){
        Page<Article> articles = articleRepository.findAll(PageRequest.of(0, 10));
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/getname")
    public ResponseEntity<Page<Article>> GetName(@RequestBody Map<String, String> json){
        Page<Article> articleByAuthorsName = articleRepository.findByAuthorsName(json.get("name"), PageRequest.of(0,10).first());
        return ResponseEntity.ok(articleByAuthorsName);
    }

    @PostMapping("/postname")
    public ResponseEntity<String> PostName(@RequestBody Map<String, String> json){
        Article article = new Article(json.get("articleName"));
        article.setAuthors(asList(new Author(json.get("name"))));
        articleRepository.save(article);
        URI location = URI.create("postName");
        return ResponseEntity.created(location).body("Author Created");
    }

    @PutMapping("/updatename")
    public ResponseEntity<String> UpdateName(@RequestBody Map<String, String> json){
        Article article = findArticle(json.get("articleTitle"));
        article.setTitle("title has been changed successfully");
        articleRepository.save(article);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deletename")
    public ResponseEntity<String> DeleteName(@RequestBody Map<String, String> json){
    Article article = findArticle(json.get("articleTitle"));
    articleRepository.delete(article);
    return ResponseEntity.noContent().build();
    }

    private Article findArticle(String articleTitle){
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("title", articleTitle).minimumShouldMatch("75%"))
                .build();

        SearchHits<Article> articles =
                elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog"));
        return articles.getSearchHit(0).getContent();
    }
}
