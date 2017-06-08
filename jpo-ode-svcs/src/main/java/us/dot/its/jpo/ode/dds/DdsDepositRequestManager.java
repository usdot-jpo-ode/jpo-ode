package us.dot.its.jpo.ode.dds;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequest.EncodeType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeRequest;

public class DdsDepositRequestManager extends DdsRequestManager<DdsStatusMessage> {

    public DdsDepositRequestManager(OdeProperties odeProperties)
            throws us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException {
        super(odeProperties);
    }

    @Override
    protected DdsRequest buildDdsRequest(OdeRequest odeRequest) throws DdsRequestManagerException {

        DdsDepRequest ddsDepReq = new DdsDepRequest();

        if (odeRequest instanceof OdeDepRequest) {
            OdeDepRequest odeDepReq = (OdeDepRequest) odeRequest;
            String sEncodeType = StringUtils.upperCase(odeDepReq.getEncodeType());
            if (sEncodeType != null) {
                EncodeType encodeType = DdsRequest.EncodeType.valueOf(sEncodeType);
                ddsDepReq.setSystemDepositName(DdsDepositRequestManager
                        .systemName(odeDepReq).getName())
                        .setEncodeType(encodeType.name())
                        .setEncodedMsg(odeDepReq.getData());
            } else {
                throw new DdsRequestManagerException("Invalid or unsupported EncodeType Deposit: " + sEncodeType
                        + ". Supported encode types are: " + Arrays.asList(DdsRequest.EncodeType.values()));
            }
        } else {
            throw new DdsRequestManagerException("Invalid Request: " + odeRequest.toJson(odeProperties.getVerboseJson()));
       }
        return ddsDepReq;
    }
}
