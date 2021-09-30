import socket
import time
import os

# Currently set to oim-dev environment's ODE
UDP_IP = os.getenv('DOCKER_HOST_IP')
UDP_PORT = 44910
MESSAGE = "001338000817a780000089680500204642b342b34802021a15a955a940181190acd0acd20100868555c555c00104342aae2aae002821a155715570"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

while True:
  time.sleep(5)
  print("sending SPaT every 5 second")
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
