package com.laboschqpa.server.repo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Builder
@Service
public class Repos {
    public AccountJoinInitiationRepository accountJoinInitiationRepository;
    public ExternalAccountDetailRepository externalAccountDetailRepository;
    public GithubExternalAccountDetailRepository githubExternalAccountDetailRepository;
    public GoogleExternalAccountDetailRepository googleExternalAccountDetailRepository;
    public NewsPostRepository newsPostRepository;
    public ProfileDetailsRepository profileDetailsRepository;
    public UserAccRepository userAccRepository;
    public UserEmailAddressRepository userEmailAddressRepository;
    public TeamRepository teamRepository;
}
