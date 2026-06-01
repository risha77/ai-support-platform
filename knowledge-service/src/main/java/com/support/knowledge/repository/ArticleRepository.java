package com.support.knowledge.repository;

import com.support.knowledge.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = """
            SELECT * FROM articles
            WHERE to_tsvector('english', title || ' ' || content)
                @@ plainto_tsquery('english', :query)
              AND published = true
            ORDER BY ts_rank(
                to_tsvector('english', title || ' ' || content),
                plainto_tsquery('english', :query)
            ) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Article> fullTextSearch(@Param("query") String query, @Param("limit") int limit);

    @Query(value = """
            SELECT title FROM articles
            WHERE title ILIKE '%' || :prefix || '%'
              AND published = true
            ORDER BY view_count DESC
            LIMIT 8
            """, nativeQuery = true)
    List<String> suggest(@Param("prefix") String prefix);

    @Query("SELECT a FROM Article a WHERE a.category = :cat AND a.published = true ORDER BY a.viewCount DESC")
    Page<Article> findByCategory(@Param("cat") String category, Pageable pageable);

    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
