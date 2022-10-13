package domain.game

import Result

typealias FleetLayoutValidation = Result<FleetError, Unit>

/**
 * Just an idea
 * TODO: Make every ERROR sealed class have a "detail" field + inherit Throwable (maybe)
 * */
fun validateFleetLayout(
    fleet: Set<Ship>,
    boardDimensions: BoardDimensions,
    allowedShipConfigurations: List<ShipConfiguration>
): FleetLayoutValidation {
    // Make sure all ships match the allowedShipConfigurations
    allowedShipConfigurations.forEach { shipConfig ->
        if (shipConfig.quantity != fleet.count { ship -> ship.parts.size == shipConfig.ship_size })
            throw Exception("Ships not according to ships configuration")
    }

    // Make sure no ship part is out of board
    fleet.forEach { ship ->
        ship.parts.forEach{shipPart->
            if (shipPart.position.col !in 0 until boardDimensions.cols_num)
                throw Exception("Ship part out of bounds")

            if (shipPart.position.row !in 0 until boardDimensions.rows_num)
                throw Exception("Ship part out of bounds")
        }
    }

    fleet.forEach { currShip ->
        // Make sure ships don't have repeated parts (same coordinates inside EACH ship)
        if (currShip.parts.toSet().size != currShip.parts.size)
            throw Exception("Ship parts of a ship are repeated")

        // Make sure ships don't have repeated parts (same coordinates among ALL)
        val otherShipsParts = fleet.filterNot { it === currShip }.flatMap { it.parts }
        currShip.parts.forEach { shipPart ->
            if (otherShipsParts.find { it == shipPart } != null)
                throw Exception("One or more ships have parts with the same coordinates")
        }
    }

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
        ) throw Exception("One or more ships are not properly aligned (Not vertical neither horizontal)")
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
            throw Exception("Ship parts are not in sequence")
    }


    // Check ships distance from each other
    fleet.forEach { ship ->
       ship.parts.forEach { shipPart ->
           fleet
               .filterNot { it === ship }
               .forEach { secondShip ->
                   if (
                       secondShip.parts.any { secondShipPart ->
                           secondShipPart.position.col == shipPart.position.col.inc() ||
                           secondShipPart.position.col == shipPart.position.col.dec() ||
                           secondShipPart.position.row == shipPart.position.row.inc() ||
                           secondShipPart.position.row == shipPart.position.row.dec()
                        }
                   )
                       throw Exception("Not all ships have at least 1 cell of spacing among them")
                }
       }

    }

    return Result.Success(Unit)
}

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