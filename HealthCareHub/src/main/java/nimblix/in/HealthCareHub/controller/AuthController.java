package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.exception.UserNotFoundException;
import nimblix.in.HealthCareHub.request.UserRegistrationRequest;
import nimblix.in.HealthCareHub.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request){
        try {
            userService.registerUser(request);
            return new ResponseEntity<>("User Registration Successfully", HttpStatus.CREATED);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e){
            return new ResponseEntity<>("Registration failed: "+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
