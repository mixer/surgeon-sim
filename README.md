# Beam plays Surgeon Simulator

## Binaries

> https://www.dropbox.com/s/pyp1gy4ew4gpu1d/SurgeonSim.zip?dl=1

## Instructions

### Step 1
Set your game to Beam Performs Surgery 0.0.2

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
-s<sens>: Change sensitivity of mouse input (Between 0 and 1)
```

### Step 5
Start a surgeon simulator game and press F7 to enable/disable input from Beam
(By default disabled)
