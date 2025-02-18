package ggomg.imageuploadpublisher.outbox.repository

import ggomg.imageuploadpublisher.outbox.entity.OutboxEntity
import ggomg.imageuploadpublisher.outbox.entity.OutboxStatus


interface OutboxRepositoryCustom {
    fun findByStatus(status: OutboxStatus): List<OutboxEntity>
}