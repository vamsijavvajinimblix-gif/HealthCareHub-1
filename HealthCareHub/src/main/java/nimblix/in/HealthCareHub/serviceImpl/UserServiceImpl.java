package nimblix.in.HealthCareHub.serviceImpl;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.exception.UserNotFoundException;
import nimblix.in.HealthCareHub.model.User;
import nimblix.in.HealthCareHub.repository.UserRepository;
import nimblix.in.HealthCareHub.request.UserRegistrationRequest;
import nimblix.in.HealthCareHub.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationRequest request){

        if (userRepository.existsByEmail(request.getEmail().toLowerCase())){
            throw new UserNotFoundException("user with this email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        return userRepository.save(user);
    }
}
