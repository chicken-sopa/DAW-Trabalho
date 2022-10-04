package server_domain

abstract class ShipPart(open val row: Int, open val col: Int)
data class MyShipPart(override val row: Int, override val col: Int, val isHit: Boolean = false) : ShipPart(row, col)
data class Ship(
    val parts: List<MyShipPart>,
    val isDestroyed: Boolean = parts.all { it.isHit }
)
data class OpponentShipPart(override val row: Int, override val col: Int) : ShipPart(row, col)
data class OpponentShip(
    val hitParts: List<OpponentShipPart>,
    val isDestroyed: Boolean = false
)
