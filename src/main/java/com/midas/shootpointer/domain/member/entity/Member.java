package com.midas.shootpointer.domain.member.entity;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.config.JpaAuditingConfig;
import com.midas.shootpointer.global.entity.BaseEntity;
import com.midas.shootpointer.global.exception.CustomException;
import com.midas.shootpointer.global.util.encrypt.EncryptionHelper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.context.annotation.Import;

import java.util.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Import(JpaAuditingConfig.class)
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "member_id", columnDefinition = "uuid")
    private UUID memberId;

    @Column(name = "member_name")
    @Convert(converter = EncryptionHelper.class)
    private String username;

    @Convert(converter = EncryptionHelper.class)
    private String email;

    //하이라이트 영상 집계 동의 여부
    @Column(name = "is_aggregation_agreed")
    private Boolean isAggregationAgreed=false;

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Member)) return false;
        Member other=(Member) o;
        return Objects.equals(this.memberId,other.memberId);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(memberId);
    }

     /*
    =========== [ 도메인-행위 ] ==============
     */
    public void agree(){
        //이미 동의한 경우
        if (Boolean.TRUE.equals(this.isAggregationAgreed)){
            throw new CustomException(ErrorCode.IS_AGGREGATION_TRUE);
        }
        this.isAggregationAgreed=true;
    }
}
