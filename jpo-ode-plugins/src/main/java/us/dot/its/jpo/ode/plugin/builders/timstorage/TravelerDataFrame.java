package us.dot.its.jpo.ode.plugin.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.model.OdeObject;

@JsonPropertyOrder({ "sspTimRights", "frameType", "startYear", "startTime", "duratonTime", "priority",
      "sspLocationRights", "regions", "sspMsgRights1", "sspMsgRights2", "content", "url" })
public class TravelerDataFrame extends OdeObject {
   private static final long serialVersionUID = 1L;

   private String sspTimRights;

   private FrameType frameType;

   private MsgId msgId;

   private String startYear;

   private String startTime;

   private String duratonTime;

   private String priority;

   private String sspLocationRights;

   private Regions[] regions;

   private String sspMsgRights1;

   private String sspMsgRights2;

   @JsonProperty("tcontent")
   private Content tcontent;

   private String url;

   public String getSspLocationRights() {
      return sspLocationRights;
   }

   public void setSspLocationRights(String sspLocationRights) {
      this.sspLocationRights = sspLocationRights;
   }

   public Regions[] getRegions() {
      return regions;
   }

   public void setRegions(Regions[] regions) {
      this.regions = regions;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public FrameType getFrameType() {
      return frameType;
   }

   public void setFrameType(FrameType frameType) {
      this.frameType = frameType;
   }

   public String getDuratonTime() {
      return duratonTime;
   }

   public void setDuratonTime(String duratonTime) {
      this.duratonTime = duratonTime;
   }

   public Content getContent() {
      return tcontent;
   }

   public void setContent(Content content) {
      this.tcontent = content;
   }

   public String getStartTime() {
      return startTime;
   }

   public void setStartTime(String startTime) {
      this.startTime = startTime;
   }

   public String getStartYear() {
      return startYear;
   }

   public void setStartYear(String startYear) {
      this.startYear = startYear;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public MsgId getMsgId() {
      return msgId;
   }

   public void setMsgId(MsgId msgId) {
      this.msgId = msgId;
   }

   public String getSspMsgRights2() {
      return sspMsgRights2;
   }

   public void setSspMsgRights2(String sspMsgRights2) {
      this.sspMsgRights2 = sspMsgRights2;
   }

   public String getSspTimRights() {
      return sspTimRights;
   }

   public void setSspTimRights(String sspTimRights) {
      this.sspTimRights = sspTimRights;
   }

   public String getSspMsgRights1() {
      return sspMsgRights1;
   }

   public void setSspMsgRights1(String sspMsgRights1) {
      this.sspMsgRights1 = sspMsgRights1;
   }
}
