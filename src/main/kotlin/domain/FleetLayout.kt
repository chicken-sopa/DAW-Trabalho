package domain

import ActionResult

typealias FleetLayoutValidation = ActionResult<FleetLayoutError, Unit>

fun validateFleetLayout(
    fleet: Set<Ship>,
    boardDimensions: BoardDimensions,
    allowedShipConfigurations: List<ShipConfiguration>
): FleetLayoutValidation {
    // Make sure no ship part is out of board
    fleet.forEach { ship ->
        ship.parts.forEach{shipPart->
            if (shipPart.position.col !in 0 until boardDimensions.cols_num)
                return ActionResult.Failure(FleetLayoutError.INVALID)

            if (shipPart.position.row !in 0 until boardDimensions.rows_num)
                return ActionResult.Failure(FleetLayoutError.INVALID)
        }
    }

    // Make sure all ships match the allowedShipConfigurations
    allowedShipConfigurations.forEach { shipConfig ->
        if (shipConfig.quantity != fleet.count { ship -> ship.parts.size == shipConfig.ship_size })
            throw Exception("error on validate ships need to specify")
    }


    fleet.forEach{ship->

        // check if all the ships are aligned
        if(ship.parts.any { part -> part.position.col != ship.parts.first().position.col } &&
        ship.parts.any { part -> part.position.row != ship.parts.first().position.row }) {
            throw Exception("SHIP Not Aligned")
        }

        /*this is very BIG BRAIN
        * count how many times a shipPart does not have next row or col
        * and because there's always one that does not have one bigger
        * if numberOfTimesDoesNotHaveNextPart != 1 then we know
        * that ship not in sequence
        * */

        val numberOfTimesDoesNotHaveNextPart = ship.parts.count { shipPart ->
            ship.parts.none {part -> part.position.col == shipPart.position.col.inc() ||
                    part.position.row == shipPart.position.row.inc() }
        }
        if(numberOfTimesDoesNotHaveNextPart != 1){
            throw Exception("SHIP not in sequence")
        }

    }

    // check if shipPart not collide
    fleet.forEach{ship ->
        ship.parts.forEach {shipPart ->
            fleet.forEach { secondShip->
                if(secondShip.parts.any {secondShipPart -> secondShipPart === shipPart }){
                    throw  Exception("Ship part collide")
                }
            }
        }

    }

    fleet.forEach {ship ->
           ship.parts.forEach { shipPart ->
               fleet.filterNot { it === ship }.forEach {secondShip ->
                   secondShip.parts.any { secondShipPart ->
                       secondShipPart.position.col == shipPart.position.col.inc() ||
                               secondShipPart.position.col == shipPart.position.col.dec() ||
                               secondShipPart.position.col == shipPart.position.row.inc() ||
                               secondShipPart.position.col == shipPart.position.row.dec()

                   }
               }
           }

    }

    // TODO
    // ship parts do not collide
    // ship parts are in sequence
    // <= 1 square spacing among ships
    return ActionResult.Success(Unit)
}

fun main(){
    val ship = Ship(listOf(ShipPart(Position(1,2)), ShipPart(Position(2,2)),ShipPart(Position(3,2))               ))
    /*if(ship.parts.any { part -> part.position.col != ship.parts.first().position.col } &&

        ship.parts.any { part -> part.position.row != ship.parts.first().position.row }){

            println("WE good we are")
    }*/
    val count = ship.parts.count { shipPart ->
        ship.parts.none {part -> part.position.col == shipPart.position.col.inc() ||
                part.position.row == shipPart.position.row.inc() }
    }

    println(count)

}