package domain.game

import Result
import domain.BoardDimensions
import domain.ShipConfiguration

typealias FleetLayoutValidation = Result<FleetError, Unit>

fun validateFleetLayout(
    fleet: Set<Ship>,
    boardDimensions: BoardDimensions,
    allowedShipConfigurations: List<ShipConfiguration>
): FleetLayoutValidation {
    // Make sure all ships match the allowedShipConfigurations
    allowedShipConfigurations.forEach { shipConfig ->
        if (shipConfig.quantity != fleet.count { ship -> ship.parts.size == shipConfig.ship_size })
            return Result.Failure(FleetError.InvalidShipsConfiguration())
    }

    // Make sure no ship part is out of bounds
    fleet.forEach { ship ->
        ship.parts.forEach{shipPart->
            if (shipPart.position.col !in 0 until boardDimensions.cols_num)
                return Result.Failure(FleetError.ShipPartOutOfBounds())

            if (shipPart.position.row !in 0 until boardDimensions.rows_num)
                return Result.Failure(FleetError.ShipPartOutOfBounds())
        }
    }

    // Make sure ships don't have repeated parts (same coordinates)
    val allShipParts = fleet.flatMap { it.parts }
    if (allShipParts.size != allShipParts.toSet().size)
        return Result.Failure(FleetError.ShipPartsWithSameCoordinates())

    // Check ships alignment
    fleet.forEach { ship ->
        if (
            !(
                // Either VERTICAL
                ship.parts.all { part -> part.position.col == ship.parts.first().position.col }
                ||
                // OR HORIZONTAL
                ship.parts.all { part -> part.position.row == ship.parts.first().position.row }
            )
        ) return Result.Failure(FleetError.BadShipAlignment())
    }

    /*
    * Make sure ships parts are in sequence
    * this is very BIG BRAIN
    * count how many times a shipPart does not have next row or col
    * and because there's always one that does not have one bigger
    * if numberOfTimesDoesNotHaveNextPart != 1 then we know
    * that ship not in sequence
    * */
    fleet.forEach { ship ->
        val numberOfTimesDoesNotHaveNextPart =
            // Count how many times a ship part does not have another part next to it
            ship.parts.count { shipPart ->
                ship.parts
                    .filterNot { it == shipPart }
                    .none {
                        shipPart.position.col.inc() == it.position.col
                        ||
                        shipPart.position.row.inc() == it.position.row
                    }
            }

        if (numberOfTimesDoesNotHaveNextPart != 1)
            return Result.Failure(FleetError.ShipNotInSequence())
    }


    // NOT WORKING
    // Check ships distance from each other
    fleet.forEach { ship ->
       ship.parts.map { it.position }.forEach { partPos ->
           fleet
               .filterNot { it === ship }
               .forEach { otherShip ->
                   if (
                       otherShip.parts.map { it.position }.any {
                           // Top + Bottom
                           (it.col == partPos.col - 1 && it.row == partPos.row) ||
                           (it.col == partPos.col + 1 && it.row == partPos.row) ||
                           // Left + Right (same level)
                           (it.col == partPos.col && it.row == partPos.row - 1) ||
                           (it.col == partPos.col && it.row == partPos.row + 1) ||
                           // Diagonals
                           // Top Left
                           (it.col - 1 == partPos.col && it.row - 1 == partPos.row) ||
                           // Bottom Left
                           (it.col + 1 == partPos.col && it.row - 1 == partPos.row) ||
                           // Top Right
                           (it.col - 1 == partPos.col && it.row + 1 == partPos.row) ||
                           // Bottom Right
                           (it.col + 1 == partPos.col && it.row + 1 == partPos.row)
                        }
                   )
                       return Result.Failure(FleetError.ShipsNotSpaced())
                }
       }
    }

    return Result.Success(Unit)
}

// FOR QUICKER TESTING PURPOSES
fun main(){
    val shipConfig: List<ShipConfiguration> = listOf(
        ShipConfiguration(2, 3)
    )
    val bd = BoardDimensions(6, 6)
    println(validateFleetLayout(
        setOf(
            Ship(
                parts = listOf(ShipPart(Position(0, 1)), ShipPart(Position(0, 2)), ShipPart(Position(0, 3)))
            ),
            Ship(
                parts = listOf(ShipPart(Position(2, 1)), ShipPart(Position(2, 2)), ShipPart(Position(2, 3)))
            )
        ),
        bd, shipConfig
    ))
}