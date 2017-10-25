package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class TravelerDataFrame
{
    private String sspLocationRights;

    private Regions regions;

    private String url;

    private FrameType frameType;

    private String duratonTime;

    private Content content;

    private String startTime;

    private String startYear;

    private String priority;

    private MsgId msgId;

    private String sspMsgRights2;

    private String sspTimRights;

    private String sspMsgRights1;

    public String getSspLocationRights ()
    {
        return sspLocationRights;
    }

    public void setSspLocationRights (String sspLocationRights)
    {
        this.sspLocationRights = sspLocationRights;
    }

    public Regions getRegions ()
    {
        return regions;
    }

    public void setRegions (Regions regions)
    {
        this.regions = regions;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    public FrameType getFrameType ()
    {
        return frameType;
    }

    public void setFrameType (FrameType frameType)
    {
        this.frameType = frameType;
    }

    public String getDuratonTime ()
    {
        return duratonTime;
    }

    public void setDuratonTime (String duratonTime)
    {
        this.duratonTime = duratonTime;
    }

    public Content getContent ()
    {
        return content;
    }

    public void setContent (Content content)
    {
        this.content = content;
    }

    public String getStartTime ()
    {
        return startTime;
    }

    public void setStartTime (String startTime)
    {
        this.startTime = startTime;
    }

    public String getStartYear ()
    {
        return startYear;
    }

    public void setStartYear (String startYear)
    {
        this.startYear = startYear;
    }

    public String getPriority ()
    {
        return priority;
    }

    public void setPriority (String priority)
    {
        this.priority = priority;
    }

    public MsgId getMsgId ()
    {
        return msgId;
    }

    public void setMsgId (MsgId msgId)
    {
        this.msgId = msgId;
    }

    public String getSspMsgRights2 ()
    {
        return sspMsgRights2;
    }

    public void setSspMsgRights2 (String sspMsgRights2)
    {
        this.sspMsgRights2 = sspMsgRights2;
    }

    public String getSspTimRights ()
    {
        return sspTimRights;
    }

    public void setSspTimRights (String sspTimRights)
    {
        this.sspTimRights = sspTimRights;
    }

    public String getSspMsgRights1 ()
    {
        return sspMsgRights1;
    }

    public void setSspMsgRights1 (String sspMsgRights1)
    {
        this.sspMsgRights1 = sspMsgRights1;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [sspLocationRights = "+sspLocationRights+", regions = "+regions+", url = "+url+", frameType = "+frameType+", duratonTime = "+duratonTime+", content = "+content+", startTime = "+startTime+", startYear = "+startYear+", priority = "+priority+", msgId = "+msgId+", sspMsgRights2 = "+sspMsgRights2+", sspTimRights = "+sspTimRights+", sspMsgRights1 = "+sspMsgRights1+"]";
    }
}
