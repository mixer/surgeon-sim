# Beam plays Surgeon Simulator

## Download

Download [Beam plays Surgeon Simulator v1.1](https://github.com/WatchBeam/surgeon-sim/releases/tag/1.1-SNAPSHOT).

For all versions, check out the [releases page](https://github.com/WatchBeam/surgeon-sim/releases).

## Building
mvn package

## Instructions

### Step 1
Set your game to Beam Performs Surgery 0.0.2 (Game id 38)

### Step 2
Put credentials in credentials.txt
First line the username
second line the password
(2FA off)

### Step 3
Get your stream ready.

### Step 4
Open a cmd in the directory of the char file and run the following command:

`java -jar BPSurgeonSimulator-1.0.0-SNAPSHOT.jar -c<channelID> -k`

Make sure to not put a space between -c and your channelid (example: -c143 )

#### CMD args:
```
-k: Enable keyboard input (MANDATORY)
-c<chanID>: ChannelID (MANDATORY)

OPTIONAL:
-m: Enable movement input (NOT WORKING)
—lab: Run against lab
—debug: Show more debug data
--noshake: disables the mouse shake option
-s<sens>: Changes the maximal box in the center of the screen that allows mouse input (percentage, default 0.22 or 22%) (Double, between 0 and 1)
```

### Step 5
Start a surgeon simulator game and press F7 to enable/disable input from Beam
(By default disabled)
