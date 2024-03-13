package com.banking.banking.service.implementation;

import com.banking.banking.entity.User;
import com.banking.banking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
//        return userRepository.findByEmail(user).orElseThrow(()->new UsernameNotFoundException(user+"not found"));
        Optional<User> email = userRepository.findByEmail(user);
        return new org.springframework.security.core.userdetails.User(email.get().getEmail(), email.get().getPassword(), new ArrayList<>());
    }
}
