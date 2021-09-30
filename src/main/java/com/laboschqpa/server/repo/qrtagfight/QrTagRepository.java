package com.laboschqpa.server.repo.qrtagfight;

import com.laboschqpa.server.entity.qrfight.QrTag;
import com.laboschqpa.server.repo.dto.qrFightArea.QrTagWithSubmissionCountJpaDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QrTagRepository extends JpaRepository<QrTag, Long> {

    @Query("select qt.id as id, count(qts) as submissionCount " +
            " from QrTag qt " +
            " left join QrTagSubmission qts on qt = qts.qrTag " +
            " group by qt " +
            " order by qt.id ASC")
    List<QrTagWithSubmissionCountJpaDto> findAll_withSubmissionCount();

    @EntityGraph(attributePaths = {"area"})
    @Query("select qt from QrTag qt where qt.id = :id")
    Optional<QrTag> findById_withEagerArea(Long id);
}
