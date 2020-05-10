package com.laboschqpa.server.api.service.admin;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.auth.Authority;
import com.laboschqpa.server.exceptions.ConflictingRequestDataApiException;
import com.laboschqpa.server.exceptions.ContentNotFoundApiException;
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
            throw new ContentNotFoundApiException("UserAcc not found with id: " + userAccId);

        return userAccOptional.get().getAuthorities();
    }

    public void deleteUserAuthority(Long userAccId, Authority authority) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundApiException("UserAcc not found with id: " + userAccId);
        UserAcc userAcc = userAccOptional.get();

        if (!userAcc.getAuthorities().removeIf(authority1 -> authority1.equals(authority)))
            throw new ConflictingRequestDataApiException("User " + userAccId + " doesn't have authority " + authority.getStringValue() + "!");

        userAccRepository.save(userAcc);
    }

    public void addUserAuthority(Long userAccId, Authority authority) {
        Optional<UserAcc> userAccOptional = userAccRepository.findById(userAccId);
        if (userAccOptional.isEmpty())
            throw new ContentNotFoundApiException("UserAcc not found with id: " + userAccId);
        UserAcc userAcc = userAccOptional.get();

        if (userAcc.getAuthorities().stream().anyMatch(authority1 -> authority1.equals(authority)))
            throw new ConflictingRequestDataApiException("User " + userAccId + " already has authority " + authority.getStringValue() + "!");

        userAcc.getAuthorities().add(authority);

        userAccRepository.save(userAcc);
    }
}
