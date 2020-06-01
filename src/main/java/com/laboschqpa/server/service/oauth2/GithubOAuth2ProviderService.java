package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import com.laboschqpa.server.entity.account.externalaccountdetail.GithubExternalAccountDetail;
import com.laboschqpa.server.exceptions.authentication.DefectiveAuthProviderResponseAuthenticationException;
import com.laboschqpa.server.repo.externalaccountdetail.GithubExternalAccountDetailRepository;
import com.laboschqpa.server.service.apiclient.github.GitHubApiClient;
import com.laboschqpa.server.service.apiclient.github.GithubUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GithubOAuth2ProviderService extends AbstractOAuth2ProviderService {
    private final GitHubApiClient gitHubApiClient;
    private final GithubExternalAccountDetailRepository githubExternalAccountDetailRepository;

    @Override
    public ExtractedOAuth2UserRequestDataDto extractDataFromOauth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        final GithubUserInfoDto githubUserInfoDto;
        try {
            githubUserInfoDto = gitHubApiClient.getOAuth2UserInfo(oAuth2UserRequest.getAccessToken());
        } catch (Exception e) {
            throw new DefectiveAuthProviderResponseAuthenticationException("Cannot get OAuth2 UserInfo resource from GitHub", e);
        }
        if (!githubUserInfoDto.isValid()) {
            throw new DefectiveAuthProviderResponseAuthenticationException("GitHub OAuth2 UserInfo resource response is invalid!");
        }

        String firstName = null;
        String lastName = null;
        if (githubUserInfoDto.getName() != null) {
            String[] parts = githubUserInfoDto.getName().split(" ", 2);
            if (parts.length > 0)
                firstName = parts[0];
            if (parts.length > 1)
                lastName = parts[1];
        }

        GithubExternalAccountDetail githubExternalAccountDetail = new GithubExternalAccountDetail();
        githubExternalAccountDetail.setGithubId(githubUserInfoDto.getId());

        return new ExtractedOAuth2UserRequestDataDto(githubExternalAccountDetail, githubUserInfoDto.getEmail(), firstName, lastName, null);
    }

    @Override
    public UserAcc loadUserAccFromDbByExternalAccountDetail(ExternalAccountDetail externalAccountDetail) {
        Optional<GithubExternalAccountDetail> githubExternalAccountDetailOptional =
                githubExternalAccountDetailRepository.findByGithubId(((GithubExternalAccountDetail) externalAccountDetail).getGithubId());
        if (githubExternalAccountDetailOptional.isPresent()) {
            return githubExternalAccountDetailOptional.get().getUserAcc();
        } else {
            return null;
        }
    }

    @Override
    protected void saveExternalAccountDetail(ExternalAccountDetail externalAccountDetail) {
        githubExternalAccountDetailRepository.save((GithubExternalAccountDetail) externalAccountDetail);
    }
}
