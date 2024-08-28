package io.tolgee.model.enums

enum class ProjectPermissionType(val availableScopes: Array<Scope>) {
  NONE(arrayOf()),
  VIEW(
    arrayOf(
      Scope.TRANSLATIONS_VIEW,
      Scope.SCREENSHOTS_VIEW,
      Scope.ACTIVITY_VIEW,
      Scope.KEYS_VIEW,
    ),
  ),
  TRANSLATE(
    arrayOf(
      Scope.KEYS_VIEW,
      Scope.TRANSLATIONS_VIEW,
      Scope.TRANSLATIONS_EDIT,
      Scope.SCREENSHOTS_VIEW,
      Scope.ACTIVITY_VIEW,
      Scope.TRANSLATIONS_COMMENTS_ADD,
      Scope.TRANSLATIONS_COMMENTS_SET_STATE,
    ),
  ),
  REVIEW(
    arrayOf(
      Scope.KEYS_VIEW,
      Scope.TRANSLATIONS_VIEW,
      Scope.TRANSLATIONS_EDIT,
      Scope.SCREENSHOTS_VIEW,
      Scope.ACTIVITY_VIEW,
      Scope.TRANSLATIONS_COMMENTS_ADD,
      Scope.TRANSLATIONS_COMMENTS_SET_STATE,
      Scope.TRANSLATIONS_STATE_EDIT,
    ),
  ),
  EDIT(
    arrayOf(
      Scope.KEYS_VIEW,
      Scope.TRANSLATIONS_VIEW,
      Scope.TRANSLATIONS_EDIT,
      Scope.KEYS_EDIT,
      Scope.KEYS_DELETE,
      Scope.KEYS_CREATE,
      Scope.SCREENSHOTS_VIEW,
      Scope.SCREENSHOTS_UPLOAD,
      Scope.SCREENSHOTS_DELETE,
      Scope.ACTIVITY_VIEW,
      Scope.TRANSLATIONS_COMMENTS_ADD,
      Scope.TRANSLATIONS_COMMENTS_SET_STATE,
      Scope.TRANSLATIONS_COMMENTS_EDIT,
      Scope.TRANSLATIONS_STATE_EDIT,
      Scope.BATCH_PRE_TRANSLATE_BY_TM,
      Scope.BATCH_MACHINE_TRANSLATE,
      Scope.BATCH_JOBS_VIEW,
    ),
  ),
  MANAGE(
    arrayOf(Scope.ADMIN),
  ),
  ;

  companion object {
    fun getRoles(): Map<String, Array<Scope>> {
      val result = mutableMapOf<String, Array<Scope>>()
      values().forEach { value -> result[value.name] = value.availableScopes }
      return result.toMap()
    }
  }
}
