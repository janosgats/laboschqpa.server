package com.laboschcst.server.repo;

import com.laboschcst.server.entity.usergeneratedcontent.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

    @Modifying
    @Query(value = "update `news_post` np inner join `user_generated_content` ugc on np.id = ugc.id set ugc.current_owner_user_id = :intoUserAccountId where ugc.current_owner_user_id = :fromUserAccountId",
            nativeQuery = true)
    int updateOwnerOnAccountJoin(@Param("fromUserAccountId") Long fromUserAccountId,
                                 @Param("intoUserAccountId") Long intoUserAccountId);

    List<NewsPost> findAllByOrderByCreationTimeDesc();
}
