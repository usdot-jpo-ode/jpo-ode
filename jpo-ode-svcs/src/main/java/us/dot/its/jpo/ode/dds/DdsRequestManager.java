package us.dot.its.jpo.ode.dds;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequest.SystemName;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeRequest;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public abstract class DdsRequestManager<T> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DdsClient<T> ddsClient;
    private Session session;

    private boolean connected = false;

    private OdeProperties odeProperties;

    private WebSocketEndpoint<T> wsClient;

    public DdsRequestManager(OdeProperties odeProperties) throws DdsRequestManagerException {

        this.odeProperties = odeProperties;

        try {
            // Class<?> decoder = null;
            // if (odeRequest.getRequestType() == OdeRequestType.Deposit) {
            // decoder = DepositResponseDecoder.class;
            // } else {
            // if (odeRequest.getDataType() == OdeDataType.VehicleData) {
            // decoder = VsdDecoder.class;
            // } else if (odeRequest.getDataType() == OdeDataType.AggregateData)
            // {
            // decoder = VsdDecoder.class;
            // } else if (odeRequest.getDataType() ==
            // OdeDataType.IntersectionData) {
            // decoder = IsdDecoder.class;
            // } else if (odeRequest.getDataType() == OdeDataType.AdvisoryData)
            // {
            // decoder = AsdDecoder.class;
            // } else if (odeRequest.getDataType() == OdeDataType.MapData) {
            // decoder = IsdDecoder.class;
            // } else if (odeRequest.getDataType() == OdeDataType.SPaTData) {
            // decoder = IsdDecoder.class;
            // }
            // }
            ddsClient = new DdsClient<>(this.odeProperties.getDdsCasUrl(), this.odeProperties.getDdsCasUsername(),
                    this.odeProperties.getDdsCasPassword(), this.odeProperties.getDdsWebsocketUrl(), null, null);

        } catch (Exception e) {
            throw new DdsRequestManagerException("Error initializing DdsRequestManager" + e);
        }
    }

    //@SuppressWarnings("unchecked")
    public Session connect(WebSocketMessageHandler<T> messageHandler, Class<?> decoder)
            throws DdsRequestManagerException {

        try {
            wsClient = ddsClient.login((Class<? extends WebSocketMessageDecoder<T>>) decoder, messageHandler);

            session = wsClient.connect();

            connected = session != null;

            return session;
        } catch (Exception e) {
            throw new DdsRequestManagerException("Error connecting to DDS", e);
        }
    }

    public void sendRequest(OdeRequest odeRequest) throws DdsRequestManagerException {
        try {
            if (session == null) {
                logger.info("Connecting to DDS");
                session = wsClient.connect();
                connected = session != null;
            }

            if (session != null) {
                // Send the new request
                DdsRequest ddsRequest = buildDdsRequest(odeRequest);
                if (ddsRequest != null) {
                    String sDdsRequest = ddsRequest.toString();

                    if (odeRequest.getRequestType() != OdeRequestType.Deposit)
                        logger.info("Sending DDS request: {}", sDdsRequest);

                    logger.info("Sending request to DDS: {}", odeRequest);
                    EventLogger.logger.info("Sending request to DDS: {}", odeRequest);
                    wsClient.send(sDdsRequest);
                }
            } else {
                throw new DdsRequestManagerException("DDS Client Session is null, probably NOT CONNECTED.");
            }
        } catch (Exception e) {
            try {
                wsClient.close();
                connected = false;
            } catch (WebSocketException e1) {
                logger.error("Error Closing DDS Client.", e1);
            }
            throw new DdsRequestManagerException("Error sending Data Request: " + e);
        }
    }

    protected abstract DdsRequest buildDdsRequest(OdeRequest odeRequest) throws DdsRequestManagerException;

    // private DdsRequest buildDdsRequest(OdeRequest odeRequest)
    // throws DdsRequestManagerException, OdeException, IOException {
    // OdeStatus status = new OdeStatus();
    // DdsRequest ddsRequest;
    // OdeRequestType requestType = odeRequest.getRequestType();
    // if (requestType == OdeRequestType.Subscription) {
    // ddsRequest = new DdsSubRequest()
    // .setSystemSubName(systemName(odeRequest).getName());
    // String serviceRegion = odeProperties.getServiceRegion();
    // if (serviceRegion == null) {
    // ddsRequest
    // .setNwLat(odeRequest.getNwLat()).setNwLon(odeRequest.getNwLon())
    // .setSeLat(odeRequest.getSeLat()).setSeLon(odeRequest.getSeLon());
    // } else {
    // J2735GeoRegion geoRegion = new J2735GeoRegion(serviceRegion);
    // ddsRequest
    // .setNwLat(geoRegion.getNwCorner().getLatitude())
    // .setNwLon(geoRegion.getNwCorner().getLongitude())
    // .setSeLat(geoRegion.getSeCorner().getLatitude())
    // .setSeLon(geoRegion.getSeCorner().getLongitude());
    // }
    // } else if (requestType == OdeRequestType.Query) {
    // DdsQryRequest qryRequest = new DdsQryRequest()
    // .setSystemQueryName(systemName(odeRequest).getName());
    //
    // if (odeRequest instanceof OdeQryRequest) {
    // OdeQryRequest odeQuery = (OdeQryRequest) odeRequest;
    // qryRequest.setStartDate(odeQuery.getStartDate());
    // qryRequest.setEndDate(odeQuery.getEndDate());
    // /*
    // * Because some data are bundled (VSD, for instance), never skip.
    // * ODE will unbundle and skip the records as needed.
    // */
    // qryRequest.setSkip(0);
    //
    // /*
    // * Because some data are bundled (VSD, for instance), add the skip
    // * to the limit to ensure that we will get enough records to skip
    // * and limit independent of the data source.
    // */
    // if (odeQuery.getLimit() != null) {
    // qryRequest.setLimit(
    // (odeQuery.getSkip() != null ? odeQuery.getSkip().intValue() : 0) +
    // odeQuery.getLimit().intValue());
    // }
    // }
    // ddsRequest = qryRequest;
    // ddsRequest
    // .setNwLat(odeRequest.getNwLat()).setNwLon(odeRequest.getNwLon())
    // .setSeLat(odeRequest.getSeLat()).setSeLon(odeRequest.getSeLon());
    // } else if (requestType == OdeRequestType.Deposit) {
    // OdeDepRequest odeDepReq = (OdeDepRequest) odeRequest;
    // if (StringUtils.isNotEmpty(odeDepReq.getData()))
    // ddsRequest = buildDepositRequest(odeDepReq);
    // else
    // ddsRequest = null;
    // } else {
    // status.setCode(OdeStatus.Code.INVALID_REQUEST_TYPE_ERROR)
    // .setMessage(
    // String.format(
    // "Invalid request type %s. Valid request types are %s",
    // requestType.getShortName(),
    // OdeRequestType.shortNames()));
    // logger.error(status.toString());
    // throw new DdsRequestManagerException(status.toString());
    // }
    //
    // if (requestType != OdeRequestType.Deposit) {
    // ddsRequest
    // .setResultEncoding(DdsRequest.EncodeType.base64.name());
    //
    // OdeDataType dataType = odeRequest.getDataType();
    // switch (dataType) {
    // case IntersectionData:
    // case MapData:
    // case SPaTData:
    // ddsRequest.setDialogID(DdsRequest.Dialog.ISD.getId());
    // break;
    // case VehicleData:
    // case AggregateData:
    // ddsRequest.setDialogID(DdsRequest.Dialog.VSD.getId());
    // break;
    // case AdvisoryData:
    // ddsRequest.setDialogID(DdsRequest.Dialog.ASD.getId());
    // break;
    // default:
    // status.setCode(OdeStatus.Code.DATA_TYPE_NOT_SUPPORTED)
    // .setMessage(
    // String.format(
    // "Invalid data type %s requested. Valid data types are %s",
    // dataType, OdeDataType.shortNames()));
    // logger.error(status.toString());
    // throw new DdsRequestManagerException(status.getMessage());
    // }
    // }
    //
    //
    // return ddsRequest;
    // }

    public static SystemName systemName(OdeRequest odeRequest) {
        SystemName sysName;

        OdeRequest.DataSource dataSource;
        
        if (odeRequest.getDataSource() != null) {
            dataSource = odeRequest.getDataSource();
        } else {
            dataSource = defaultDataSource(odeRequest);
        }
            
        switch (dataSource) {
        case SDC:
        case DEPOSIT_SDC:
            sysName = SystemName.SDC;
            break;
        case SDW:
        case DEPOSIT_SDW:
            sysName = SystemName.SDW;
            break;
        case SDPC:
            sysName = SystemName.SDPC;
            break;
        default:
            sysName = defaultSystemName(odeRequest);
        }

        return sysName;
    }

    public static DataSource defaultDataSource(OdeRequest odeRequest) {
        DataSource dataSource;
        if (odeRequest.getRequestType() == OdeRequestType.Subscription)
            dataSource = DataSource.SDC;
        else
            dataSource = DataSource.SDW;
        return dataSource;
    }

    public static SystemName defaultSystemName(OdeRequest odeRequest) {
        SystemName sysName;
        if (odeRequest.getRequestType() == OdeRequestType.Subscription)
            sysName = SystemName.SDC;
        else
            sysName = SystemName.SDW;
        return sysName;
    }

    public DdsClient<T> getDdsClient() {
        return ddsClient;
    }

    public void close() throws DdsRequestManagerException {
        connected = false;
        session = null;

        if (wsClient != null) {
            try {
                logger.info("Closing WebSocket Client.");
                wsClient.close();
                wsClient = null;
            } catch (WebSocketException e) {
                throw new DdsRequestManagerException("Error closing DDS Client: ", e);
            }
        }
    }

    public Session getSession() {
        return session;
    }

    public boolean isConnected() {
        return connected;
    }

    public WebSocketEndpoint<T> getWsClient() {
        return wsClient;
    }

    public static class DdsRequestManagerException extends Exception {
        private static final long serialVersionUID = 1L;

        public DdsRequestManagerException(String message, Exception e) {
            super(message, e);
        }

        public DdsRequestManagerException(String message) {
            super(message);
        }
    }
    
    public OdeProperties getOdeProperties() {
        return odeProperties;
    }
    
    public void setDdsClient(DdsClient<T> pDdsClient) {
        this.ddsClient = pDdsClient;
    }
    
    public void setWsClient(WebSocketEndpoint<T> pWsClient) {
        this.wsClient = pWsClient;
    }
    
    public void setLogger(Logger newLogger) {
        this.logger = newLogger;
    }
}
