insert into users 
	(username, password)
values
	('John Doe', 'HiPassword123#'),
	('Diogo Jesus', 'superSecretPassword1_'),
	('Jose Menezes', 'another50SecretPasswd!!'),
	('Henrique Aguas', 'Password123#')
;

insert into gamerules (
	id, shots_per_round, shot_timeout_s, layout_timeout_s,
	board_dimensions, ships_configuration
) values (
	4, 2, 30, 90, (7, 7),
	array[
		(1, 4),
		(2, 3),
		(3, 2),
		(4, 1)
	]::shipconfiguration[]
);

insert into games
(
	game_id, player1, player2, 
	p1_fleet, p2_fleet, 
	p1_missed_shots, p2_missed_shots, turn, turn_deadline, layout_phase_deadline, 
	rules
) 
values
	(
		'40e6215d-b5c6-4896-987c-f30f3678f608', 'John Doe', 'Diogo Jesus', 
		-- P1 FLEET
		array [
			(
				-- SHIP
				false,
				array[
					-- SHIPPART
					((1, 2), false), 
					((3, 4), true)
				]::shippart[]
			)
		]::ship[],
		-- P2 FLEET
		array [
			(
				-- SHIP
				false,
				array[
					-- SHIPPART
					((7, 8), true), 
					((5, 2), true)
				]::shippart[]
			)
		]::ship[],
		 array[(1,2),(3,4)]::coordinates[], array[(5,6),(7,8), (9, 10)]::coordinates[],
		0, current_timestamp + '60 seconds', current_timestamp + '90 seconds',
		4
	)
;

/*
select p1_fleet[1].parts[1].is_hit from games
where game_id = '40e6215d-b5c6-4896-987c-f30f3678f608';
*/