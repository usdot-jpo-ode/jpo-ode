import socket
import time
import os

# Currently set to oim-dev environment's ODE
# UDP_IP = os.getenv('DOCKER_HOST_IP')
UDP_IP = 'localhost'
UDP_PORT = 44940
MESSAGE = "00205D7FFFC37FEAFD3D0D511524BEABA433E42540C03BF2C4840C497E676A0E4C556C35F6FFFEC9289090C5DD7CA7D9149826FFED175FB22FB658EB94D1B12C3510D0200FF8DBFF3FCD4ABFDD4B1A38DAE01C267CCC19408652AA0200421B31"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

while True:
  time.sleep(5)
  print("sending PSM every 5 second")
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
