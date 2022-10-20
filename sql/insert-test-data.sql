insert into users values
('TESTUSER4', 'SDKJLSDFLKJSDFKJSFDJKLSDFKLS', 200, 120, 2000);

insert into users values
('TESTUSER1', 'SDKJLSDFLKJSDFKJSFDJKLSDFKLS', 50, 20, 500);

insert into users values
('TESTUSER5', 'SDKJLSDFLKJSDFKJSFDJKLSDFKLS', 250, 200, 2700);

insert into users values
('TESTUSER2', 'SDKJLSDFLKJSDFKJSFDJKLSDFKLS', 100, 40, 1000);

insert into users values
('TESTUSER3', 'SDKJLSDFLKJSDFKJSFDJKLSDFKLS', 150, 100, 1300);


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
),
(
	'Test Mode',
	'{"rows_num": 7, "cols_num": 7}',
	'[
	    {
	      "quantity": 1,
	      "ship_size": 3
	    },
		{
	      "quantity": 1,
	      "ship_size": 2
	    }
  	 ]',
  	 2, 90, 40
)
;
