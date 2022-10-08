package domain

fun validateFleetLayout(
    fleet: Set<Ship>,
    boardDimensions: BoardDimensions,
    allowedShipConfigurations: List<ShipConfiguration>
) {
    // Make sure no ship part is out of board
    fleet.forEach { ship ->
        ship.parts.forEach{shipPart->
            if (shipPart.position.col !in 0 until boardDimensions.cols_num)
                throw Exception("error on validate ships need to specify")

            if (shipPart.position.row !in 0 until boardDimensions.rows_num)
                throw Exception("error on validate ships need to specify")
        }
    }

    // Make sure all ships match the allowedShipConfigurations
    allowedShipConfigurations.forEach { shipConfig ->
        if (shipConfig.quantity != fleet.count { ship -> ship.parts.size == shipConfig.ship_size })
            throw Exception("error on validate ships need to specify")
    }

    // TODO
    // ship parts do not collide
    // ship parts are in sequence
    // <= 1 square spacing among ships
}
