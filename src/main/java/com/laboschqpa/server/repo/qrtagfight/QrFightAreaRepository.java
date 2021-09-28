package com.laboschqpa.server.repo.qrtagfight;

import com.laboschqpa.server.entity.qrfight.QrFightArea;
import com.laboschqpa.server.repo.dto.QrFightAreaWithTeamSubmissionCountJpaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QrFightAreaRepository extends JpaRepository<QrFightArea, Long> {

    List<QrFightArea> findAllByEnabledIsTrueOrderByIdAsc();

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
    List<QrFightAreaWithTeamSubmissionCountJpaDto> findEnabledAreasWithTeamSubmissionCount();
}
