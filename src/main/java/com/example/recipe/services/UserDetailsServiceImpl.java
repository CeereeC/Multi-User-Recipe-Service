package com.example.recipe.services;

import com.example.recipe.dao.UserRepository;
import com.example.recipe.entities.User;
import com.example.recipe.entities.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + email);
        }

        return new UserDetailsImpl(user.get());
    }

}