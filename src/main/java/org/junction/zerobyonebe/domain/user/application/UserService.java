package org.junction.zerobyonebe.domain.user.application;

import org.junction.zerobyonebe.domain.user.domain.User;
import org.junction.zerobyonebe.domain.user.dto.request.UserLoginRequest;
import org.junction.zerobyonebe.domain.user.dto.request.UserLoginResponse;
import org.junction.zerobyonebe.domain.user.dto.request.UserRegisterRequest;
import org.junction.zerobyonebe.domain.user.infrastructure.UserRepository;
import org.junction.zerobyonebe.global.exception.CustomException;
import org.junction.zerobyonebe.global.exception.ErrorCode;
import org.junction.zerobyonebe.global.security.jwt.JwtToken;
import org.junction.zerobyonebe.global.security.jwt.JwtTokenProvider;
import org.junction.zerobyonebe.global.util.RedisUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final RedisUtil redisUtil;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;

	private final Long EXPIRATION = 10 * 60L;
	private final Long REFRESH_TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60L;

	@Transactional
	public void register(UserRegisterRequest userRegisterRequest) {
		userRepository.findByEmail(userRegisterRequest.getEmail()).ifPresent(it -> {
			throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
		});

		userRepository.findByName(userRegisterRequest.getName()).ifPresent(it -> {
			throw new CustomException(ErrorCode.NICKNAME_DUPLICATION);
		});

		String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
		User user = User.create(userRegisterRequest.getEmail(), encodedPassword, userRegisterRequest.getName());
		userRepository.save(user);
	}


	@Transactional
	public UserLoginResponse login(UserLoginRequest userLoginRequest) {
		User user = userRepository.findByEmail(userLoginRequest.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword());

		try {
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
			redisUtil.setDataExpire(authentication.getName(), jwtToken.getRefreshToken(), REFRESH_TOKEN_EXPIRE_SECONDS);

			return UserLoginResponse.of(jwtToken);
		} catch (BadCredentialsException e) {
			log.error("login error: not valid password");
			throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
		} catch (Exception e) {
			log.error("server error: {} ", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public UserLoginResponse reissue(String refreshToken) {
		JwtToken newToken = jwtTokenProvider.refreshToken(refreshToken);
		return UserLoginResponse.of(newToken);
	}

	@Transactional
	public void logout(String token) {
		if (token == null && !token.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		String accessToken = token.substring(7);

		jwtTokenProvider.validateToken(accessToken);

		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
		String name = authentication.getName();

		if (redisUtil.existData(name)) {
			redisUtil.deleteData(name);
		} else {
			log.warn("logout: not exist refeshtoken");
		}

		Long remainingExpirationMillis = jwtTokenProvider.getExpiration(accessToken);
		if (remainingExpirationMillis > 0) {
			redisUtil.setDataExpire(accessToken, "logout", remainingExpirationMillis / 1000);
		}

		SecurityContextHolder.clearContext();
	}
}
