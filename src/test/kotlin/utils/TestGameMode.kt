package utils

import domain.BoardDimensions
import domain.GameMode
import domain.ShipConfiguration

val testGameMode = GameMode(
    name = "Test Mode",
    board_dimensions = BoardDimensions(rows_num=8, cols_num=8),
    ships_configurations = listOf(
        ShipConfiguration(1, 3),
        ShipConfiguration(1, 2)
    ),
    shots_per_round = 2,
    layout_timeout_s = 90,
    shots_timeout_s = 40
)