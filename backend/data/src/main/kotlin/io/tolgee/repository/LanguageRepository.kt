package io.tolgee.repository

import io.tolgee.dtos.cacheable.LanguageDto
import io.tolgee.model.Language
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LanguageRepository : JpaRepository<Language, Long> {
  @Query(
    """
    select l
    from Language l
    where l.name = :name and l.project = :project and l.deletedAt is null
  """,
  )
  fun findByNameAndProject(
    name: String?,
    project: io.tolgee.model.Project,
  ): Optional<Language>

  @Query(
    """
    select l
    from Language l
    where l.project.id = :projectId and l.deletedAt is null
  """,
  )
  fun findAllByProjectId(projectId: Long?): Set<Language>

  @Query(
    """
    select new io.tolgee.dtos.cacheable.LanguageDto(
      l.id,
      l.name,
      l.tag,
      l.originalName,
      l.flagEmoji,
      l.aiTranslatorPromptDescription,
      coalesce((l.id = l.project.baseLanguage.id), false)
    )
    from Language l
    where l.project.id = :projectId and l.deletedAt is null
  """,
  )
  fun findAllByProjectId(
    projectId: Long?,
    pageable: Pageable,
  ): Page<LanguageDto>

  fun findAllByTagInAndProjectId(
    tag: Collection<String>,
    projectId: Long,
  ): List<Language>

  fun deleteAllByProjectId(projectId: Long?)

  @Query(
    """
    select l
    from Language l
    where l.project.id = :projectId and l.id in :languageIds and l.deletedAt is null
  """,
  )
  fun findAllByProjectIdAndIdInOrderById(
    projectId: Long,
    languageIds: List<Long>,
  ): List<Language>

  @Query(
    """
    select l
    from Language l
    where l.project.id = :projectId and l.id in :languageId and l.deletedAt is null
  """,
  )
  fun find(
    languageId: Long,
    projectId: Long,
  ): Language?

  @Query(
    """
    select l
    from Language l
    where l.id = :languageId and l.deletedAt is null
  """,
  )
  fun find(languageId: Long): Language?

  @Query(
    """
    select new io.tolgee.dtos.cacheable.LanguageDto(
      l.id,
      l.name,
      l.tag,
      l.originalName,
      l.flagEmoji,
      l.aiTranslatorPromptDescription,
      coalesce((l.id = l.project.baseLanguage.id), false)
    )
    from Language l where l.project.id = :projectId and l.deletedAt is null
  """,
  )
  fun findAllDtosByProjectId(projectId: Long): List<LanguageDto>
}
