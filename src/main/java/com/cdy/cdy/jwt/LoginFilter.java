package com.cdy.cdy.jwt;

import com.cdy.cdy.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    //authenticationmanager를 주입받고 이 매니저가 검증할수있게 dto를 넘김
    private final AuthenticationManager authenticationManager;


    //환경변수에 담아놓은 만료시간 뽑아오기
//    @Value("${jwt.expiration}")
//    private Long expiration;


    //로그인시 jwt가 이 서버에서 발행한게 맞는지 검증하는 로직이랑 jwt를 발급해주는 로직을 담당하는 유틸
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                           throws AuthenticationException {
        //클라이언트에게서 username,password 뽑아오기
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //authentication  매니저가 위 내용을 검증을 진행하고, 그걸 위해 loginfilter에서 매니저에게 넘겨줘야함
        //이때 그냥 넘기는게아니라 dto처럼 바구니에 담아 넘겨야 하는데 그게 usernamepasswordauthenticationtoken

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password, null);

        //매니저를 생성자로 주입받기
        //매니저의 authenticate의 메서드로 위 토큰을 매니저에게 넘겨야함

        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    //manager에서 검증하고 정보를 authentication에 담아서 successfulauthentication 메서드에 전달되어있는 상태.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();


        //로그인 비밀번호,아이디 확인하고 userdetailsservice를 통한 검증 끝났으면 그걸 userdetails에  담고
        // userdetails에 뽑아온 위 정보 토대로  jwt 토큰 생성
        String token = jwtUtil.createJwt(username,role,60 * 60 * 10L);

        //jwt 토큰을 header에 담아서 클라이언트에게 전달
        response.addHeader("Authorization","Bearer " + token);

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}
