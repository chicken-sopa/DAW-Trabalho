package domain

data class BoardDimensions(val rows_num: Int, val cols_num: Int)
data class ShipConfiguration(val quantity: Int, val ship_size: Int)

data class GameMode (
    val mode_name: String,
    val board_dimensions: BoardDimensions,
    val ships_configurations: List<ShipConfiguration>,
    val shots_per_round: Int,
    val layout_timeout_s: Int,
    val shots_timeout_s: Int
)