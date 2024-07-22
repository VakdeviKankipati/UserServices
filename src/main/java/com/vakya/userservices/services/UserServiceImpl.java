package com.vakya.userservices.services;

import com.vakya.userservices.models.Token;
import com.vakya.userservices.models.User;
import com.vakya.userservices.repositories.TokenRepository;
import com.vakya.userservices.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    protected BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.userRepository=userRepository;
        this.tokenRepository=tokenRepository;
    }
    @Override
    public User signUp(String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = null;
        if (optionalUser.isPresent()){
            //navigate them to login
        }else{
            //create new user Object
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setHashedPassword(bCryptPasswordEncoder.encode(password));
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = null;

        if (optionalUser.isEmpty()) {
            //signup method.
        } else {
            user = optionalUser.get();

            if (!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
                return null;
            }

            //Generate the token.
            Token token = createToken(user);
            token = tokenRepository.save(token);

            return token;
        }

        return null;
    }

    private Token createToken(User user) {
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plus(30, ChronoUnit.DAYS);

        Date expiryAt = Date.from(thirtyDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());

        token.setExpiryAt(expiryAt);

        return token;
    }

    @Override
    public User validateToken(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                token,
                false,
                new Date()
        );

        if (tokenOptional.isEmpty()) {
            //Throw some exception
            return null;
        }

        return tokenOptional.get().getUser();
    }

    @Override
    public void logout(String token) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeleted(token,false);

        if(optionalToken.isEmpty()){

        }
         Token token1 = optionalToken.get();
        token1.setDeleted(true);
        tokenRepository.save(token1);
    }
}
