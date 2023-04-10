import socket
import time
import os

# Currently set to oim-dev environment's ODE
UDP_IP = os.getenv('DOCKER_HOST_IP')
UDP_PORT = 47900
MESSAGE = "005f498718cca69ec1a04600000100105d9b46ec5be401003a0103810040038081d4001f80d07016da410000000000000bbc2b0f775d9b0309c271431fa166ee0a27fff93f136b8205a0a107fb2ef979f4c5bfaeec97e4ad70c2fb36cd9730becdb355cc2fd2a7556b160b98b46ab98ae62c185fa55efb468d5b4000000004e2863f42cddc144ff7980040401262cdd7b809c509f5c62cdd35519c507b9062cdcee129c505cf262cdca5ff9c50432c62cdc5d3d9c502e3e62cdc13e79c501e9262cdbca2d9c5013ee62cdb80359c500e6a62cdb36299c500bc862cdaec1d9c50093c62cdaa2109c5006ea1080203091a859eeebb36006001830001aad27f4ff7580001aad355e39b5880a30029d6585009ef808332d8d9f80c3855151b38c772f765007967ec1170bcb7937f5cb880a25a52863493bcb87570dbcb5abc6bfb2faec606cfa34eb95a24790b2017366d3aabe7729e"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

while True:
  time.sleep(5)
  print("sending TIM every 5 second")
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
