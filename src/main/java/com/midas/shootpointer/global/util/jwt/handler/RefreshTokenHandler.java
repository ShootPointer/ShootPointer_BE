package com.midas.shootpointer.global.util.jwt.handler;

import java.util.Optional;

public interface RefreshTokenHandler {
	void saveRefreshToken(String email, String refreshToken);
	Optional<String> getRefreshToken(String email);
	void deleteRefreshToken(String email);
}
