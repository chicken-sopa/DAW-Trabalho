package server_domain

data class LayoutValidationSettings(val boardDimensions: BoardDimensions, val shipConfiguration: List<ShipConfiguration>)

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



        return true
    }
}
