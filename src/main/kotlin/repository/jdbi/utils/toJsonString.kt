package repository.jdbi.utils

// Implement .fromString on each domain class
fun Any.toJsonString() = gson().toJson(this)
