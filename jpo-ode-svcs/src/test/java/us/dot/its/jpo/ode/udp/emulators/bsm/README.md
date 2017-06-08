Follow the instructions below if running the java app through terminal.

1. Run the following command to compile the java file.
javac -classpath .:commons-codec-1.10.jar BsmDepositorToOde.java 

2. Run the following command to start the app. The ode is set to listen for bsms on port 46800 by default.
java -classpath .:commons-codec-1.10.jar BsmDepositorToOde <odeIp> <OdePort> <SelfPort>

3. The app then continuously sends uper encoded bsms to ode on 5 seconds interval.
