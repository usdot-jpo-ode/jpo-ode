package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequest.SystemName;
import us.dot.its.jpo.ode.model.OdeRequest;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;

/**
 * Test set for DdsRequestManager static methods
 *
 */
public class DdsRequestManagerTest {

    /**
     * Verify correct system name from known deposit data sources
     */
    @Test
    public void testExpectedSystemNames(@Mocked OdeRequest mockOdeRequest) {

        // Test 1: SDC --> SDC
        new Expectations() {
            {
                mockOdeRequest.getDataSource();
                result = DataSource.SDC;
            }
        };

        assertEquals("Expected SDC --> SDC: ", SystemName.SDC, DdsRequestManager.systemName(mockOdeRequest));

        // Test 2: DEPOSIT_SDC --> SDC
        new Expectations() {
            {
                mockOdeRequest.getDataSource();
                result = DataSource.DEPOSIT_SDC;
            }
        };
        assertEquals("Expected DEPOSIT_SDC --> SDC: ", SystemName.SDC, DdsRequestManager.systemName(mockOdeRequest));

        // Test 3: SDW --> SDW
        new Expectations() {
            {
                mockOdeRequest.getDataSource();
                result = DataSource.SDW;
            }
        };
        assertEquals("Expected SDW --> SDW: ", SystemName.SDW, DdsRequestManager.systemName(mockOdeRequest));

        // Test 4: DEPOSIT_SDW --> SDW
        new Expectations() {
            {
                mockOdeRequest.getDataSource();
                result = DataSource.DEPOSIT_SDW;
            }
        };
        assertEquals("Expected DEPOSIT_SDW --> SDW: ", SystemName.SDW, DdsRequestManager.systemName(mockOdeRequest));

        // Test 5: SDPC --> SDPC
        new Expectations() {
            {
                mockOdeRequest.getDataSource();
                result = DataSource.SDPC;
            }
        };
        assertEquals("Expected SDPC --> SDPC: ", SystemName.SDPC, DdsRequestManager.systemName(mockOdeRequest));
    }

    /**
     * Verify correct defaulting behavior from unknown deposit data sources
     */
    @Test
    public void testUnexpectedSystemNames(@Mocked OdeRequest mockOdeRequest) {

        // Test 1: Subscription --> SDC
        new Expectations() {
            {
                mockOdeRequest.getDataSource();
                result = DataSource.TEST_UPLOAD;
                mockOdeRequest.getRequestType();
                result = OdeRequestType.Subscription;
            }
        };

        assertEquals("Expected OdeRequestType.Subscription --> SDC: ", SystemName.SDC,
                DdsRequestManager.systemName(mockOdeRequest));

        // Test 2: Query --> SDW
        new Expectations() {
            {
                mockOdeRequest.getDataSource();
                result = DataSource.TEST_UPLOAD;
                mockOdeRequest.getRequestType();
                result = OdeRequestType.Query;
            }
        };
        assertEquals("Expected OdeRequestType.Query --> SDW: ", SystemName.SDW,
                DdsRequestManager.systemName(mockOdeRequest));
    }

}
