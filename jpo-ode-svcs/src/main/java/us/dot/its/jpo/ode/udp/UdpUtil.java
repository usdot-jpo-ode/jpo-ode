package us.dot.its.jpo.ode.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpUtil {

   private UdpUtil() {
      throw new UnsupportedOperationException("Cannot instantiate static class.");
   }

   public static class UdpUtilException extends Exception {
      private static final long serialVersionUID = 1L;

      public UdpUtilException(String errMsg, Exception e) {
         super(errMsg, e);
      }
   }

   //TODO open-ode
//   public static ServiceResponse createServiceResponse(ServiceRequest request, int expirationSeconds) {
//      ServiceResponse response = new ServiceResponse();
//      response.setDialogID(request.getDialogID());
//
//      ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(expirationSeconds);
//      response.setExpiration(new DDateTime(new DYear(expiresAt.getYear()), new DMonth(expiresAt.getMonthValue()),
//            new DDay(expiresAt.getDayOfMonth()), new DHour(expiresAt.getHour()), new DMinute(expiresAt.getMinute()),
//            new DSecond(expiresAt.getSecond()), new DOffset(0)));
//
//      response.setGroupID(request.getGroupID());
//      response.setRequestID(request.getRequestID());
//      response.setSeqID(SemiSequenceID.svcResp);
//
//      response.setHash(new Sha256Hash(ByteBuffer.allocate(32).putInt(1).array()));
//      return response;
//   }
//
//   public static void send(DatagramSocket sock, AbstractData message, String ip, int port)
//         throws UdpUtilException {
//      try {
//         byte[] messageBytes = J2735.getPERUnalignedCoder().encode(message).array();
//         UdpUtil.send(sock, messageBytes, ip, port);
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         throw new UdpUtilException("Failed to encode and send message.", e);
//      }
//   }

   public static void send(DatagramSocket sock, byte[] msgBytes, String ip, int port)
         throws UdpUtilException {
      try {
         sock.send(new DatagramPacket(msgBytes, msgBytes.length, new InetSocketAddress(ip, port)));
      } catch (IOException e) {
         throw new UdpUtilException("Failed to encode and send message.", e);
      }
   }
}
