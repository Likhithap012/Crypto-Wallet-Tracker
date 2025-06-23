package com.gevernova.crypto_wallet_tracker.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ICustomUserDetailsService {
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
