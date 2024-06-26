package com.example.HiBuddy.domain.oauth.kakao;


import com.example.HiBuddy.domain.oauth.dto.TokenDto;
import com.example.HiBuddy.domain.oauth.jwt.JwtUtil;
import com.example.HiBuddy.domain.oauth.jwt.refreshtoken.RefreshToken;
import com.example.HiBuddy.domain.oauth.jwt.refreshtoken.RefreshTokenRepository;
import com.example.HiBuddy.domain.user.Status;
import com.example.HiBuddy.domain.user.Users;
import com.example.HiBuddy.domain.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final KakaoProvider kakaoProvider;

    public KakaoTokenResponse getAccessToken(String code){
        return kakaoProvider.getKakaoOAuthToken(code);
    }

    public TokenDto kakaoLogin(KakaoTokenResponse accessToken){
        String jwtAccessToken = "";
        String jwtRefreshToken = "";
        KakaoProfile kakaoProfile = kakaoProvider.getKakaoProfile(accessToken);

        String username = "kakao"+"_"+kakaoProfile.getId();

        Users kakaoUsers = Users.builder()
                .email(kakaoProfile.getKakao_account().getEmail())
                .status(Status.ENABLED)
                .username(username)
                .build();

        Users originUsers = usersRepository.findByUsername(kakaoUsers.getUsername()).orElse(null);

        if (originUsers == null) {
            System.out.println("새 사용자 로그인 처리");
            // 새 사용자 등록 로그 또는 처리
            jwtAccessToken = jwtUtil.createJwt("Authorization",kakaoUsers.getUsername(), kakaoUsers.getId(),86400000L);
            jwtRefreshToken = jwtUtil.createJwt("refreshToken",kakaoUsers.getUsername(), kakaoUsers.getId(), 604800000L);
            addRefreshToken(username,jwtRefreshToken,604800000L);
            usersRepository.save(kakaoUsers);
            System.out.println("Access Token: "+jwtAccessToken);
            System.out.println("Refresh Token: "+jwtRefreshToken);
        } else {
            // 기존 사용자 로그인 처리
            System.out.println("기존 사용자 로그인 처리");
            jwtAccessToken = jwtUtil.createJwt("Authorization",originUsers.getUsername(), originUsers.getId(), 86400000L);
            jwtRefreshToken = jwtUtil.createJwt("refreshToken",originUsers.getUsername(), originUsers.getId(), 604800000L);
            addRefreshToken(username,jwtRefreshToken,604800000L);;
            System.out.println("Access Token: "+jwtAccessToken);
            System.out.println("Refresh Token: "+jwtRefreshToken);
        }
        return TokenDto.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
    }

    private void addRefreshToken(String username, String refreshToken, Long expiredMs){
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refresh = new RefreshToken();
        refresh.setUsername(username);
        refresh.setRefresh(refreshToken);
        refresh.setExpiration(date.toString());

        refreshTokenRepository.save(refresh);
    }

}