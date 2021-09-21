package com.laboschqpa.server.repo.qrtagfight;

import com.laboschqpa.server.entity.qrfight.QrTag;
import com.laboschqpa.server.repo.dto.QrFightAreaWithTeamSubmissionCountJpaDto;
import com.laboschqpa.server.repo.dto.QrTagWithSubmissionCountJpaDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QrTagRepository extends JpaRepository<QrTag, Long> {

    @Query("select qt.id as id, count(qts) as submissionCount " +
            " from QrTag qt " +
            " left join QrTagSubmission qts on qt = qts.qrTag " +
            " group by qt " +
            " order by qt.id ASC")
    List<QrTagWithSubmissionCountJpaDto> findAll_withSubmissionCount();

    @Query("select " +
            " qfa.id as areaId, " +
            " qts.team.id as teamId, " +
            " qts.team.name as teamName, " +
            " count(qts) as submissionCount " +
            " from QrFightArea qfa " +
            " left join QrTag qt on qfa = qt.area " +
            " left join QrTagSubmission qts on qt = qts.qrTag " +
            " group by qfa, qts.team ")
    List<QrFightAreaWithTeamSubmissionCountJpaDto> finAllAreasWithTeamSubmissionCount();
}
