package com.pagely.userservice.temp.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * TODO ⚠️ 임시 구현 :
 * <p>
 * 공통 모듈이 배포되면 이 클래스를 제거하고 공통 모듈 import( com.pagely.common.audit.BaseEntity)
 */
@Getter
@MappedSuperclass
@Access(AccessType.FIELD)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    protected LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    protected UUID createdBy;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    protected LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", insertable = false)
    protected UUID updatedBy;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    protected UUID deletedBy;

    /**
     * Soft delete 처리 (이미 삭제된 경우 무시)
     */
    protected void delete(UUID deletedBy) {
        if (this.deletedAt != null) {
            return;
        }
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }
}
