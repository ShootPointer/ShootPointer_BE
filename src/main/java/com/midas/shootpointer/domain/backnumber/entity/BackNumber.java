package com.midas.shootpointer.domain.backnumber.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
public class BackNumber {
    @Column(name = "back_number",nullable = false,unique = true)
    private Integer number;

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof BackNumber))return false;

        BackNumber that=(BackNumber) o;
        return Objects.equals(this.number,that.number);
    }

    @Override
    public int hashCode(){
        return Objects.hash(number);
    }
    public static BackNumber of(Integer backNumber){
        return new BackNumber(backNumber);
    }
}
