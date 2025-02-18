package ggomg.imageuploadpublisher.outbox.entity

import jakarta.persistence.*

@Entity
class OutboxEntity(
    val message: String,
    @Enumerated(EnumType.STRING)
    val status: OutboxStatus,
) : BaseEntity()

enum class OutboxStatus {
    PENDING,
    COMPLETED,
    FAILED,
}