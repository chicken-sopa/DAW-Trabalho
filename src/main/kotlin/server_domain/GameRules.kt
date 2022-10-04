package server_domain

data class BoardDimensions(val rows_num: Int, val cols_num: Int)
data class ShipConfiguration(val quantity: Int, val ship_size: Int)

data class GameRules(

    val board_dimensions: BoardDimensions = BoardDimensions(rows_num=1, cols_num=2),
    val ships_configurations: List<ShipConfiguration> = listOf(
        ShipConfiguration(1, 4),
        ShipConfiguration(2, 3),
        ShipConfiguration(3, 2),
        ShipConfiguration(4, 1)
    ),
    val shots_per_round: Int = 4,
    val layout_timeout_s: Int = 90,
    val shots_timeout_s: Int = 40
)