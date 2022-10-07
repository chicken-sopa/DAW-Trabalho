package repository.jdbi.mappers

import domain.*
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import repository.jdbi.GameDbModel
import java.sql.ResultSet
import java.sql.SQLException

class GameMapper : RowMapper<GameDbModel> {
    override fun map(rs: ResultSet, ctx: StatementContext?): GameDbModel {
        println("Game Mapper")

        println(rs.getString("game_id"))
        println(rs.getString("p1"))
        println(rs.getString("p2"))
        println(rs.getString("rules"))
        println(rs.getString("board_dimensions"))

        val array: Array<Any> = arrayOf(rs.getArray("p1_missed_shots"))
        // println(rs.getArray("p1_fleet"))

        TODO()
    }
}

class GameRulesMapper : ColumnMapper<GameRules> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): GameRules {

        println("Game Rules mapper")
        println(r.getString(columnNumber))

        TODO()
        // return GameDbModel(r.getString(columnNumber))
    }
}

class FleetLayoutMapper : ColumnMapper<FleetLayout> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): FleetLayout {

        println("FleetLayout mapper")
        // println(r.getString("ships").extractArray())
        val bd_props = r.getString("board_dimensions").extractProps()
        val boardDimensions = BoardDimensions(bd_props[0].toInt(), bd_props[1].toInt())

        val ships_configuration: List<ShipConfiguration> =
            r.getString("ships_configuration")
                .extractArray().map {
                    val props = it.extractProps()
                    ShipConfiguration(props[0].toInt(), props[1].toInt())
                }

        val ships = r.getString(columnNumber).extractArray()[0].extractArray()[0].extractArray()
        println(ships)
        // println(ships_configuration)
        val validation = LayoutValidationSettings(boardDimensions, ships_configuration)
        // println(validation)

        TODO()
        // return GameDbModel(r.getString(columnNumber))
    }
}


class ShotSetMapper : ColumnMapper<Shot> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): Shot {

        println("Shot mapper")

        TODO()
        // return GameDbModel(r.getString(columnNumber))
    }
}

class BoardDimensionsMapper : ColumnMapper<BoardDimensions> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): BoardDimensions {
        val props = r.getString(columnNumber).extractProps()
        return BoardDimensions(props[0].toInt(), props[1].toInt())
    }
}



class A: ColumnMapper<Shot> {
    override fun map(r: ResultSet?, columnNumber: Int, ctx: StatementContext?): Shot {
        println("SHOT MAPPER")
        TODO("Not yet implemented")
    }
}


// UTILS - SEPARATE FILE LATER MAYBE LATER BE EXTENSION OF RESULTSET !!!!
fun String.extractProps(): List<String> {
    return this.removeSuffix(")").removePrefix("(").split(',')
}

fun String.extractArray(): List<String> {
    val temp = this.removeSuffix("}").removePrefix("{")
    val list = temp.split(",\"").map {
       it.removeSuffix("\"").removePrefix("\"")
    }
    return list
}
