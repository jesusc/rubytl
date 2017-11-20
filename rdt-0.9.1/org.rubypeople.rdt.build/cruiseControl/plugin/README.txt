This is an Eclipse project for a cruise control label provider, it produces labels
with the format 

<prefix>.<date>NGT

where date has the format ymmddhhMM and prefix can be set from the cruise control 
config file.

Setup: 
This project requires a project named "CruiseControl" in the same workspace, it has
been tested with Cruise Control 2.3.1.