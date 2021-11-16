import socket
import time
import os

# Currently set to oim-dev environment's ODE
UDP_IP = os.getenv('DOCKER_HOST_IP')
UDP_PORT = 44930
MESSAGE = "001d2130000010090bd341080d00855c6c0c6899853000a534f7c24cb29897694759b7c0"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

while True:
  time.sleep(5)
  print("sending SRM every 5 second")
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
