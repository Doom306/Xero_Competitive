[![wakatime](https://wakatime.com/badge/user/986136b0-1846-407d-98bf-6419adad41cb/project/a0c08b0b-e2ad-4aed-8c9c-bf73a4d8efbf.svg)](https://wakatime.com/badge/user/986136b0-1846-407d-98bf-6419adad41cb/project/a0c08b0b-e2ad-4aed-8c9c-bf73a4d8efbf)

Bot should create a 3vs3 ranked channel where up to 6 users can join at a time or leave the queue (can be a button or typing =join/=leave)

Basic elo system: bronze (1500), silver (1750), gold (2000), plat (2250), dia (2500) and masters (2750). Fixed points for each role, e.g. you gain 26 points if you win when bronze but only gain 6 points at master. Same for losses.

> Bronze:
> +26 -10
> 
> Silver:
> +22 -14
> 
> Gold:
> +18 -18
> 
> (by 4s)

Once the queue is full, the bot should create a channel and 2 voice channels for each respective team (the voice is not necessary but would be a cool addition)

Teams are made based on elo points for fairness. The captains of team 1 and team 2 are the 2 highest ELOs.

Maps should be banned by the 2 captains starting with captain 1. The maps are: Station-1, Station-2, Temple-M, Colosseum, Side-3, Ice Square and Wonderland. (Ban until one map is left) Once the final map is reached there should be an output mentioning the name of the map.

Users should be able to write a command to say which team won e.g. =game 2, and the bot should automatically award the points. 

There should be a leaderboard command.

There should be some basic moderator commands such as manual points modifier, a ban and clear queue.

Something like how many games they’ve won in total and lost and a winrate %

**Total: 370$**
