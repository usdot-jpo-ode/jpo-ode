import socket
import time
import os

# Currently set to oim-dev environment's ODE
UDP_IP = os.getenv('DOCKER_HOST_IP')
UDP_PORT = 44950
MESSAGE = "0029250a010c0c0a301fa04edb8816396666f30c34000a00002c43e94bba4200020002ec00280002"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

while True:
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
  time.sleep(5)
  print("sending SDSM every 5 seconds")