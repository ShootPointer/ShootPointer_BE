package com.midas.shootpointer.domain.member.entity;

import com.midas.shootpointer.global.entity.BaseEntity;
import com.midas.shootpointer.global.util.encrypt.EncryptionHelper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
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
}
