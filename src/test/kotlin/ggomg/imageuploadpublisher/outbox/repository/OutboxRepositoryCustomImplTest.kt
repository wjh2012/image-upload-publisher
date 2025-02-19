package ggomg.imageuploadpublisher.outbox.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import ggomg.imageuploadpublisher.outbox.entity.OutboxEntity
import ggomg.imageuploadpublisher.outbox.entity.OutboxStatus
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Executors

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OutboxRepositoryCustomImplTest {

    @Autowired
    private lateinit var outboxRepository: OutboxRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private lateinit var queryFactory: JPAQueryFactory

    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager

    @BeforeEach
    fun setup() {
        queryFactory = JPAQueryFactory(entityManager)

        val entity1 = OutboxEntity(message = "hello_c", status = OutboxStatus.COMPLETED)
        val entity2 = OutboxEntity(message = "hello_f", status = OutboxStatus.FAILED)

        entityManager.persist(entity1)
        entityManager.persist(entity2)
        entityManager.flush()

        (1..20).forEach {
            entityManager.persist(OutboxEntity(message = "hello$it", status = OutboxStatus.PENDING))
        }
        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `PENDING 상태 데이터 조회`() {
        // when
        val results = outboxRepository.findByStatus(OutboxStatus.PENDING)

        // then
        assertEquals(20, results.size)
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

    @Test
    fun `동시성 PENDING 상태 데이터 10개 조회`() {
        // 현재 테스트 트랜잭션이 활성화되어 있다면 데이터를 커밋하여 다른 트랜잭션에서 조회할 수 있도록 함
        if (TestTransaction.isActive()) {
            TestTransaction.flagForCommit()
            TestTransaction.end()
        }

        val transactionTemplate = TransactionTemplate(transactionManager)
        val executor = Executors.newFixedThreadPool(2)

        // 각 executor 스레드에서 새 트랜잭션을 시작하여 repository 호출
        val future1 = executor.submit<List<OutboxEntity>> {
            transactionTemplate.execute {
                outboxRepository.findTop10PendingQuery()
            } ?: emptyList()
        }
        val future2 = executor.submit<List<OutboxEntity>> {
            transactionTemplate.execute {
                outboxRepository.findTop10PendingQuery()
            } ?: emptyList()
        }

        val result1 = future1.get()
        val result2 = future2.get()

        assertThat(result1).hasSize(10)
        assertThat(result2).hasSize(10)

        // 두 결과에서 가져온 ID 집합이 서로 겹치지 않아야 함 (동시에 동일 데이터를 처리하지 않음)
        val result1Ids = result1.map { it.id }.toSet()
        val result2Ids = result2.map { it.id }.toSet()

        val intersection = result1Ids.intersect(result2Ids)
        assertThat(intersection).isEmpty()

        executor.shutdown()
    }

}
