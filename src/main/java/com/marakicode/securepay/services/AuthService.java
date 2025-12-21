package com.marakicode.securepay.services;

import com.marakicode.securepay.config.JwtConfig;
import com.marakicode.securepay.dtos.JwtResponse;
import com.marakicode.securepay.dtos.LoginRequest;
import com.marakicode.securepay.dtos.UserDto;
import com.marakicode.securepay.exceptions.InvalidOrExpiredTokenException;
import com.marakicode.securepay.exceptions.UserNotFoundException;
import com.marakicode.securepay.mappers.UserMapper;
import com.marakicode.securepay.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;


    public JwtResponse login(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Send refresh token as httpOnly cookie for security reason
        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setPath("/auth/refresh");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);

        response.addCookie(cookie);

        return new JwtResponse(accessToken.toString());
    }

    public UserDto me() {
        var user = getCurrentUser();
        if (user == null)
            throw new UserNotFoundException();

        return userMapper.toDto(user);
    }

    public JwtResponse refresh(String token) {
        var jwt = jwtService.parseToken(token);
        if (jwt == null || jwt.isExpired()) {
            throw new InvalidOrExpiredTokenException();
        }
        var userId = jwt.getUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        var accessToken = jwtService.generateAccessToken(user);
        return new JwtResponse(accessToken.toString());
    }

    public com.marakicode.securepay.entities.User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        var userId = (Long) auth.getPrincipal();
        assert userId != null;
        return userRepository.findById(userId).orElse(null);
    }
}
