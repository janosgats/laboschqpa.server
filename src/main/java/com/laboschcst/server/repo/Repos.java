package com.laboschcst.server.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Repos {
    @Autowired
    public AccountJoinInitiationRepository accountJoinInitiationRepository;
    @Autowired
    public ExternalAccountDetailRepository externalAccountDetailRepository;
    @Autowired
    public GithubExternalAccountDetailRepository githubExternalAccountDetailRepository;
    @Autowired
    public GoogleExternalAccountDetailRepository googleExternalAccountDetailRepository;
    @Autowired
    public NewsPostRepository newsPostRepository;
    @Autowired
    public ProfileDetailsRepository profileDetailsRepository;
    @Autowired
    public UserAccRepository userAccRepository;
    @Autowired
    public UserEmailAddressRepository userEmailAddressRepository;
}
