package com.laboschqpa.server.repo.qrtagfight;

import com.laboschqpa.server.entity.qrfight.QrFightArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QrFightAreaRepository extends JpaRepository<QrFightArea, Long> {

    List<QrFightArea> findAllByOrderByIdAsc();
}
