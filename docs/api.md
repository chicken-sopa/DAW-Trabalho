# API Documentation

### Notes:
- All the responses with error status (4xx and 5xx) come with the following format:

`Content-Type: application/problem+json`
#### Response

```json
{
  "type": "error-type",
  "title": "Error Title",
  "details": "More information about the error cause"
}
```


<br/>

## System Information 
### Information about the system

<br/>

> **GET /api/sysinfo** (Obtain information about the application)

#### Responses:

`Status Code: 200`
```json
{
  "authors": [
      {
        "name": "John",
        "github_profile": "https://github.com/John"
      }
  ],
  "system_version": "2.0.1"
}
```


## Statistical Information
### Provide global statistical information

<br/>

> **GET /api/stats/ranking** (Get the global ranking)

#### Responses

`Status Code: 200`

```json
{
  "ranking": [
    {
      "username": "John",
      "ranking_position": 1,
      "ranking_points": 19000,
      "games_played": 29,
      "win_rate": 10
    },
    {
      "username": "Rick",
      "ranking_position": 2,
      "ranking_points": 14000,
      "games_played": 290,
      "win_rate": 90
    },
    {
      "username": "Mark",
      "ranking_position": 3,
      "ranking_points": 1200,
      "games_played": 500,
      "win_rate": 40
    }
  ]
}
```

<br/>

## Users
### User operations (creating, login, logout, statistics, etc.)

<br/>

> **GET /api/users/{username}/stats** (User game-related statistics)

#### Responses

`Status Code: 200`
```json
{
  "username": "John",
  "ranking_position": 1,
  "ranking_points": 19348,
  "games_played": 29,
  "win_rate": 10
}
```

<br/><br/>

> **POST /api/users** (Create a user)


#### Request Format

```json
{
  "username": "Manuel",
  "password": "Manuel1234"
}
```

#### Responses

`Status Code: 422` If input is invalid (Ex: Password should contain at least one capital letter)

`Status Code: 409` User with that name already exists

`Status Code: 201`
```json
{
  "token": "JSKAHJDLKAS-JHD3892-KHADSKJHFIDUSF"
}
```

<br/><br/>

> **PUT /api/users** (Login)

#### Request Format

```json
{
  "username": "Manuel",
  // Maybe later the password will already come hashed (to protect it in case the communication is not encrypted) 
  "password": "Manuel123"
}
```

#### Responses

`Status Code: 404`

`Status Code: 403` If invalid credentials

`Status Code: 200`
```json
{
  "token": "JSKAHJDLKAS-JHD3892-KHADSKJHFIDUSF"
}
```

## Games
### Matchmaking, submitting fleet layout, submiting shots and getting the updated game state

<br/>

>  **GET /api/games/matchmaking** (Join matchmaking) (Authentication Required)

#### Request Format
```json
{
  "mode": "Normal"
}
```

#### Responses

`Status Code: 200` Found a match (no need to go to queue)
```json
{
  "href": "/api/games/ASKJDHLAJSD-AJFGSDH-ASDHJKA"
}
```

`Status Code: 202` Match not found (goes to queue)
```json
{
  // Inform the user how he can keep up with it's matchmaking status
  "href": "/api/games/matchmaking/status"
}
```

<br/>

> **GET /api/games/matchmaking/status** (Check matchmaking status) (Authentication Required)

#### Responses

`Status Code: 404` User not in matchmaking queue

`Status Code: 200`
```json
{
  "status": "pending",
  "mode": "Normal"
}
```

`Status Code: 200`

`Content-Location (Header): /api/games/ASKJDHLAJSD-AJFGSDH-ASDHJKA`
```json
{
  "status": "completed",
  "mode": "Normal"
}
```

<br/>

> **PUT /api/games/matchmaking/leave** (Leave Matchmaking) (Authentication Required)

// TODO
 
<br/>

> **PUT /api/games/{game_id}/layout** (Authentication Required)

#### Request Format

