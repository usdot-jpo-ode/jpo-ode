package us.dot.its.jpo.ode;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class OdeTimJsonTopologyTest {

    private OdeTimJsonTopology odeTimJsonTopology;
    private KafkaStreams mockStreams;
    private ReadOnlyKeyValueStore<String, String> mockStore;
    private OdeKafkaProperties mockOdeKafkaProps;

    @BeforeEach
    void setUp() throws SecurityException, IllegalArgumentException {
        mockOdeKafkaProps = mock(OdeKafkaProperties.class);
        odeTimJsonTopology = new OdeTimJsonTopology(mockOdeKafkaProps);
        mockStreams = mock(KafkaStreams.class);
        mockStore = mock(ReadOnlyKeyValueStore.class);

        OdeTimJsonTopology.streams = mockStreams;
    }

    @AfterEach
    void tearDown() {
        OdeTimJsonTopology.streams = null;
    }

    @Test
    void testStart() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.NOT_RUNNING);
        doNothing().when(mockStreams).start();

        odeTimJsonTopology.start();

        verify(mockStreams).start();
    }

    @Test
    void testStartWhenAlreadyRunning() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.RUNNING);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            odeTimJsonTopology.start();
        });

        assertEquals("Start called while streams is already running.", exception.getMessage());
    }

    @Test
    void testStop() {
        doNothing().when(mockStreams).close();

        odeTimJsonTopology.stop();

        verify(mockStreams).close();
    }

    @Test
    void testIsRunning() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.RUNNING);

        assertTrue(odeTimJsonTopology.isRunning());
    }

    @Test
    void testIsNotRunning() {
        when(mockStreams.state()).thenReturn(KafkaStreams.State.NOT_RUNNING);

        assertFalse(odeTimJsonTopology.isRunning());
    }

    @Test
    void testBuildTopology() {
        Topology topology = odeTimJsonTopology.buildTopology();
        assertNotNull(topology);
    }

    @Test
    void testQuery() {
        String uuid = "test-uuid";
        String expectedValue = "test-value";

        when(mockStreams.store(any(StoreQueryParameters.class))).thenReturn(mockStore);
        when(mockStore.get(uuid)).thenReturn(expectedValue);

        String result = odeTimJsonTopology.query(uuid);

        assertEquals(expectedValue, result);
    }
}