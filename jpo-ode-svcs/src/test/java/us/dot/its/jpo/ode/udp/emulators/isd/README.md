1. Run the following command to compile the java file.
javac -classpath .:commons-codec-1.10.jar VsdDepositorToOde.java 

2. Run the following command to run the app.
java -classpath .:commons-codec-1.10.jar VsdDepositorToOde

3. The app does the following 3 things:
  a) Sends Service Request to ODE.
  b) Receives Service Response from ODE.
  c) Sends VSD to ODE.