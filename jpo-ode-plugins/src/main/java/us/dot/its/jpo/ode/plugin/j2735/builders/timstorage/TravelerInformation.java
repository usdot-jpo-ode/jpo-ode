package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class TravelerInformation
{
    private String timeStamp;

    private String urlB;

    private String packetID;

    private DataFrames dataFrames;

    private String msgCnt;

    public String getTimeStamp ()
    {
        return timeStamp;
    }

    public void setTimeStamp (String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public String getUrlB ()
    {
        return urlB;
    }

    public void setUrlB (String urlB)
    {
        this.urlB = urlB;
    }

    public String getPacketID ()
    {
        return packetID;
    }

    public void setPacketID (String packetID)
    {
        this.packetID = packetID;
    }

    public DataFrames getDataFrames ()
    {
        return dataFrames;
    }

    public void setDataFrames (DataFrames dataFrames)
    {
        this.dataFrames = dataFrames;
    }

    public String getMsgCnt ()
    {
        return msgCnt;
    }

    public void setMsgCnt (String msgCnt)
    {
        this.msgCnt = msgCnt;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [timeStamp = "+timeStamp+", urlB = "+urlB+", packetID = "+packetID+", dataFrames = "+dataFrames+", msgCnt = "+msgCnt+"]";
    }
}
