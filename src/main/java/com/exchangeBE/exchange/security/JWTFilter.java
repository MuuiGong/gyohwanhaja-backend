package com.exchangeBE.exchange.security;

import com.exchangeBE.exchange.entity.User.CustomUserDetails;
import com.exchangeBE.exchange.entity.User.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorizationHeader.substring(7); // "Bearer " 이후의 토큰
        if(jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if(jwtUtils.isExpired(jwtToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token is expired");
            response.getWriter().flush();
            return;
        }

        String username = jwtUtils.getUsername(jwtToken);
        String role = jwtUtils.getRole(jwtToken);

        User user = new User();
        user.setNickname(username);
        user.setPassword("temppassword"); // 비밀번호는 실제로 사용하지 않으므로 임시값 설정

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    // 얘가 true면 필터가 작동하지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/auth") || path.startsWith("/swagger") || path.startsWith("/public");
    }

}
