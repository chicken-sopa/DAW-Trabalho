package utils

import domain.game.BoardDimensions
import domain.game.GameMode
import domain.game.ShipConfiguration

// Note: Needs to be in sync with DB. Fix Later
val testGameMode = GameMode(
    mode_name = "Test Mode",
    board_dimensions = BoardDimensions(rows_num=7, cols_num=7),
    ships_configurations = listOf(
        ShipConfiguration(1, 3),
        ShipConfiguration(1, 2)
    ),
    shots_per_round = 2,
    layout_timeout_s = 90,
    shots_timeout_s = 40
)