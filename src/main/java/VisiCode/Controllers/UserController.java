package VisiCode.Controllers;

import VisiCode.Domain.Exceptions.UserException;
import VisiCode.Domain.User;
import VisiCode.Domain.UserRepository;
import VisiCode.Payload.LoginRequest;
import VisiCode.Payload.MessageResponse;
import VisiCode.Payload.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController extends UserAuthenticable {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/login")
    public MessageResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return MessageResponse.makeMessage("User log in successfully!");
    }

    @PostMapping("/create")
    public MessageResponse registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        Optional<User> findUser = userRepository.findByUsername(signupRequest.getUsername());

        if (findUser.isEmpty()) {
            // Create new user's account
            User user = new User(signupRequest.getUsername(), encoder.encode(signupRequest.getPassword()), new HashSet<>());
            userRepository.save(user);
            return MessageResponse.makeMessage("User registered successfully!");
        } else {
            throw UserException.duplicateUser(signupRequest.getUsername());
        }
    }
}
