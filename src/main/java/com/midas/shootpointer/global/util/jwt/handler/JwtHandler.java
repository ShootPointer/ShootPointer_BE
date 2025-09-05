package com.midas.shootpointer.global.util.jwt.handler;

import com.midas.shootpointer.domain.member.entity.Member;
import java.util.UUID;

public interface JwtHandler {
	String createAccessToken(Member member);
	String createRefreshToken(String email);
	String getEmailFromToken(String token);
	String getNicknameFromToken(String token);
	UUID getMemberIdFromToken(String token);
	boolean validateToken(String token);
}
