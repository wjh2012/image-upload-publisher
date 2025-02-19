package ggomg.imageuploadpublisher.outbox.entity

import jakarta.persistence.*

@Entity
class OutboxEntity(
    @Column
    val message: String,
    @Enumerated(EnumType.STRING)
    val status: OutboxStatus,
) : BaseEntity()

enum class OutboxStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    EXPIRED,
    CANCELED,
    RETRYING
}