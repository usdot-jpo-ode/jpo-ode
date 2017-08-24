package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;

public class RawBsmMfSorter {
  
   private static final Logger logger = LoggerFactory.getLogger(RawBsmMfSorter.class); 
   
   private final OssJ2735Coder j2735Coder;
   
   public RawBsmMfSorter(OssJ2735Coder j2735Coder){
      this.j2735Coder = j2735Coder;
   }
   
   public OdeObject decodeBsm(BufferedInputStream is) {
      J2735MessageFrame mf = (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameStream(is);
      if (mf != null) {
         logger.debug("Decoding as a message frame.");
         return mf.getValue();
      } else {
         logger.debug("Decoding as raw BSM.");
         return j2735Coder.decodeUPERBsmStream(is);
      }
   }

   public OdeObject decodeBsm(byte[] bytes) {
      J2735MessageFrame mf = (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameBytes(bytes);
      if (mf != null) {

         logger.info("Decoding as message frame...");
         return mf.getValue();
      } else {

         logger.info("Decoding as bsm without message frame...");
         return j2735Coder.decodeUPERBsmBytes(bytes);
      }
         
      }
   }

