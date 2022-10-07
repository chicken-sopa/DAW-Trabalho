package repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import repository.jdbi.mappers.*

fun Jdbi.configure(): Jdbi {
    installPlugin(KotlinPlugin())

    // Row Mappers
    // registerRowMapper(GameMapper())

    // Column Mappers
    /*registerColumnMapper(GameRulesMapper())
    registerColumnMapper(FleetLayoutMapper())
    registerColumnMapper(BoardDimensionsMapper())
    registerColumnMapper(A())*/

    return this
}