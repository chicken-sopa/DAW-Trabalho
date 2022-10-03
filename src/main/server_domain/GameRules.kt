package com.example.first_app.server_domain

object GameRules {

    data class BoardDimensions(val rows_num: Int, val cols_num: Int)
    data class ShipConfiguration(val quantity: Int, val ship_size: Int)

    val board_dimensions = BoardDimensions(rows_num=1, cols_num=2)
    val ships = listOf(
        ShipConfiguration(1, 4),
        ShipConfiguration(2, 3),
        ShipConfiguration(3, 2),
        ShipConfiguration(4, 1)
    )

    val shots_per_round = 4
    val layout_timeout_s = 90
    val shot_timeout_s = 40
}