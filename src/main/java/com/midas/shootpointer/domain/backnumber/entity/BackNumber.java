package com.midas.shootpointer.domain.backnumber.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
public class BackNumber {
    @Column(name = "back_number",nullable = false)
    private Integer number;

    public static BackNumber of(Integer backNumber){
        return new BackNumber(backNumber);
    }
}
