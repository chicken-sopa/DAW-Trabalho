package repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin

fun Jdbi.configure(): Jdbi {
    installPlugin(KotlinPlugin())

    return this
}