```json
{
  "ships": [ 
    [
      // Ship Parts
      { "row": 1, "col": 4 },
      { "row": 2, "col": 4 }
    ]
    // More ships ...
  ]
}
```

<br/>

> **PUT /api/games/{game_id}/shot** (Authentication Required)

#### Request Format

```json
{
  "shot": {
      "row": 2,
      "col": 6
    }
}
```

#### Responses

`Status Code: 403` If not participating in the Game

`Status Code: 200 true` Shot hit

`Status Code: 200 false` Shot did not hit

<br/>

> **GET /api/games/{game_id}/my-fleet** (Authentication Required)

#### Responses

`Status Code: 403` If not participating in the Game

`Status Code: 200`
```json
{
    "my_fleet": [ // Ships
      {
        "parts": [ // Ship Parts
            { "row": 1, "col": 4, "isHit": false },
            { "row": 1, "col": 5, "isHit": true }
        ],
        "is_destroyed": false
      },
      {
        "parts": [ // Ship Parts
          { "row": 1, "col": 5, "isHit": false },
          { "row": 2, "col": 5, "isHit": true }
        ],
        "is_destroyed": false
      }
    // ... more ships
    ]
}
```

<br/>

> **GET /api/games/{game_id}/opponent-fleet** (Authentication Required)

#### Responses
`Status Code: 403`  If not participating in the Game

`Status Code: 200`
```json
{
  "opponent_fleet": [ // Ships
    {
      "parts": [ // Ship Parts
        { "row": 1, "col": 4, "isHit": true },
        { "row": 1, "col": 5, "isHit": true }
      ],
      "is_destroyed": false
    },
    {
      "parts": [ // Ship Parts
        { "row": 1, "col": 5, "isHit": true },
        { "row": 2, "col": 5, "isHit": true }
      ],
      "is_destroyed": true
    }
    // ... more ships
  ]
}
```

<br/>

> **GET /api/games/{game_id}** (Authentication Required)

#### Request

`If-None-Match (OPTIONAL Header): <etag>`

#### Responses

`Status Code: 403` If not participating in the game

`Status Code: 304` If game's etag has not changed

`Status Code: 200`

```json
{
  "game_id": "KLDSFS-SDJFKHSKDJF-SDFJKSDHJ",
  "p1": "John",
  "p2": "Mark",
  "game_mode": {
    "mode_name": "Normal",
    "board_dimensions": {
      "rows_num": 7,
      "cols_num": 7
    },
    "ships_configuration": [
      {
        "quantity": 1,
        "ship_size": 4
      },
      {
        "quantity": 3,
        "ship_size": 3
      },
      {
        "quantity": 2,
        "ship_size": 1
      }
    ],
    "shots_per_round": 4,
    "layout_timeout_s": 90,
    "shots_timeout_s": 40
  },
  "opponent_fleet": [
    // Only the ship parts which have been hit will be in this list
    {
      "parts": [
        // Ship Parts
        {
          "row": 1,
          "col": 4,
          "isHit": true
        },
        {
          "row": 1,
          "col": 5,
          "isHit": true
        }
      ],
      "is_destroyed": false
    }
  ],
  "my_missed_shots": [
    {
      "row": 7,
      "col": 4
    }
  ],
  "my_fleet": [
    // Ships
    {
      "parts": [
        // Ship Parts
        {
          "row": 1,
          "col": 4,
          "isHit": true
        },
        {
          "row": 2,
          "col": 4,
          "isHit": true
        }
      ],
      "is_destroyed": true
    }
    // ... more ships
  ],
  "opponent_missed_shots": [
    {
      "row": 1,
      "col": 4
    },
    {
      "row": 2,
      "col": 6
    }
  ],
  // null if game_phase != SHOOTING
  "turn_deadline": "23/04/2009-19:39:46",
  // null if game_phase != LAYOUT
  "layout_deadline": "23/04/2009-19:39:46",
  // LAYOUT | SHOOTING | COMPLETED
  "phase": "COMPLETED",
  // null | Player1 | Player2 
  "winner": null
}
```

