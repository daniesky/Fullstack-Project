package ntnu.idatt2105.project.backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.model.AuthenticationRequest;
import ntnu.idatt2105.project.backend.dto.AuthenticationResponse;
import ntnu.idatt2105.project.backend.model.RegisterRequest;
import ntnu.idatt2105.project.backend.exceptions.UserAlreadyExistsException;
import ntnu.idatt2105.project.backend.service.AuthenticationService;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Login Controller", description = "Controller to handle user login")
public class LoginController {

    private final AuthenticationService authenticationService;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response){
        try {
            AuthenticationResponse authResponse = authenticationService.register(request);
            response.addCookie(getCookie(authResponse));

            return ResponseEntity.ok(authResponse);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().errorMessage(e.getMessage()).build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request){
        try {
            AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest);
            Cookie cookie = getCookie(authResponse);
            cookie.setDomain(request.getServerName());

            response.addHeader("Set-Cookie", cookieToHeaderWithSameSite(response, cookie, "strict"));

            return ResponseEntity.ok(authResponse);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().errorMessage(e.getMessage()).build());
        }
    }

    public Cookie getCookie(AuthenticationResponse authResponse){
        // Set access token as an HttpOnly cookie
        Cookie accessTokenCookie = new Cookie("myMarketPlaceAccessToken", authResponse.getToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(5 * 60); // 5 minutes
        return accessTokenCookie;
    }

    private String cookieToHeaderWithSameSite(HttpServletResponse response, Cookie cookie, String sameSite) {
        return cookie.getName() + "=" + cookie.getValue() + "; Path=" + cookie.getPath() + "; Max-Age=" + cookie.getMaxAge() + "; HttpOnly; SameSite=" + sameSite;
    }
    /*
    @PostMapping
    @Operation(summary = "User login", description = "Authenticates a user and returns the user object if successful")
    public ResponseEntity<User> login(@RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.ok(existingUser);
        }
        return ResponseEntity.status(401).build();
    }


     */
}