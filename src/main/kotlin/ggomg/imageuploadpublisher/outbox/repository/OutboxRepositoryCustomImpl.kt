package ggomg.imageuploadpublisher.outbox.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import ggomg.imageuploadpublisher.outbox.entity.OutboxEntity
import ggomg.imageuploadpublisher.outbox.entity.OutboxStatus
import ggomg.imageuploadpublisher.outbox.entity.QOutboxEntity.outboxEntity
import jakarta.persistence.EntityManager

class OutboxRepositoryCustomImpl(
    entityManager: EntityManager
) : OutboxRepositoryCustom {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    override fun findByStatus(status: OutboxStatus): List<OutboxEntity> {
        return queryFactory
            .select(outboxEntity)
            .from(outboxEntity)
            .where(outboxEntity.status.eq(status))
            .fetch()
    }
}
