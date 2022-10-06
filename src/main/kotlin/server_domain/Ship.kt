package server_domain

abstract class ShipPart(open val coordinates: Coordinates)

data class MyShipPart(override val coordinates: Coordinates, val isHit: Boolean = false) : ShipPart(coordinates)

enum class ShipAlignment { HORIZONTAL, VERTICAL }

data class Ship(
    val parts: List<MyShipPart>,
    val isDestroyed: Boolean = parts.all { it.isHit }
) {
    companion object {
        fun fromCoordinates(start: Coordinates, shipAlignment: ShipAlignment, shipSize: Int): Ship {
            val parts = mutableListOf<MyShipPart>()
            if (shipAlignment == ShipAlignment.HORIZONTAL) {
                repeat(shipSize) { colIdx ->
                    val newPart = MyShipPart(Coordinates(start.row, start.col + colIdx))
                    parts.add(newPart)
                }
            } else {
                repeat(shipSize) { rowIdx ->
                    val newPart = MyShipPart(Coordinates(start.row + rowIdx, start.col))
                    parts.add(newPart)
                }
            }
            return Ship(parts)
        }
    }
}

data class OpponentShipPart(override val coordinates: Coordinates) : ShipPart(coordinates)

data class OpponentShip(
    val hitParts: List<OpponentShipPart>,
    val isDestroyed: Boolean = false
)
