package us.dot.its.jpo.ode.dds;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequest.EncodeType;
import us.dot.its.jpo.ode.model.OdeDepRequest;

public class DdsDepositRequestManager extends DdsRequestManager<DdsStatusMessage> {

    public DdsDepositRequestManager(OdeProperties odeProperties)
            throws us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException {
        super(odeProperties);
    }

    @Override
    protected DdsRequest buildDdsRequest(OdeDepRequest odeDepRequest) throws DdsRequestManagerException {

        DdsDepRequest ddsDepReq = new DdsDepRequest();

        String sEncodeType = StringUtils.lowerCase(odeDepRequest.getEncodeType());
        if (sEncodeType != null) {
            EncodeType encodeType = DdsRequest.EncodeType.valueOf(sEncodeType);
            ddsDepReq.setSystemDepositName(DdsDepositRequestManager.systemName(odeDepRequest).getName())
                    .setEncodeType(encodeType.name()).setEncodedMsg(odeDepRequest.getData());
        } else {
            throw new DdsRequestManagerException("Invalid or unsupported EncodeType Deposit: " + sEncodeType
                    + ". Supported encode types are: " + Arrays.asList(DdsRequest.EncodeType.values()));
        }
        return ddsDepReq;
    }
}
