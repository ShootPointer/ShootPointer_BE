package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.member.business.command.MemberCommandService;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.global.dto.ApiResponse;
import com.midas.shootpointer.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agree")
public class MemberHighlightAgreeController {
    private final MemberCommandService memberCommandService;

    /*==========================
    *
    *MemberHighlightAgreeController
    * 하이라이트 정보 수집 동의 API
    * @return memberId
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 10. 13.
    *
    ==========================**/
    @PutMapping(value = "/highlight")
    public ResponseEntity<ApiResponse<UUID>> agree(){
        Member currentMember= SecurityUtils.getCurrentMember();
        return ResponseEntity.ok(ApiResponse.ok(memberCommandService.agree(currentMember)));
    }
}
