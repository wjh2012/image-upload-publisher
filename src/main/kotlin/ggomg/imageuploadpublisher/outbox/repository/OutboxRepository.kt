package ggomg.imageuploadpublisher.outbox.repository

import ggomg.imageuploadpublisher.outbox.entity.OutboxEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxRepository : JpaRepository<OutboxEntity, Long>, OutboxRepositoryCustom