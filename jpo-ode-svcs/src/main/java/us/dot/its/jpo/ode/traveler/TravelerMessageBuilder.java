package us.dot.its.jpo.ode.traveler;

import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import java.nio.ByteBuffer;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrameList;
import us.dot.its.jpo.ode.j2735.dsrc.UniqueMSGID;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInfoType;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.MinuteOfTheYear;
import us.dot.its.jpo.ode.j2735.dsrc.SignPrority;
import us.dot.its.jpo.ode.j2735.dsrc.MinutesDuration;






/**
 * Created by anthonychen on 2/16/17.
 */
public class TravelerMessageBuilder {
    public TravelerInformation travelerInfo;

    private TravelerInformation buildTravelerInformation(TravelerInputData travInputData)
            throws ParseException, EncodeFailedException, EncodeNotSupportedException {


        travelerInfo = new TravelerInformation();
        travelerInfo.setDataFrames(buildDataFrames(travInputData));
        travelerInfo.setMsgCnt(new MsgCount(ti.getDataFrames().getSize()));
        ByteBuffer buf = ByteBuffer.allocate(9).put((byte)0).putLong(travInputData.anchorPoint.packetID);
        ti.setPacketID(new UniqueMSGID(buf.array()));
        return ti;
    }

    private TravelerDataFrameList buildDataFrames(TravelerInputData travInputData) throws ParseException {
        TravelerDataFrameList dataFrames = new TravelerDataFrameList();
        TravelerDataFrame dataFrame = new TravelerDataFrame();

        // -- Part I, Frame header
        dataFrame.setSspTimRights(new SSPindex(travInputData.anchorPoint.sspTimRights));
        dataFrame.setFrameType(TravelerInfoType.valueOf(travInputData.anchorPoint.infoType));
        dataFrame.setMsgId(getMessageId(travInputData));
        dataFrame.setStartYear(new DYear(getStartYear(travInputData)));
        dataFrame.setStartTime(new MinuteOfTheYear(getStartTime(travInputData)));
        dataFrame.setDuratonTime(new MinutesDuration(getDurationTime(travInputData)));
        dataFrame.setPriority(new SignPrority(travInputData.anchorPoint.priority));

        // -- Part II, Applicable Regions of Use
        dataFrame.setSspLocationRights(new SSPindex(travInputData.anchorPoint.sspLocationRights));
//        dataFrame.setRegions(buildRegions(travInputData));

        // -- Part III, Content
        dataFrame.setSspMsgRights1(new SSPindex(travInputData.anchorPoint.sspTypeRights));		// allowed message types
        dataFrame.setSspMsgRights2(new SSPindex(travInputData.anchorPoint.sspContentRights));	// allowed message content
        dataFrame.setContent(buildContent(travInputData));

        dataFrames.add(dataFrame);
        return dataFrames;
    }
}
