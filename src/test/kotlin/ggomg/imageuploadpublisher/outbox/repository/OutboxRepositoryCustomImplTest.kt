package ggomg.imageuploadpublisher.outbox.repository

import ggomg.imageuploadpublisher.outbox.entity.OutboxEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.querydsl.jpa.impl.JPAQueryFactory
import ggomg.imageuploadpublisher.outbox.entity.OutboxStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class OutboxRepositoryCustomImplTest {

    @Autowired
    private lateinit var outboxRepository: OutboxRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var queryFactory: JPAQueryFactory

    @BeforeEach
    fun setup() {
        queryFactory = JPAQueryFactory(entityManager)

        val entity1 = OutboxEntity(message = "hello1", status = OutboxStatus.PENDING)
        val entity2 = OutboxEntity(message = "hello2", status = OutboxStatus.PENDING)
        val entity3 = OutboxEntity(message = "hello3", status = OutboxStatus.COMPLETED)
        val entity4 = OutboxEntity(message = "hello3", status = OutboxStatus.FAILED)

        entityManager.persist(entity1)
        entityManager.persist(entity2)
        entityManager.persist(entity3)
        entityManager.persist(entity4)
        entityManager.flush()
    }

    @Test
    fun `PENDING 상태 데이터 조회`() {
        // when
        val results = outboxRepository.findByStatus(OutboxStatus.PENDING)

        // then
        assertEquals(2, results.size)
        assertEquals(OutboxStatus.PENDING, results[0].status)
        assertEquals(OutboxStatus.PENDING, results[1].status)
    }

    @Test
    fun `COMPLETED 상태 데이터 조회`() {
        // when
        val results = outboxRepository.findByStatus(OutboxStatus.COMPLETED)

        // then
        assertEquals(1, results.size)
        assertEquals(OutboxStatus.COMPLETED, results[0].status)
    }

    @Test
    fun `FAILED 상태 데이터 조회`() {
        // when
        val results = outboxRepository.findByStatus(OutboxStatus.FAILED)

        // then
        assertEquals(1, results.size)
        assertEquals(OutboxStatus.FAILED, results[0].status)
    }

}
