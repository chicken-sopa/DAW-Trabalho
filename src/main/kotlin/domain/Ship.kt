package domain

data class ShipPart(val position: Position, val isHit: Boolean = false)

enum class ShipAlignment { HORIZONTAL, VERTICAL }

data class Ship(
    val parts: List<ShipPart>,
    val isDestroyed: Boolean = parts.all { it.isHit }
) {
    companion object {
        fun fromCoordinates(start: Position, shipAlignment: ShipAlignment, shipSize: Int): Ship {
            val parts = mutableListOf<ShipPart>()
            if (shipAlignment == ShipAlignment.HORIZONTAL) {
                repeat(shipSize) { colIdx ->
                    val newPart = ShipPart(Position(start.row, start.col + colIdx))
                    parts.add(newPart)
                }
            } else {
                repeat(shipSize) { rowIdx ->
                    val newPart = ShipPart(Position(start.row + rowIdx, start.col))
                    parts.add(newPart)
                }
            }
            return Ship(parts)
        }
    }
}

data class PartialShip(
    val hitParts: List<ShipPart>,
    val isDestroyed: Boolean = false
)
