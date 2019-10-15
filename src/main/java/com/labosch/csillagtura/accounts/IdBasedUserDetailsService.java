package com.labosch.csillagtura.accounts;

import com.labosch.csillagtura.entity.User;
import com.labosch.csillagtura.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdBasedUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Long userId = null;
        try {
            userId = Long.parseLong(username);
        } catch (Exception e) {
            throw new RuntimeException("UserId is not numeric!");
        }
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(optionalUser.get());
    }
}