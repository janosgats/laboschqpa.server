package com.laboschcst.server.repo;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
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
