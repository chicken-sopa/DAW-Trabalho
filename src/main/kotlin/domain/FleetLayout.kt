package domain

data class LayoutValidationSettings(
    val boardDimensions: BoardDimensions,
    val shipConfiguration: List<ShipConfiguration>
)

data class FleetLayout(
    val ships: Set<Ship> = setOf(),
    val validation: LayoutValidationSettings? = null,
) {
    init {
        if (validation != null)
            validate()
    }

    private fun validate(): Boolean{
        requireNotNull(validation)

        /**
         * TODO: Validate Ship positions:
         *  - No Ship part falls out of board
         *  - Every ship is complete
         *  - Ship all in the same alignment
         **/


        ships.forEach{ ship ->
            ship.parts.forEach{shipPart->
                if( shipPart.coordinates.col !in 0 until validation.boardDimensions.cols_num )
                    throw Exception("error on validate ships need to specify" )

                if( shipPart.coordinates.row !in 0 until validation.boardDimensions.rows_num)
                    throw Exception("error on validate ships need to specify" )
            }
        }

        validation.shipConfiguration.forEach{shipConfiguration ->
            if(shipConfiguration.quantity != ships.count { ship -> ship.parts.size == shipConfiguration.ship_size })
                throw Exception("error on validate ships need to specify" )
        }

        ships.forEach{ship->
            ship.parts.filter { shipPart ->
                shipPart.coordinates.col != ship.parts.first().coordinates.col &&
                        shipPart.coordinates.col == ship.parts.first().coordinates.col
            }

            ships.forEach {ship ->

                if(ship.parts.any { it.coordinates.col != ship.parts.first().coordinates.col } ||
                    ship.parts.any { it.coordinates.row != ship.parts.first().coordinates.row })
                {
                 throw  Exception("error on validate ships need to specify")
                }

            }
        }




        return true
    }
}


fun main() {
    data class shipPart(val row: Int, val col: Int)
    val ship = listOf(shipPart(1,2), shipPart(1,3), shipPart(1,2))

    if(ship.any { it.col != ship.first().col } &&
        ship.any { it.row != ship.first().row }){
        println("hello")
    }
}