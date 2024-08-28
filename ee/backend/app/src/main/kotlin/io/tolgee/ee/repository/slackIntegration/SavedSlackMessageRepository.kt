package io.tolgee.ee.repository.slackIntegration

import io.tolgee.model.slackIntegration.SavedSlackMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Repository
interface SavedSlackMessageRepository : JpaRepository<SavedSlackMessage, Long> {
  @Modifying
  @Query(
    """
    delete from SavedSlackMessage sm where sm.createdAt < :cutoff
    """,
  )
  fun deleteOlderThan(cutoff: Date)

  @Query(
    """
    select sm from SavedSlackMessage sm where sm.createdAt < :cutoff
    """,
  )
  fun findOlderThan(cutoff: Date): List<SavedSlackMessage>

  fun findByKeyIdAndSlackConfigId(
    keyId: Long,
    configId: Long,
  ): List<SavedSlackMessage>

  @Query(
    """
    select sm from SavedSlackMessage sm
    join fetch sm.info info
    where sm.keyId in :keyIds and sm.slackConfig.id = :configId
    """,
  )
  fun findAllByKeyIdAndConfigId(
    keyIds: List<Long>,
    configId: Long,
  ): List<SavedSlackMessage>
}
