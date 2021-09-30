package com.laboschqpa.server.repo.qrtagfight;

import com.laboschqpa.server.entity.qrfight.QrFightArea;
import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaAndTeamSubmissionCountJpaDto;
import com.laboschqpa.server.repo.dto.qrFightArea.QrFightAreaWithTagCountJpaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QrFightAreaRepository extends JpaRepository<QrFightArea, Long> {

    List<QrFightArea> findAllByEnabledIsTrueOrderByIdAsc();

    @Query("select " +
            " qfa.id as id, " +
            " qfa.name as name, " +
            " qfa.description as description, " +
            " qfa.enabled as enabled, " +
            " coalesce(count(qt), 0) as tagCount " +
            " from QrFightArea qfa " +
            " left join QrTag qt on qfa = qt.area " +
            " where qfa.enabled = true " +
            " group by qfa " +
            " order by qfa.id ASC")
    List<QrFightAreaWithTagCountJpaDto> findAllByEnabledIsTrue_withBelongingTagCount_orderByIdAsc();

    @Query("select " +
            " qfa.id as areaId, " +
            " qts.team.id as teamId, " +
            " qts.team.name as teamName, " +
            " count(qts) as submissionCount " +
            " from QrFightArea qfa " +
            " left join QrTag qt on qfa = qt.area " +
            " left join QrTagSubmission qts on qt = qts.qrTag " +
            " where qfa.enabled = true " +
            " group by qfa, qts.team ")
    List<QrFightAreaAndTeamSubmissionCountJpaDto> findEnabledAreasWithTeamSubmissionCount();
}
