package com.laboschqpa.server.api.service.admin;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.apierrordescriptor.AuthorityAdminApiError;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.exceptions.apierrordescriptor.AuthorityAdminException;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthorityAdminService {
    private final UserAccRepository userAccRepository;

    public Set<Authority> getUserAuthorities(long userAccId) {
        Optional<UserAcc> userAccOptional = userAccRepository.findByIdWithAuthorities(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundException("UserAcc not found with id: " + userAccId);

        return userAccOptional.get().getAuthorities();
    }

    public void deleteUserAuthority(long targetUserId, Authority authority, long initiatorUserId) {
        Optional<UserAcc> userAccOptional = userAccRepository.findByIdWithAuthorities(targetUserId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundException("UserAcc not found with id: " + targetUserId);
        UserAcc userAcc = userAccOptional.get();

        if (!userAcc.getAuthorities().removeIf(authority1 -> authority1.equals(authority))) {
            throw new AuthorityAdminException(AuthorityAdminApiError.THE_ALTERED_USER_DOES_NOT_HAVE_THE_AUTHORITY,
                    "User " + targetUserId + " doesn't have authority: " + authority.getStringValue() + "!", authority);
        }

        log.info("Removing user Authority: {}, targetUser: {}, initiatorUser: {}",
                authority.getStringValue(), targetUserId, initiatorUserId);
        userAccRepository.save(userAcc);
    }

    public void addUserAuthority(long targetUserId, Authority authority, long initiatorUserId) {
        Optional<UserAcc> userAccOptional = userAccRepository.findByIdWithAuthorities(targetUserId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundException("UserAcc not found with id: " + targetUserId);
        UserAcc userAcc = userAccOptional.get();

        if (userAcc.getAuthorities().stream().anyMatch(authority1 -> authority1.equals(authority))) {
            throw new AuthorityAdminException(AuthorityAdminApiError.THE_ALTERED_USER_ALREADY_HAS_THE_AUTHORITY,
                    "User " + targetUserId + " already has authority: " + authority.getStringValue() + "!", authority);
        }

        userAcc.getAuthorities().add(authority);

        log.info("Adding user Authority: {}, targetUser: {}, initiatorUser: {}",
                authority.getStringValue(), targetUserId, initiatorUserId);
        userAccRepository.save(userAcc);
    }
}
