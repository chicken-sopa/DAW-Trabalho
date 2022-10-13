package domain

import Result

typealias FleetLayoutValidation = Result<FleetLayoutError, Unit>

fun validateFleetLayout(
    fleet: Set<Ship>,
    boardDimensions: BoardDimensions,
    allowedShipConfigurations: List<ShipConfiguration>
): FleetLayoutValidation {
    // Make sure no ship part is out of board
    fleet.forEach { ship ->
        ship.parts.forEach{shipPart->
            if (shipPart.position.col !in 0 until boardDimensions.cols_num)
                return Result.Failure(FleetLayoutError.INVALID)

            if (shipPart.position.row !in 0 until boardDimensions.rows_num)
                return Result.Failure(FleetLayoutError.INVALID)
        }
    }

    // Make sure all ships match the allowedShipConfigurations
    allowedShipConfigurations.forEach { shipConfig ->
        if (shipConfig.quantity != fleet.count { ship -> ship.parts.size == shipConfig.ship_size })
            return Result.Failure(FleetLayoutError.INVALID)
    }

    // TODO
    // ship parts do not collide
    // ship parts are in sequence
    // <= 1 square spacing among ships (each cell protected from every ship)
    return Result.Success(Unit)
}
