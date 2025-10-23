package com.midas.shootpointer.domain.backnumber.business;

import com.midas.shootpointer.domain.backnumber.entity.BackNumberEntity;
import com.midas.shootpointer.domain.backnumber.helper.BackNumberHelper;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.memberbacknumber.helper.MemberBackNumberHelper;
import com.midas.shootpointer.infrastructure.openCV.service.OpenCVClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class BackNumberManager {
    private final BackNumberHelper backNumberHelper;
    private final MemberBackNumberHelper memberBackNumberHelper;
    private final OpenCVClient openCVClient;

    @Transactional
    public Integer create(BackNumberEntity backNumberEntity, MultipartFile image, Member member) {
        //1.요청 등 번호가 있는지 확인 -> 없으면 저장.
        backNumberEntity = backNumberHelper.findOrElseGetBackNumber(backNumberEntity.getBackNumber());

        //2. 중간테이블 저장.
        memberBackNumberHelper.findOrElseGetMemberBackNumber(member, backNumberEntity);

        //3. OpenCv 사진 전송.

        openCVClient.sendBackNumberInformation(member.getMemberId(), backNumberEntity.getBackNumber().getNumber(), image);

        return backNumberEntity.getBackNumber().getNumber();
    }
}
