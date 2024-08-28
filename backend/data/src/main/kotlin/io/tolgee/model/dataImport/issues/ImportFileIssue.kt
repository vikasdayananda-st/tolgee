package io.tolgee.model.dataImport.issues

import io.tolgee.model.StandardAuditModel
import io.tolgee.model.dataImport.ImportFile
import io.tolgee.model.dataImport.issues.issueTypes.FileIssueType
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull

@Entity
class ImportFileIssue(
  @ManyToOne(optional = false)
  @field:NotNull
  var file: ImportFile,
  @Enumerated
  var type: FileIssueType = FileIssueType.ID_ATTRIBUTE_NOT_PROVIDED,
  @OneToMany(mappedBy = "issue", orphanRemoval = true)
  var params: MutableList<ImportFileIssueParam> = mutableListOf(),
) : StandardAuditModel()
