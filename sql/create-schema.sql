/*
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
*/

-- Create Tables
create table if not exists users (
	username varchar(15) primary key,

	password_hash varchar not null,

    games_played int not null default 0,
    games_won int not null default 0,

	ranking_points int not null default 0
);

create table if not exists tokens (
	token_value uuid not null,
	username varchar(15) not null,

	constraint fk_username foreign key (username) references users(username) on delete cascade,

	primary key (token_value, username)
);

create table if not exists gamemodes(
	mode_name varchar not null primary key,

	board_dimensions varchar not null,
    ships_configuration varchar not null,

    shots_per_round int not null,
    layout_timeout_s int not null,
    shots_timeout_s int not null
);

create table if not exists games(
	game_id uuid primary key,

	mode varchar not null,
	
	p1 varchar not null,
	p2 varchar not null,

	p1_fleet varchar not null,
	p2_fleet varchar not null,

	p1_missed_shots varchar not null,
	p2_missed_shots varchar not null,

	-- 0: PLAYER1 | 1: PLAYER1
	turn_shots_counter int not null,
	turn varchar not null,
	turn_deadline timestamp,
	layout_phase_deadline timestamp,
	
	constraint fk_gamemode foreign key(mode) references gamemodes(mode_name) on delete cascade,
	constraint fk_player1 foreign key(p1) references users(username) on delete cascade,
	constraint fk_player2 foreign key(p2) references users(username) on delete cascade
);

-- VIEWS
create or replace view ranking as
(
	select ranking_points from users
	order by ranking_points desc
);