[![wakatime](https://wakatime.com/badge/user/986136b0-1846-407d-98bf-6419adad41cb/project/a0c08b0b-e2ad-4aed-8c9c-bf73a4d8efbf.svg)](https://wakatime.com/badge/user/986136b0-1846-407d-98bf-6419adad41cb/project/a0c08b0b-e2ad-4aed-8c9c-bf73a4d8efbf)

# 3v3 Ranked Bot

This bot facilitates 3vs3 ranked matches by creating a designated channel where up to six users can join or leave the queue. Users can join or leave the queue either by clicking a button or typing `=join` / `=leave`.

## Features

### Basic Elo System

The ranking system consists of six tiers: Bronze, Silver, Gold, Platinum, Diamond, and Masters:
- **Bronze:** 1500 Elo
- **Silver:** 1750 Elo
- **Gold:** 2000 Elo
- **Platinum:** 2250 Elo
- **Diamond:** 2500 Elo
- **Masters:** 2750 Elo

Points awarded or deducted per match are fixed based on the rank:

```text
Bronze:
+26 -10

Silver:
+22 -14

Gold:
+18 -18

(by 4s)
```

### Queue and Teams

Once the queue reaches six users, the bot will automatically create a text channel and two voice channels for each respective team (optional but recommended). 

Teams are balanced based on Elo points, with the highest two Elo players becoming the team captains. 

### Map Banning

Captains ban maps in turns starting with Captain
1. The map pool includes: Station-1, Station-2, Temple-M, Colosseum, Side-3, Ice Square, and Wonderland.
2. The banning process continues until only one map remains, at which point the bot will announce the selected map.

### Reporting Game Results

Users can report the winning team using a command like `=game <winning_team_number>`. The bot will then automatically update the points for each player based on the match outcome.

### Leaderboard

A command to display the leaderboard showing players' ranks, Elo, wins, losses, and win rate percentage.

### Moderator Commands

Basic moderator commands include:
- **Manual Points Modifier:** Adjust player points manually.
- **Ban:** Ban users from participating in matches.
- **Clear Queue:** Clear the current matchmaking queue.

## Commands

### User Commands

- `=join`: Join the match queue.
- `=leave`: Leave the match queue.
- `=game <winning_team_number>`: Report the winning team.
- `=leaderboard`: Display the leaderboard.

### Moderator Commands

- `=mod points <user> <points>`: Modify the points of a user.
- `=mod ban <user>`: Ban a user from the queue.

## Images
### Matchmaking
![image](https://github.com/user-attachments/assets/caab36ec-7e30-484c-9946-bb16732eacfd)
### User Profile
![image](https://github.com/user-attachments/assets/b0bb09de-b4f1-4b2c-81d9-66bf0ab4933c)
### Leaderboard
![image](https://github.com/user-attachments/assets/f8031e94-f407-499a-af65-979c3c662237)


## Last updated
### March 26, 2023
