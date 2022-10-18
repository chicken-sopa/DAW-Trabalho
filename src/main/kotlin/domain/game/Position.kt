package domain.game

data class Position(val row: Int, val col: Int) {
    init {
        require(row > 0 && col > 0) { "Position can´t contain negative coordinates" }
    }
}
