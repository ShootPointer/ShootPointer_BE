package com.midas.shootpointer.domain.backnumber.business.command;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface BackNumberCommandService {
    Integer create(Member member, BackNumberEntity backNumberEntity, MultipartFile image);
}
