package com.laboschqpa.server.repo;

import com.laboschqpa.server.entity.AcceptedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface AcceptedEmailRepository extends JpaRepository<AcceptedEmail, Long> {
    List<AcceptedEmail> findAllByOrderByCreatedDesc();

    @Modifying
    @Query(value = "" +
            " INSERT INTO accepted_email " +
            " SET email = :email, created = :created " +
            " ON DUPLICATE KEY UPDATE id=id",
            nativeQuery = true)
    void insertOnDuplicateKeyDoNothing(String email, Instant created);

    @Modifying
    @Query(value = "" +
            "update user_acc u " +
            "    inner join ( " +
            "        select u.id as userId, (min(accepted.id) is not null) as hasAcceptedAddress " +
            "        from user_acc u " +
            "                 left join user_email_address uea on u.id = uea.user_id " +
            "                 left join accepted_email accepted on uea.email = accepted.email " +
            "        where u.id in(:userIds) " +
            "        group by u.id " +
            "    ) as acceptedAddress on u.id = acceptedAddress.userId " +
            "set u.is_accepted_by_email = acceptedAddress.hasAcceptedAddress",
            nativeQuery = true)
    void recalculateByUserId(Collection<Long> userIds);

    default void recalculateByUserId(long userId) {
        recalculateByUserId(List.of(userId));
    }

    @Modifying
    @Query(value = "" +
            "update user_acc u " +
            "    inner join ( " +
            "        select u.id as userId, (min(accepted.id) is not null) as hasAcceptedAddress " +
            "        from user_acc u " +
            "                 left join user_email_address uea on u.id = uea.user_id " +
            "                 left join accepted_email accepted on uea.email = accepted.email " +
            "        where uea.email in(:emails) or accepted.email in(:emails) " +
            "        group by u.id " +
            "    ) as acceptedAddress on u.id = acceptedAddress.userId " +
            "set u.is_accepted_by_email = acceptedAddress.hasAcceptedAddress",
            nativeQuery = true)
    void recalculateByEmail(Collection<String> emails);

    default void recalculateByEmail(String email) {
        recalculateByEmail(List.of(email));
    }

    @Modifying
    @Query(value = "" +
            "update user_acc u " +
            "    inner join ( " +
            "        select u.id as userId, (min(accepted.id) is not null) as hasAcceptedAddress " +
            "        from user_acc u " +
            "                 left join user_email_address uea on u.id = uea.user_id " +
            "                 left join accepted_email accepted on uea.email = accepted.email " +
            "        group by u.id " +
            "    ) as acceptedAddress on u.id = acceptedAddress.userId " +
            "set u.is_accepted_by_email = acceptedAddress.hasAcceptedAddress",
            nativeQuery = true)
    void recalculateAll();
}
