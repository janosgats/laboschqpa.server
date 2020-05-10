package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

    List<NewsPost> findAllByOrderByCreationTimeDesc();
}
