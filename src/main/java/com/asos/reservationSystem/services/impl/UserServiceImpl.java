package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.auth.AuthenticationService;
import com.asos.reservationSystem.exception.CustomException;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AuthenticationService authenticationService, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> getUser(Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return userRepository.findById(user.getId());
    }

    public void saveUserPhoto(Optional<User> user, MultipartFile photoFile){
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Set image: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if (photoFile != null && !photoFile.isEmpty()) {
            try{
                byte[] photoBytes = photoFile.getBytes();
                Blob photoBlob = new SerialBlob(photoBytes);
                user.get().setPhoto(photoBlob);
                userRepository.save(user.get());
            }catch (Exception e){
                throw new CustomException("The image could not be loaded.", "Set image: The image could not be loaded.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    public byte[] getUserPhoto(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Get image: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if (user.get().getPhoto() == null) {
            throw new CustomException("Image not found for user.", "Get image: Image not found for user.", HttpStatus.NOT_FOUND);
        }
        try {
            return user.get().getPhoto().getBytes(1, (int) user.get().getPhoto().length());
        }catch (Exception e){
            e.printStackTrace();  // This prints the stack trace to the console
            throw new CustomException("The image could not be loaded.", "Set image: The image could not be loaded.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void removeUserPhoto(Optional<User> user) {
        if (user.isEmpty()) {
            throw new CustomException("User does not exist.",
                    "Remove image: User does not exist.", HttpStatus.NOT_FOUND);
        }
        try {
            user.get().setPhoto(null);  // Set the photo to null to remove it
            userRepository.save(user.get());
        } catch (Exception e) {
            throw new CustomException("The image could not be removed.", 
                    "Remove image: The image could not be removed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void setRole(User user, Role role) {
        userRepository.findUserByEmail(user.getEmail()).ifPresentOrElse(
                u -> {
                    u.setRole(role);
                    userRepository.save(u);
                },
                () -> {
                    throw new CustomException("User does not exist.",
                            "Set status: User with email: " + user.getEmail() + " does not exist.", HttpStatus.NOT_FOUND);
                }
        );
    }

    @Override
    public void updateProfile(Optional<User> user, User userData) {
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Update profile: User does not exist.", HttpStatus.NOT_FOUND);
        }
        user.get().setDegree(userData.getDegree());
        user.get().setFirstName(userData.getFirstName());
        user.get().setLastName(userData.getLastName());
        try{
            user.get().setPhoneNumber(userData.getPhoneNumber());
        }catch (Exception e){
            throw new CustomException("Wrong format of phone number.", "Update profile: Wrong format of phone number.", HttpStatus.BAD_REQUEST);
        }
        user.get().setDescription(userData.getDescription());
        userRepository.save(user.get());
    }

    @Override
    public void updateEmail(Optional<User> user, String oldEmail, String email, String password) {
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Update email: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(Objects.equals(user.get().getEmail(), email)){
            throw new CustomException("New email is equal to actual email.", "Update email: New email is equal to actual email.", HttpStatus.BAD_REQUEST);
        }
        else if(!Objects.equals(oldEmail, user.get().getEmail())){
            throw new CustomException("Old email is not equal to actual email.", "Update email: Old email is not equal to actual email.", HttpStatus.BAD_REQUEST);
        }
         else if (passwordEncoder.matches(password, user.get().getPassword())) {
            user.get().setEmail(email);
            userRepository.save(user.get());
            authenticationService.revokeAllUserTokens(user.get());
        }
        else {
            throw new CustomException("Password is incorrect.",
                    "Update email: Password is incorrect for user with id: " + user.get().getId() + ".", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void updatePassword(Optional<User> user, String newPassword, String oldPassword) {
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Update password: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(Objects.equals(newPassword, oldPassword)){
            throw new CustomException("New password is qual to old password.", "Update password: New password is qual to old password.", HttpStatus.BAD_REQUEST);
        }
        else if (passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            user.get().setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user.get());
            authenticationService.revokeAllUserTokens(user.get());
        } else {
            throw new CustomException("Password is incorrect.",
                    "Update password: Password is incorrect for user with id: " + user.get().getId() + ".", HttpStatus.BAD_REQUEST);
        }
    }
}
