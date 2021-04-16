package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.NewsPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {
    List<NewsPost> findAllByOrderByCreationTimeDesc();

    @Modifying
    @Query("delete from NewsPost where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select np from NewsPost np where np.id = :id")
    Optional<NewsPost> findByIdWithEagerAttachments(Long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select np from NewsPost np order by np.creationTime desc")
    List<NewsPost> findAllByOrderByCreationTimeDesc_withEagerAttachments();
}
