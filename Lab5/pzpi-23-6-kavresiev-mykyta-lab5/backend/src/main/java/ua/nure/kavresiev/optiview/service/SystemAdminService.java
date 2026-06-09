package ua.nure.kavresiev.optiview.service;

import ua.nure.kavresiev.optiview.entity.User;
import ua.nure.kavresiev.optiview.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemAdminService {

    private final UserRepository userRepository;

    public SystemAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDetails.getUsername());
        user.setRole(userDetails.getRole());
        user.setIsActive(userDetails.getIsActive());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }

    public User updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(newRole);
        return userRepository.save(user);
    }
}