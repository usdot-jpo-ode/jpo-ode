package us.dot.its.jpo.ode;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class OdeTimJsonTopologyTest {

    private OdeTimJsonTopology odeTimJsonTopology;
    private KafkaStreams mockStreams;
    private ReadOnlyKeyValueStore<String, String> mockStore;
    private OdeProperties mockOdeProps;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        mockOdeProps = mock(OdeProperties.class);
        odeTimJsonTopology = new OdeTimJsonTopology(mockOdeProps);
        mockStreams = mock(KafkaStreams.class);
        mockStore = mock(ReadOnlyKeyValueStore.class);

        OdeTimJsonTopology.streams = mockStreams;
    }

    @AfterEach
    public void tearDown() {
        OdeTimJsonTopology.streams = null;
    }

    @Test
    public void testStart() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.NOT_RUNNING);
        doNothing().when(mockStreams).start();

        odeTimJsonTopology.start();

        verify(mockStreams).start();
    }

    @Test
    public void testStartWhenAlreadyRunning() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.RUNNING);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            odeTimJsonTopology.start();
        });

        assertEquals("Start called while streams is already running.", exception.getMessage());
    }

    @Test
    public void testStop() {
        doNothing().when(mockStreams).close();

        odeTimJsonTopology.stop();

        verify(mockStreams).close();
    }

    @Test
    public void testIsRunning() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.RUNNING);

        assertTrue(odeTimJsonTopology.isRunning());
    }

    @Test
    public void testIsNotRunning() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.NOT_RUNNING);

        assertFalse(odeTimJsonTopology.isRunning());
    }

    @Test
    public void testBuildTopology() {
        Topology topology = odeTimJsonTopology.buildTopology();
        assertNotNull(topology);
    }

    @Test
    public void testQuery() {
        String uuid = "test-uuid";
        String expectedValue = "test-value";

        when(mockStreams.store(any(StoreQueryParameters.class))).thenReturn(mockStore);
        when(mockStore.get(uuid)).thenReturn(expectedValue);

        String result = odeTimJsonTopology.query(uuid);

        assertEquals(expectedValue, result);
    }
}