package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TravelerDataFrame extends Asn1Object {
   private static final long serialVersionUID = 1031726280264573342L;

   public enum TravelerInfoType {
      unknown, advisory, roadSignage, commercialSignage
   }

   
   private TravelerInfoType frameType;
   private J2735MessageId msgId;
   private Integer startYear;
   private Integer startTime;
   private Integer duratonTime;
   private Integer priority;
   private J2735Position3D commonAnchor;
   private Integer commonLaneWidth;
   private J2735DirectionOfUse commonDirectionality;
   private ArrayList<J2735GeographicalPath> regions;
   private J2735MessageContent content;
   private String url;

   public TravelerInfoType getFrameType() {
      return frameType;
   }

   public void setFrameType(TravelerInfoType frameType) {
      this.frameType = frameType;
   }

   public J2735MessageId getMessageId() {
      return msgId;
   }

   public J2735TravelerDataFrame setMessageId(J2735MessageId msgId) {
      this.msgId = msgId;
      return this;
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

   public J2735Position3D getCommonAnchor() {
      return commonAnchor;
   }

   public void setCommonAnchor(J2735Position3D commonAnchor) {
      this.commonAnchor = commonAnchor;
   }

   public Integer getCommonLaneWidth() {
      return commonLaneWidth;
   }

   public void setCommonLaneWidth(Integer commonLaneWidth) {
      this.commonLaneWidth = commonLaneWidth;
   }

   public J2735DirectionOfUse getCommonDirectionality() {
      return commonDirectionality;
   }

   public void setCommonDirectionality(J2735DirectionOfUse commonDirectionality) {
      this.commonDirectionality = commonDirectionality;
   }

   public ArrayList<J2735GeographicalPath> getRegions() {
      return regions;
   }

   public J2735TravelerDataFrame setRegions(ArrayList<J2735GeographicalPath> regions) {
      this.regions = regions;
      return this;
   }

   public J2735MessageContent getMessageContent() {
      return content;
   }

   public void setMessageContent(J2735MessageContent content) {
      this.content = content;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
  
}
