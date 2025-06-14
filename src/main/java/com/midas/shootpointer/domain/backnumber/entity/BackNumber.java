package com.midas.shootpointer.domain.backnumber.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BackNumber {
    @Column(name = "back_number",nullable = false)
    private Integer backNumber;
}
