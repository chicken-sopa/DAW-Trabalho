package domain

data class ShipPart(val coordinates: Coordinates, val isHit: Boolean = false)

enum class ShipAlignment { HORIZONTAL, VERTICAL }

data class Ship(
    val parts: List<ShipPart>,
    val isDestroyed: Boolean = parts.all { it.isHit }
) {
    companion object {
        fun fromCoordinates(start: Coordinates, shipAlignment: ShipAlignment, shipSize: Int): Ship {
            val parts = mutableListOf<ShipPart>()
            if (shipAlignment == ShipAlignment.HORIZONTAL) {
                repeat(shipSize) { colIdx ->
                    val newPart = ShipPart(Coordinates(start.row, start.col + colIdx))
                    parts.add(newPart)
                }
            } else {
                repeat(shipSize) { rowIdx ->
                    val newPart = ShipPart(Coordinates(start.row + rowIdx, start.col))
                    parts.add(newPart)
                }
            }
            return Ship(parts)
        }
    }
}

data class OpponentShipPart(val coordinates: Coordinates)

data class OpponentShip(
    val hitParts: List<OpponentShipPart>,
    val isDestroyed: Boolean = false
)
