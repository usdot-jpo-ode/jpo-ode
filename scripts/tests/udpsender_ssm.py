import socket
import time
import os

# Currently set to oim-dev environment's ODE
UDP_IP = os.getenv('DOCKER_HOST_IP')
UDP_PORT = 44900
MESSAGE = "001e120000000005e9c04071a26614c06000040ba0"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

while True:
  time.sleep(5)
  print("sending SSM every 5 second")
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
