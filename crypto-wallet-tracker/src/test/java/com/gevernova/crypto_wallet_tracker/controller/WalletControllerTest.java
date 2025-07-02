package com.gevernova.crypto_wallet_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;
import com.gevernova.crypto_wallet_tracker.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WalletControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void testAddEntry_Success() throws Exception {
        WalletRequestDTO requestDTO = WalletRequestDTO.builder()
                .coin("BTC")
                .units(1.5)
                .buyPrice(50000.0)
                .build();

        WalletResponseDTO responseDTO = WalletResponseDTO.builder()
                .id(1L)
                .coin("BTC")
                .units(1.5)
                .buyPrice(50000.0)
                .build();

        when(walletService.addEntry(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.coin").value("BTC"))
                .andExpect(jsonPath("$.units").value(1.5))
                .andExpect(jsonPath("$.buyPrice").value(50000.0));
    }

    @Test
    void testAddEntry_ValidationFailure() throws Exception {
        WalletRequestDTO requestDTO = WalletRequestDTO.builder()
                .coin("")  // invalid, blank
                .units(-1.0)
                .buyPrice(-100.0)
                .build();

        mockMvc.perform(post("/api/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllEntries_Success() throws Exception {
        List<WalletResponseDTO> responseList = List.of(
                WalletResponseDTO.builder().id(1L).coin("BTC").units(2.0).buyPrice(30000.0).build(),
                WalletResponseDTO.builder().id(2L).coin("ETH").units(5.0).buyPrice(2000.0).build()
        );

        when(walletService.getAllEntries()).thenReturn(responseList);

        mockMvc.perform(get("/api/wallet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].coin").value("BTC"));
    }

    @Test
    void testUpdateEntry_Success() throws Exception {
        WalletRequestDTO requestDTO = WalletRequestDTO.builder()
                .coin("ETH")
                .units(10.0)
                .buyPrice(2500.0)
                .build();

        WalletResponseDTO updated = WalletResponseDTO.builder()
                .id(1L)
                .coin("ETH")
                .units(10.0)
                .buyPrice(2500.0)
                .build();

        when(walletService.updateEntry(eq(1L), any())).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/wallet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coin").value("ETH"));
    }

    @Test
    void testUpdateEntry_NotFound() throws Exception {
        WalletRequestDTO requestDTO = WalletRequestDTO.builder()
                .coin("ETH")
                .units(10.0)
                .buyPrice(2500.0)
                .build();

        when(walletService.updateEntry(eq(1L), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/wallet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEntry_Success() throws Exception {
        doNothing().when(walletService).deleteEntry(1L);

        mockMvc.perform(delete("/api/wallet/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Entry deleted successfully"));
    }
}
