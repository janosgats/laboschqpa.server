package com.labosch.csillagtura.repo;

import com.labosch.csillagtura.entity.AccountJoinInitiation;
import com.labosch.csillagtura.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountJoinInitiationRepository extends JpaRepository<AccountJoinInitiation, Long> {

    Optional<AccountJoinInitiation> findByInitiatorUser(User initiatorUser);

    List<AccountJoinInitiation> findByApproverUser(User approverUser);
}
