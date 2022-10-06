package server_domain

abstract class ShipPart(open val position: Position)

data class MyShipPart(override val position: Position, val isHit: Boolean = false) : ShipPart(position)

data class Ship(
    val parts: List<MyShipPart>,
    val isDestroyed: Boolean = parts.all { it.isHit }
) {
    companion object {
        fun fromCoordinates(start: Position, end: Position): Ship {
            val parts = mutableListOf<ShipPart>()
            if (start.row == end.row) {
                repeat(1) {

                }
            }
        }
    }
}

data class OpponentShipPart(override val position: Position) : ShipPart(position)

data class OpponentShip(
    val hitParts: List<OpponentShipPart>,
    val isDestroyed: Boolean = false
)
