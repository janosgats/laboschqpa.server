package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.SpeedDrinking;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpeedDrinkingRepository extends JpaRepository<SpeedDrinking, Long> {
    List<SpeedDrinking> findAllByOrderByTimeAsc();

    @EntityGraph(attributePaths = {"drinkerUserAcc", "drinkerUserAcc.team"})
    @Query("select sd from SpeedDrinking sd " +
            " where sd.category = :category " +
            " order by sd.time asc")
    List<SpeedDrinking> findByCategory_withDrinkerUserAccAndTeam_orderByTimeAsc(@Param("category") SpeedDrinkingCategory category);

    @Modifying
    @Query("delete from SpeedDrinking where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @EntityGraph(attributePaths = {"drinkerUserAcc", "drinkerUserAcc.team"})
    @Query("select sd from SpeedDrinking sd where sd.id = :id")
    Optional<SpeedDrinking> findById_withDrinkerUserAccAndTeam(Long id);
}
