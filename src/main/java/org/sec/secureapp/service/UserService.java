package org.sec.secureapp.service;

import lombok.RequiredArgsConstructor;
import org.sec.secureapp.dto.UserDto;
import org.sec.secureapp.entity.Role;
import org.sec.secureapp.entity.User;
import org.sec.secureapp.repository.RoleRepository;
import org.sec.secureapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.username());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.password()));

        Role role = roleRepository.findByName("ROLE_ADMIN");

        if (role == null) {
            role = validateRole();
        }

        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    private Role validateRole(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    public List<Object> isUserPresent(UserDto userDto) {
        boolean userExists = false;
        String message = null;
        Optional<User> existingUserEmail = userRepository.findByUsername(userDto.username());

        if (existingUserEmail.isPresent()){
            userExists = true;
            message = "Username already exists";
        }

        return Arrays.asList(userExists, message);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found."));
    }

}
