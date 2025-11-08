package com.midas.shootpointer.domain.member.controller;

import com.midas.shootpointer.domain.like.business.LikeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberQueryController {
    private final LikeManager likeManager;
}
