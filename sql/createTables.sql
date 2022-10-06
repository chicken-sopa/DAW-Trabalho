-- Create Tables

create table if not exists users (
	username varchar(15) primary key
	check (
		length(username) >= 3
	),
	
	password varchar(30) not null
	check (
		length(password) > 10 and
		password ~ '^.*(?=.{6,})(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!_#\."]).*$'
	),
	
	ranking_points int not null default 0
);

create table if not exists tokens (
	token_value uuid not null,
	username varchar(15) not null,
	
	constraint fk_username foreign key (username) references users(username) on delete cascade,
		
	primary key (token_value, username)
);


create table if not exists gamerules(
    id serial primary key,
    
    shots_per_round int not null,
    shot_timeout_s int not null,
    layout_timeout_s int not null,
    
    board_dimensions BoardDimensions not null,
    
    ships ShipConfiguration[] not null
);

create table if not exists gamestates(
	game_id uuid primary key,
	
	player1 varchar(15) not null,
	player2 varchar(15) not null,
	
	p1_fleet Ship [] not null default '{}',
	p2_fleet Ship [] not null default '{}',
	
	p1_missed_shots Coordinates[] not null default '{}',
	p2_missed_shots Coordinates[] not null default '{}',
	
	-- 0: PLAYER1 | 1: PLAYER1
	turn int not null check(turn in (0, 1))
		default 0,
	turn_deadline timestamp default null,
	layout_phase_deadline timestamp,
	
	-- 0: LAYOUT | 1: SHOOTING | 2: COMPLETED
	phase int not null check(phase in (0, 1, 2)) default 0,
	/*
	phase GamePhase not null
		default 'LAYOUT',
	*/
	-- winner int check(winner in (null, 0, 1)) default null,

	rules int not null,
	
	constraint fk_rules foreign key(rules) references gamerules(id) on delete cascade,	
	constraint fk_player1 foreign key(player1) references users(username) on delete cascade,
	constraint fk_player2 foreign key(player2) references users(username) on delete cascade
);

-- VIEWS
create or replace view ranking as
(
	select ranking_points from users
	order by ranking_points desc
);