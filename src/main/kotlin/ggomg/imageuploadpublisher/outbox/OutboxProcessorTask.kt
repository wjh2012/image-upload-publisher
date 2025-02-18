package ggomg.imageuploadpublisher.outbox

import ggomg.imageuploadpublisher.outbox.repository.OutboxRepository
import org.springframework.stereotype.Component

@Component
class OutboxProcessorTask(
    private val outboxRepository: OutboxRepository,
) {

}