insert into users values
('TEST_USER_1', 'mypasswordhash');

insert into users values
('TEST_USER_2', 'mypasswordhash');

insert into gamemodes values
(
	'normal',
	'{"rows_num": 7, "cols_num": 7}',
	'[
	    {
	      "quantity": 2,
	      "ship_size": 3
	    },
	    {
	      "quantity": 3,
	      "ship_size": 2
	    },
	    {
	      "quantity": 4,
	      "ship_size": 1
	    }
  	 ]',
  	 4, 90, 40
),
(
	'One Shot + One cell Ships',
	'{"rows_num": 7, "cols_num": 7}',
	'[
	    {
	      "quantity": 6,
	      "ship_size": 1
	    }
  	 ]',
  	 1, 80, 15
)
;
