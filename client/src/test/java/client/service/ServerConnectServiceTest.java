package client.service;

import client.scenes.MainCtrl;
import client.scenes.ServerConnectController;
import client.utils.ServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class ServerConnectServiceTest {

    @InjectMocks
    ServerConnectService serverConnectService;
    @Mock
    ServerUtils serverUtils;
    @Mock
    MainCtrl mainCtrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getterAndSetterTest() {
        ServerConnectController mockCtrl = Mockito.mock(ServerConnectController.class);
        serverConnectService.setServerConnectController(mockCtrl);

        assertEquals(mockCtrl,serverConnectService.getServerConnectController());
    }

    @Test
    void joinServerWithEmptyAddressTest() {
        ServerConnectController mockCtrl = Mockito.mock(ServerConnectController.class);
        serverConnectService.setServerConnectController(mockCtrl);
        Mockito.doThrow(new IllegalArgumentException("The server address should not be empty."))
                .when(mockCtrl).showConnectionError();

        assertThrows(IllegalArgumentException.class, () -> serverConnectService.joinServer(""));
    }

    @Test
    void joinServerTest() {
        Mockito.doReturn(true).when(serverUtils).testConnection();

        serverConnectService.joinServer("an address");

        verify(serverUtils).testConnection();
        verify(mainCtrl).showOverview();
        verify(mainCtrl).testConnectionToWebSocket(any(String.class));
    }

}