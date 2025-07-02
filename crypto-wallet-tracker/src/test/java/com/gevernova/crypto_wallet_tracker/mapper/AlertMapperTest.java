package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.request.AlertRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.AlertResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.PriceAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertMapperTest {

    private AlertMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AlertMapper();
    }

    // Positive test for toEntity method
    @Test
    void testToEntityWithValidInput() {
        AlertRequestDTO dto = AlertRequestDTO.builder()
                .coin("BTC")
                .targetPrice(30000.0)
                .condition("GREATER_THAN")
                .build();

        String email = "user@example.com";

        PriceAlert entity = mapper.toEntity(dto, email);

        assertEquals("BTC", entity.getCoin());
        assertEquals(30000.0, entity.getTargetPrice());
        assertEquals("GREATER_THAN", entity.getCondition());
        assertEquals("user@example.com", entity.getEmail());
        assertFalse(entity.isTriggered());
    }

    // Negative test for toEntity when dto is null
    @Test
    void testToEntityWithNullDTO() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            mapper.toEntity(null, "user@example.com");
        });

        assertTrue(exception.getMessage().contains("dto"));
    }

    // Negative test for toEntity when email is null
    @Test
    void testToEntityWithNullEmail() {
        AlertRequestDTO dto = AlertRequestDTO.builder()
                .coin("BTC")
                .targetPrice(30000.0)
                .condition("GREATER_THAN")
                .build();

        PriceAlert entity = mapper.toEntity(dto, null);

        assertNull(entity.getEmail());
        assertEquals("BTC", entity.getCoin());
        assertEquals(30000.0, entity.getTargetPrice());
    }

    // Positive test for toDTO method
    @Test
    void testToDTOWithValidInput() {
        PriceAlert alert = PriceAlert.builder()
                .id(1L)
                .coin("ETH")
                .targetPrice(2500.0)
                .condition("LESS_THAN")
                .triggered(true)
                .build();

        AlertResponseDTO dto = mapper.toDTO(alert);

        assertEquals(1L, dto.getId());
        assertEquals("ETH", dto.getCoin());
        assertEquals(2500.0, dto.getTargetPrice());
        assertEquals("LESS_THAN", dto.getCondition());
        assertTrue(dto.isTriggered());
    }

    // Negative test for toDTO when alert is null
    @Test
    void testToDTOWithNullEntity() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            mapper.toDTO(null);
        });

        assertTrue(exception.getMessage().contains("alert"));
    }
}
