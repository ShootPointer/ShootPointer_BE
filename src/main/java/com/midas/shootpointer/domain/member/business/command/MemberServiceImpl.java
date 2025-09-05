package com.midas.shootpointer.domain.member.business.command;

import com.midas.shootpointer.domain.member.dto.KakaoDTO;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberCommandRepository;
import com.midas.shootpointer.domain.member.repository.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	private final MemberQueryRepository memberQueryRepository;
	private final MemberCommandRepository memberCommandRepository;
	
	@Override
	@Transactional
	public Member findOrCreateMember(KakaoDTO kakaoDTO) {
		return memberQueryRepository.findByEmail(kakaoDTO.getEmail())
			.orElseGet(() -> createNewMember(kakaoDTO));
	}
	
	@Override
	public Member createNewMember(KakaoDTO kakaoDTO) {
		Member newMember = Member.builder()
			.email(kakaoDTO.getEmail())
			.username(kakaoDTO.getNickname())
			.build();
		return memberCommandRepository.save(newMember);
	}
}
