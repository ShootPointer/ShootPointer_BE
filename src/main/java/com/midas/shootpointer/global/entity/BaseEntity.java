package com.midas.shootpointer.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity extends BaseTimeEntity{
    @Column(name = "is_deleted",nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted=false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void delete(){
        this.isDeleted=true;
        this.deletedAt=LocalDateTime.now();
    }

}