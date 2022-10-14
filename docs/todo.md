### TODO


- Tests for validateFleetLayout
- Matchmaking Logic:
  - DB Schema
  - Repository
  - Services (with Games?)
- Implementation
- API Interfaces + Implementation
- Logging
- Testing Domain
- Testing Repository


### Matchmaking Idea
1. User makes request
2. Server tries to find a suitable game (from DB) (ranking points close enough)
3. If Found:
   1. Create Game (dont delete the matchmaking entry yet (so that the other player can consult it))
   2. Give game to user with 200
4. If Not Found:
   1. Create Matchmaking entry in DB where username2 = null
   2. Respond with 202 Accepted
5. User can keep consulting the matchmaking state (Ex: GET /matchmaking/status)
    - Response (200 in "pending" as well as in "completed"): 
   ```json
    {
      "status": "completed",
      "href": "/api/games/asfjkhsds-sdfjklhsdjif-fjksdhkjf"
    }
   ```