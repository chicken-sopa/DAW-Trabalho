package domain

data class Author(
    val name: String,
    val github_profile: String
)

data class SystemInfo (
    val authors: List<Author>,
    val version: String
)