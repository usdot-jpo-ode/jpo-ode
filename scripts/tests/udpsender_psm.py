import socket
import time
import os

# Currently set to oim-dev environment's ODE
UDP_IP = '10.0.0.85'
UDP_PORT = 44940
MESSAGE = "011d0000201a0000021bd86891de75f84da101c13f042e2214141fff00022c2000270000000163b2cc7986010000"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

while True:
  time.sleep(1/20)
  print("sending PSM every 5 second")
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
