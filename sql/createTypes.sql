-- Create Types

-- create type GamePhase as ENUM('LAYOUT', 'ONGOING', 'COMPLETED');
-- Not being used since elements in a enum class in Kotlin are just numbers
-- and not strings.
-- create type Player as ENUM('P1', 'P2');
create type Coordinates as (row int, col int);
create type ShipPart as (coordinates Coordinates, is_hit boolean);
create type BoardDimensions as (rows_num int, cols_num int);
create type ShipConfiguration as (quantity int, ship_size int);
create type Ship as (is_destroyed boolean, parts ShipPart[]);
