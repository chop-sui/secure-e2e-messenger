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

import java.util.ArrayList;
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
        user.setTodos(new ArrayList<>());
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

    public void addFriend(User user, User friend) {
        user.addFriend(friend);
        userRepository.save(friend);
    }

    public void removeFriend(User user, Integer friendId) {
        user.removeFriend(friendId);
        userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found."));
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User with id: " + id + " not found."));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addTodo(User user, String compliment) {
        user.addTodo(compliment);
        userRepository.save(user);
    }
}
