package repository.jdbi.utils

// Implement .fromString on each domain class
fun Any.toJsonString(): String = gson().toJson(this)
