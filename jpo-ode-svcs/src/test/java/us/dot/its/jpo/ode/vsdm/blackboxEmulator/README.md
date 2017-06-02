Follow the instructions below if running the java app through terminal.

1. Run the following command to compile the java file.
javac -classpath .:commons-codec-1.10.jar BlackBoxEmulator.java 

2. Run the following command to start the app. The ode is set to listen for VSD dialog on port 46753 by default.
java -classpath .:commons-codec-1.10.jar BlackBoxEmulator <odeIp> <OdePort> <SelfPort>

3. The app does the following 3 things:
  a) Sends Service Request to ODE.
  b) Receives Service Response from ODE.
  c) Sends VSD to ODE.
