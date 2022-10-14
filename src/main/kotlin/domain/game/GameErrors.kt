package domain.game

abstract class GameError(
    override val message: String?,
    open val details: String?
): Throwable()


sealed class FleetError(
    override val details: String?,
    override val message: String?
): GameError(message, details) {

    data class NotLayoutPhase(
        override val details: String? = null,
        override val message: String = "Game not in Layout phase anymore",
    ): FleetError(message, details)

    data class AlreadySubmitted(
        override val details: String? = null,
        override val message: String = "You already submitted your fleet layout",
    ): FleetError(message, details)


    data class InvalidShipsConfiguration(
        override val details: String? = null,
        override val message: String = "The ships configuration provided is invalid",
    ): FleetError(message, details)

    data class ShipPartOutOfBounds(
        override val details: String? = null,
        override val message: String = "One or more ship parts are out of bounds",
    ): FleetError(message, details)

    data class ShipPartsWithSameCoordinates(
        override val details: String? = null,
        override val message: String = "There are ship parts with the same coordinates",
    ): FleetError(message, details)

    data class BadShipAlignment(
        override val details: String? = null,
        override val message: String = "One or more ships are not properly aligned (Not vertical nor horizontal)",
    ): FleetError(message, details)

    data class ShipNotInSequence(
        override val details: String? = null,
        override val message: String = "One or more ship parts from a ship are not in sequence",
    ): FleetError(message, details)

    data class ShipsNotSpaced(
        override val details: String? = null,
        override val message: String = "There are ships with less than 1 cell of distance between them",
    ): FleetError(message, details)
}

sealed class MakeShotError(
    override val details: String?,
    override val message: String?
): GameError(message, details) {
    data class NotShootingPhase(
        override val details: String? = null,
        override val message: String = "Game not in shooting phase anymore",
    ): MakeShotError(message, details)

    data class NotYourTurn(
        override val details: String? = null,
        override val message: String = "Its not your turn",
    ): MakeShotError(message, details)

    data class RepeatedShot(
        override val details: String? = null,
        override val message: String = "You already made shot that position",
    ): MakeShotError(message, details)
}