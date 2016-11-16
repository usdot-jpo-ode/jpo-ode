package us.dot.its.jpo.ode.asn;

import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.ExitService;
import com.bah.ode.asn.oss.dsrc.GenericSignage;
import com.bah.ode.asn.oss.dsrc.SpeedLimit;
import com.bah.ode.asn.oss.dsrc.TravelerInformation.DataFrames.Sequence_;
import com.bah.ode.asn.oss.dsrc.TravelerInformation.DataFrames.Sequence_.Content;
import com.bah.ode.asn.oss.dsrc.TravelerInformation.DataFrames.Sequence_.MsgId;
import com.bah.ode.asn.oss.dsrc.WorkZone;
import com.bah.ode.asn.oss.itis.ITIScodesAndText;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeAdvisoryDataFrame extends OdeObject {
   private static final long serialVersionUID = 1031726280264573342L;

   public enum TravelerInfoType {
      unknown, advisory, roadSignage, commercialSignage
   }

   public class MessageId extends OdeChoice {

      private static final long serialVersionUID = 5091542631221985473L;

      public String furtherInfoID_chosen;
      public OdeRoadSignId roadSignID_chosen;
      
      public MessageId(MsgId msgId) {
         super();

         int chosenFlag = msgId.getChosenFlag();
         switch (chosenFlag) {
         case MsgId.furtherInfoID_chosen:
            if (msgId.hasFurtherInfoID())
               setChosenField("furtherInfoID_chosen", CodecUtils.toHex(msgId.getFurtherInfoID().byteArrayValue()));
            break;
         case MsgId.roadSignID_chosen:
            if (msgId.hasRoadSignID())
               setChosenField("roadSignID_chosen", new OdeRoadSignId(msgId.getRoadSignID()));
            break;
         }
      }

   }
   
   public class MessageContent extends OdeChoice {
      
      private static final long serialVersionUID = 3472684479212369295L;
      
      public class CodeOrText extends OdeChoice {

         private static final long serialVersionUID = 1686363207723189470L;

         public Integer code_chosen;
         public String text_chosen;
         
         public CodeOrText (Integer code, String text) {
            super();
            setChosenField("code_chosen", code);
            setChosenField("text_chosen", text);
         }
      }

      public List<CodeOrText> advisory_chosen;
      public List<CodeOrText> exitService_chosen;
      public List<CodeOrText> genericSign_chosen;
      public List<CodeOrText> speedLimit_chosen;
      public List<CodeOrText> workZone_chosen;
      
      public MessageContent(Content content) {
         super();

         int chosenFlag = content.getChosenFlag();
         
         switch (chosenFlag) {
         case Content.advisory_chosen:
            setChosenField("advisory_chosen", new ArrayList<CodeOrText>());
            if (content.hasAdvisory()) {
               ITIScodesAndText advisory = content.getAdvisory();
               for (ITIScodesAndText.Sequence_ e : advisory.elements) {
                  if (e.item != null) {
                     switch (e.item.getChosenFlag()) {
                     case ITIScodesAndText.Sequence_.Item.itis_chosen:
                        if (e.item.hasItis())
                           advisory_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
                        break;
                     case ITIScodesAndText.Sequence_.Item.text_chosen:
                        if (e.item.hasText())
                           advisory_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
                        break;
                     }
                  }
               }
            }
            break;
         case Content.exitService_chosen:
            setChosenField("exitService_chosen", new ArrayList<CodeOrText>());
            ExitService exitService = content.getExitService();
            for (ExitService.Sequence_ e : exitService.elements) {
               if (e.item != null) {
                  switch (e.item.getChosenFlag()) {
                  case ExitService.Sequence_.Item.itis_chosen:
                     if (e.item.hasItis())
                        exitService_chosen.add(
                           new CodeOrText(e.item.getItis().intValue(), null));
                     break;
                  case ExitService.Sequence_.Item.text_chosen:
                     if (e.item.hasText())
                        exitService_chosen.add(new CodeOrText(null,
                           e.item.getText().stringValue()));
                     break;
                  }
               }
            }
            break;
         case Content.genericSign_chosen:
            setChosenField("genericSign_chosen", new ArrayList<CodeOrText>());
            GenericSignage genericSign = content.getGenericSign();
            for (GenericSignage.Sequence_ e : genericSign.elements) {
               if (e.item != null) {
                  switch (e.item.getChosenFlag()) {
                  case GenericSignage.Sequence_.Item.itis_chosen:
                     if (e.item.hasItis())
                        genericSign_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
                     break;
                  case GenericSignage.Sequence_.Item.text_chosen:
                     if (e.item.hasText())
                        genericSign_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
                     break;
                  }
               }
            }
            break;
         case Content.speedLimit_chosen:
            setChosenField("speedLimit_chosen", new ArrayList<CodeOrText>());
            SpeedLimit speedLimit = content.getSpeedLimit();
            for (SpeedLimit.Sequence_ e : speedLimit.elements) {
               if (e.item != null) {
                  switch (e.item.getChosenFlag()) {
                  case SpeedLimit.Sequence_.Item.itis_chosen:
                     if (e.item.hasItis())
                        speedLimit_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
                     break;
                  case SpeedLimit.Sequence_.Item.text_chosen:
                     if (e.item.hasText())
                        speedLimit_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
                     break;
                  }
               }
            }
            break;
         case Content.workZone_chosen:
            setChosenField("workZone_chosen", new ArrayList<CodeOrText>());
            WorkZone workZone = content.getWorkZone();
            for (WorkZone.Sequence_ e : workZone.elements) {
               if (e.item != null) {
                  switch (e.item.getChosenFlag()) {
                  case WorkZone.Sequence_.Item.itis_chosen:
                     if (e.item.hasItis())
                        workZone_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
                     break;
                  case WorkZone.Sequence_.Item.text_chosen:
                     if (e.item.hasText())
                        workZone_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
                     break;
                  }
               }
            }
            break;
         }
      }

   }
   
   private TravelerInfoType frameType;
   private MessageId msgId;
   private Integer startYear;
   private Integer startTime;
   private Integer duratonTime;
   private Integer priority;
   private OdePosition3D commonAnchor;
   private Integer commonLaneWidth;
   private OdeDirectionOfUse commonDirectionality;
   private ArrayList<OdeValidRegion> regions;
   private MessageContent content;
   private String url;

   public OdeAdvisoryDataFrame(Sequence_ element) {
      if (element.hasCommonAnchor())
         setCommonAnchor(new OdePosition3D(element.getCommonAnchor()));
      if (element.hasCommonDirectionality())
         setCommonDirectionality(OdeDirectionOfUse.valueOf(element.getCommonDirectionality().name()));
      if (element.hasCommonLaneWidth())
         setCommonLaneWidth(element.getCommonLaneWidth().intValue());
      if (element.content != null)
         setMessageContent(new MessageContent(element.content));
      if (element.duratonTime != null)
         setDuratonTime(element.duratonTime.intValue());
      if (element.frameType != null)
         setFrameType(TravelerInfoType.valueOf(element.frameType.name()));
      if (element.msgId != null)
         setMessageId(new MessageId(element.msgId));
      if (element.priority != null)
         setPriority(element.priority.intValue());
      if (element.regions != null)
         setRegions(OdeValidRegion.createList(element.regions));
      if (element.startTime != null)
         setStartTime(element.startTime.intValue());
      if (element.hasStartYear())
         setStartYear(element.getStartYear().intValue());
      if (element.url != null)
         setUrl(element.url.stringValue());
   }

   public TravelerInfoType getFrameType() {
      return frameType;
   }

   public void setFrameType(TravelerInfoType frameType) {
      this.frameType = frameType;
   }

   public MessageId getMessageId() {
      return msgId;
   }

   public void setMessageId(MessageId msgId) {
      this.msgId = msgId;
   }

   public Integer getStartYear() {
      return startYear;
   }

   public void setStartYear(Integer startYear) {
      this.startYear = startYear;
   }

   public Integer getStartTime() {
      return startTime;
   }

   public void setStartTime(Integer startTime) {
      this.startTime = startTime;
   }

   public Integer getDuratonTime() {
      return duratonTime;
   }

   public void setDuratonTime(Integer duratonTime) {
      this.duratonTime = duratonTime;
   }

   public Integer getPriority() {
      return priority;
   }

   public void setPriority(Integer priority) {
      this.priority = priority;
   }

   public OdePosition3D getCommonAnchor() {
      return commonAnchor;
   }

   public void setCommonAnchor(OdePosition3D commonAnchor) {
      this.commonAnchor = commonAnchor;
   }

   public Integer getCommonLaneWidth() {
      return commonLaneWidth;
   }

   public void setCommonLaneWidth(Integer commonLaneWidth) {
      this.commonLaneWidth = commonLaneWidth;
   }

   public OdeDirectionOfUse getCommonDirectionality() {
      return commonDirectionality;
   }

   public void setCommonDirectionality(OdeDirectionOfUse commonDirectionality) {
      this.commonDirectionality = commonDirectionality;
   }

   public ArrayList<OdeValidRegion> getRegions() {
      return regions;
   }

   public void setRegions(ArrayList<OdeValidRegion> regions) {
      this.regions = regions;
   }

   public MessageContent getMessageContent() {
      return content;
   }

   public void setMessageContent(MessageContent content) {
      this.content = content;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((commonAnchor == null) ? 0 : commonAnchor.hashCode());
      result = prime * result + ((commonDirectionality == null) ? 0
            : commonDirectionality.hashCode());
      result = prime * result
            + ((commonLaneWidth == null) ? 0 : commonLaneWidth.hashCode());
      result = prime * result + ((content == null) ? 0 : content.hashCode());
      result = prime * result
            + ((duratonTime == null) ? 0 : duratonTime.hashCode());
      result = prime * result
            + ((frameType == null) ? 0 : frameType.hashCode());
      result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
      result = prime * result + ((priority == null) ? 0 : priority.hashCode());
      result = prime * result + ((regions == null) ? 0 : regions.hashCode());
      result = prime * result
            + ((startTime == null) ? 0 : startTime.hashCode());
      result = prime * result
            + ((startYear == null) ? 0 : startYear.hashCode());
      result = prime * result + ((url == null) ? 0 : url.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeAdvisoryDataFrame other = (OdeAdvisoryDataFrame) obj;
      if (commonAnchor == null) {
         if (other.commonAnchor != null)
            return false;
      } else if (!commonAnchor.equals(other.commonAnchor))
         return false;
      if (commonDirectionality != other.commonDirectionality)
         return false;
      if (commonLaneWidth == null) {
         if (other.commonLaneWidth != null)
            return false;
      } else if (!commonLaneWidth.equals(other.commonLaneWidth))
         return false;
      if (content == null) {
         if (other.content != null)
            return false;
      } else if (!content.equals(other.content))
         return false;
      if (duratonTime == null) {
         if (other.duratonTime != null)
            return false;
      } else if (!duratonTime.equals(other.duratonTime))
         return false;
      if (frameType != other.frameType)
         return false;
      if (msgId == null) {
         if (other.msgId != null)
            return false;
      } else if (!msgId.equals(other.msgId))
         return false;
      if (priority == null) {
         if (other.priority != null)
            return false;
      } else if (!priority.equals(other.priority))
         return false;
      if (regions == null) {
         if (other.regions != null)
            return false;
      } else if (!regions.equals(other.regions))
         return false;
      if (startTime == null) {
         if (other.startTime != null)
            return false;
      } else if (!startTime.equals(other.startTime))
         return false;
      if (startYear == null) {
         if (other.startYear != null)
            return false;
      } else if (!startYear.equals(other.startYear))
         return false;
      if (url == null) {
         if (other.url != null)
            return false;
      } else if (!url.equals(other.url))
         return false;
      return true;
   }

   
}
