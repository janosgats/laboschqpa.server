package com.labosch.csillagtura.accounts.extractor;

import com.labosch.csillagtura.entity.User;
import com.labosch.csillagtura.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;
import java.util.Optional;

public class GooglePrincipalExtractor implements PrincipalExtractor {

    @Autowired
    UserRepository userRepository;

    @Override
    public Object extractPrincipal(Map<String, Object> map) {

        if (!map.containsKey("email"))
            throw new RuntimeException("Missing email field from oauth response!");

        Optional<User> optionalUser = userRepository.findByEmail(map.get("email").toString());

        if (optionalUser.isEmpty())
            throw new RuntimeException("User not found with this email!");

        return optionalUser.get();
    }
}
