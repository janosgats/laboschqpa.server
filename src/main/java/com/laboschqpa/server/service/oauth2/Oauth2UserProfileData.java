package com.laboschqpa.server.service.oauth2;

import com.laboschqpa.server.entity.account.externalaccountdetail.ExternalAccountDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2UserProfileData {
    private ExternalAccountDetail externalAccountDetail;
    private String emailAddress;

    private String firstName;
    private String lastName;
    private String nickName;
    private String profilePicUrl;
}
