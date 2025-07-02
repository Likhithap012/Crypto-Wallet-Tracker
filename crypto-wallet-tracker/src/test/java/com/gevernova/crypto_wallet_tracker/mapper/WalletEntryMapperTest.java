package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletEntryMapperTest {

    private WalletEntryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WalletEntryMapper();
    }

    // Positive Test: toEntity
    @Test
    void testToEntity_ValidDTO() {
        WalletRequestDTO dto = WalletRequestDTO.builder()
                .coin("BTC")
                .units(2.5)
                .buyPrice(30000.0)
                .build();

        WalletEntry entity = mapper.toEntity(dto);

        assertEquals("BTC", entity.getCoin());
        assertEquals(2.5, entity.getUnits());
        assertEquals(30000.0, entity.getBuyPrice());
        assertNull(entity.getId());
    }

    // Positive Test: toResponseDTO
    @Test
    void testToResponseDTO_ValidEntity() {
        WalletEntry entity = WalletEntry.builder()
                .id(1L)
                .coin("ETH")
                .units(1.0)
                .buyPrice(2000.0)
                .build();

        WalletResponseDTO dto = mapper.toResponseDTO(entity);

        assertEquals(1L, dto.getId());
        assertEquals("ETH", dto.getCoin());
        assertEquals(1.0, dto.getUnits());
        assertEquals(2000.0, dto.getBuyPrice());
    }

    // Negative Test: toEntity with null DTO
    @Test
    void testToEntity_NullDTO() {
        Exception ex = assertThrows(NullPointerException.class, () -> {
            mapper.toEntity(null);
        });

        assertTrue(ex.getMessage().contains("dto"));
    }

    // Negative Test: toResponseDTO with null entity
    @Test
    void testToResponseDTO_NullEntity() {
        Exception ex = assertThrows(NullPointerException.class, () -> {
            mapper.toResponseDTO(null);
        });

        assertTrue(ex.getMessage().contains("entity"));
    }

    // Negative Test: toEntity with null fields in DTO
    @Test
    void testToEntity_NullFields() {
        WalletRequestDTO dto = WalletRequestDTO.builder()
                .coin(null)
                .units(null)
                .buyPrice(null)
                .build();

        WalletEntry entity = mapper.toEntity(dto);

        assertNull(entity.getCoin());
        assertNull(entity.getUnits());
        assertNull(entity.getBuyPrice());
    }
    

    // Edge Case: Empty coin string in DTO
    @Test
    void testToEntity_EmptyCoin() {
        WalletRequestDTO dto = WalletRequestDTO.builder()
                .coin("")
                .units(5.0)
                .buyPrice(1000.0)
                .build();

        WalletEntry entity = mapper.toEntity(dto);

        assertEquals("", entity.getCoin());
        assertEquals(5.0, entity.getUnits());
        assertEquals(1000.0, entity.getBuyPrice());
    }

    // Edge Case: Zero units and buyPrice
    @Test
    void testToEntity_ZeroValues() {
        WalletRequestDTO dto = WalletRequestDTO.builder()
                .coin("DOGE")
                .units(0.0)
                .buyPrice(0.0)
                .build();

        WalletEntry entity = mapper.toEntity(dto);

        assertEquals("DOGE", entity.getCoin());
        assertEquals(0.0, entity.getUnits());
        assertEquals(0.0, entity.getBuyPrice());
    }

    // Edge Case: Large numeric values
    @Test
    void testToEntity_LargeValues() {
        WalletRequestDTO dto = WalletRequestDTO.builder()
                .coin("ADA")
                .units(Double.MAX_VALUE)
                .buyPrice(Double.MAX_VALUE)
                .build();

        WalletEntry entity = mapper.toEntity(dto);

        assertEquals("ADA", entity.getCoin());
        assertEquals(Double.MAX_VALUE, entity.getUnits());
        assertEquals(Double.MAX_VALUE, entity.getBuyPrice());
    }
}
