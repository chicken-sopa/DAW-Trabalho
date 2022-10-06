package domain

data class LayoutValidationSettings(val boardDimensions: BoardDimensions, val shipConfiguration: List<ShipConfiguration>)

data class FleetLayout(
    val ships: Set<Ship> = setOf(),
    val validation: LayoutValidationSettings? = null,
) {
    init {
        if (validation != null)
            validate()
    }

    private fun validate() {
        /**
         * TODO: Validate Ship positions:
         *  - No Ship part falls out of board
         *  - Every ship is complete
         **/
    }
}
