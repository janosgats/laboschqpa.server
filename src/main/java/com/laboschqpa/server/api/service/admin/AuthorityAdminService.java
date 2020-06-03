package com.laboschqpa.server.api.service.admin;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.apierrordescriptor.AuthorityAdminApiError;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.exceptions.apierrordescriptor.AuthorityAdminException;
import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.UserAccRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorityAdminService {
    private final UserAccRepository userAccRepository;

    public Set<Authority> getUserAuthorities(Long userAccId) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundException("UserAcc not found with id: " + userAccId);

        return userAccOptional.get().getAuthorities();
    }

    public void deleteUserAuthority(Long userAccId, Authority authority) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundException("UserAcc not found with id: " + userAccId);
        UserAcc userAcc = userAccOptional.get();

        if (!userAcc.getAuthorities().removeIf(authority1 -> authority1.equals(authority))) {
            throw new AuthorityAdminException(AuthorityAdminApiError.THE_ALTERED_USER_DOES_NOT_HAVE_THE_AUTHORITY,
                    "User " + userAccId + " doesn't have authority: " + authority.getStringValue() + "!", authority);
        }

        userAccRepository.save(userAcc);
    }

    public void addUserAuthority(Long userAccId, Authority authority) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundException("UserAcc not found with id: " + userAccId);
        UserAcc userAcc = userAccOptional.get();

        if (userAcc.getAuthorities().stream().anyMatch(authority1 -> authority1.equals(authority))) {
            throw new AuthorityAdminException(AuthorityAdminApiError.THE_ALTERED_USER_ALREADY_HAS_THE_AUTHORITY,
                    "User " + userAccId + " already has authority: " + authority.getStringValue() + "!", authority);
        }

        userAcc.getAuthorities().add(authority);

        userAccRepository.save(userAcc);
    }
}
