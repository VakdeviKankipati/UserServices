package com.vakya.userservices.services;

import com.vakya.userservices.models.Token;
import com.vakya.userservices.models.User;
import org.springframework.stereotype.Service;


public interface UserService {

    User signUp(String name, String email, String password);

    Token login(String email, String password);

    User validateToken(String token);

    void logout(String token);

    User getUserDetails(Long userId);


}
