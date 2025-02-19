package ggomg.imageuploadpublisher.outbox.repository

import ggomg.imageuploadpublisher.outbox.entity.OutboxEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OutboxRepository : JpaRepository<OutboxEntity, Long>, OutboxRepositoryCustom {

    @Query(
        value = "SELECT * FROM outbox_entity WHERE status = 'PENDING' ORDER BY id LIMIT 10 FOR UPDATE SKIP LOCKED",
        nativeQuery = true
    )
    fun findTop10PendingQuery(): List<OutboxEntity>
}
