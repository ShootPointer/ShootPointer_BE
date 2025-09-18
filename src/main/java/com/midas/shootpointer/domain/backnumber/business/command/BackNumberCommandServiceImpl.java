package com.midas.shootpointer.domain.backnumber.business.command;

import com.midas.shootpointer.domain.backnumber.business.BackNumberManager;
import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BackNumberCommandServiceImpl implements BackNumberCommandService{
   private final BackNumberManager backNumberManager;

    @Override
    public Integer create(Member member, BackNumberEntity backNumberEntity, MultipartFile image) {
        return backNumberManager.create(backNumberEntity,image,member);
    }
}
