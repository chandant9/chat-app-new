package com.zack.projects.chatapp.service;

import com.zack.projects.chatapp.VO.ProfileResponseTemplate;
import com.zack.projects.chatapp.VO.UserOnlineStatusResponseTemplate;
import com.zack.projects.chatapp.entity.User;
import com.zack.projects.chatapp.exception.UserNameExistsException;
import com.zack.projects.chatapp.exception.UserNameNotFoundException;
import com.zack.projects.chatapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatappUserService {

    @Autowired
    private UserRepository userRepository;

    public ProfileResponseTemplate saveUser(User user) throws UserNameExistsException {
        log.info(String.format("Checking if username [%s] already exists", user.getUsername()));
        boolean userNameExists = userRepository.existsById(user.getUsername());

        if(userNameExists) {
            log.info(String.format("username [%s] is taken", user.getUsername()));
            throw new UserNameExistsException(String.format("Username [%s] is taken", user.getUsername()));
        }

        log.info(String.format("username [%s] is available: ", user.getUsername()));

        log.info("Activating user account");
        activateUserAccount(user);

        log.info(String.format("Adding user: [%s]", user));
        userRepository.save(user);

        log.info(String.format("Creating response"));
        ProfileResponseTemplate profileResponseTemplate = new ProfileResponseTemplate(user);

        return profileResponseTemplate;
    }

    public ProfileResponseTemplate getUserProfile(String username) throws UserNameNotFoundException {

        User user = findUserByUsername(username);

        log.info(String.format("Generating [%s] profile", username));
        return new ProfileResponseTemplate(user);
    }

    public List<ProfileResponseTemplate> getUsersProfiles() {

        List<ProfileResponseTemplate> usersProfiles = new ArrayList<>();

        log.info(String.format("Retrieving users profiles"));
        userRepository.findAll()
                .stream()
                .forEach(user ->
                        usersProfiles.add(new ProfileResponseTemplate(user)));

        return usersProfiles;
    }

    public UserOnlineStatusResponseTemplate setUserOnline(String username) throws UserNameNotFoundException {

        User user = findUserByUsername(username);

        log.info(String.format("Setting user [%s] online", username));
        user.setOnline(true);

        log.info(String.format("Generating [%s] online status", username));
        UserOnlineStatusResponseTemplate userOnlineStatusResponseTemplate =
                new UserOnlineStatusResponseTemplate(user.getUsername(), user.isOnline());

        log.info(String.format("Saving user"));
        userRepository.save(user);

        return userOnlineStatusResponseTemplate;
    }

    public UserOnlineStatusResponseTemplate setUserOffline(String username) throws UserNameNotFoundException {

        User user = findUserByUsername(username);

        log.info(String.format("Setting user [%s] online", username));
        user.setOnline(false);

        log.info(String.format("Generating [%s] online status", username));
        UserOnlineStatusResponseTemplate userOnlineStatusResponseTemplate =
                new UserOnlineStatusResponseTemplate(user.getUsername(), user.isOnline());

        log.info(String.format("Saving user"));
        userRepository.save(user);

        return userOnlineStatusResponseTemplate;
    }

    public UserOnlineStatusResponseTemplate getUserStatus(String username) throws UserNameNotFoundException {

        User user = findUserByUsername(username);

        log.info(String.format("Generating [%s] online status", username));
        UserOnlineStatusResponseTemplate userOnlineStatusResponseTemplate =
                new UserOnlineStatusResponseTemplate(user.getFirstName(), user.isOnline());

        return userOnlineStatusResponseTemplate;
    }

    private void activateUserAccount(User user) {
        log.info(String.format("Activating User [%s] account", user));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
    }

    private User findUserByUsername(String username) throws UserNameNotFoundException {
        log.info(String.format("Looking for username: [%s]", username));
        return userRepository.findById(username)
                .orElseThrow(() -> {
                    log.info(String.format("Username [%s] does not exist", username));
                    return new UserNameNotFoundException(String.format("Username [%s] not found", username));
                });

    }

}