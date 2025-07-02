package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SummaryMapperTest {

    private SummaryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SummaryMapper();
    }

    // Positive Test: Normal values
    @Test
    void testToDTOWithProfit() {
        double investment = 1000.0;
        double current = 1200.0;

        SummaryResponseDTO dto = mapper.toDTO(investment, current);

        assertEquals(1000.0, dto.getTotalInvestment());
        assertEquals(1200.0, dto.getCurrentValue());
        assertEquals(200.0, dto.getNetGainLoss());
        assertEquals(20.0, dto.getNetGainLossPercent());
    }

    // Positive Test: Loss scenario
    @Test
    void testToDTOWithLoss() {
        double investment = 1500.0;
        double current = 1200.0;

        SummaryResponseDTO dto = mapper.toDTO(investment, current);

        assertEquals(1500.0, dto.getTotalInvestment());
        assertEquals(1200.0, dto.getCurrentValue());
        assertEquals(-300.0, dto.getNetGainLoss());
        assertEquals(-20.0, dto.getNetGainLossPercent());
    }

    // Positive Test: Break-even scenario
    @Test
    void testToDTOWithNoGainOrLoss() {
        double investment = 1000.0;
        double current = 1000.0;

        SummaryResponseDTO dto = mapper.toDTO(investment, current);

        assertEquals(1000.0, dto.getTotalInvestment());
        assertEquals(1000.0, dto.getCurrentValue());
        assertEquals(0.0, dto.getNetGainLoss());
        assertEquals(0.0, dto.getNetGainLossPercent());
    }

    // Edge Case: Zero investment, positive current value
    @Test
    void testToDTOWithZeroInvestment() {
        double investment = 0.0;
        double current = 500.0;

        SummaryResponseDTO dto = mapper.toDTO(investment, current);

        assertEquals(0.0, dto.getTotalInvestment());
        assertEquals(500.0, dto.getCurrentValue());
        assertEquals(500.0, dto.getNetGainLoss());
        assertEquals(0.0, dto.getNetGainLossPercent()); // Prevent divide by zero
    }

    // Edge Case: Zero investment and current value
    @Test
    void testToDTOWithZeroInvestmentAndValue() {
        double investment = 0.0;
        double current = 0.0;

        SummaryResponseDTO dto = mapper.toDTO(investment, current);

        assertEquals(0.0, dto.getTotalInvestment());
        assertEquals(0.0, dto.getCurrentValue());
        assertEquals(0.0, dto.getNetGainLoss());
        assertEquals(0.0, dto.getNetGainLossPercent());
    }

    // Edge Case: Negative investment value (invalid input)
    @Test
    void testToDTOWithNegativeInvestment() {
        double investment = -1000.0;
        double current = 500.0;

        SummaryResponseDTO dto = mapper.toDTO(investment, current);

        assertEquals(-1000.0, dto.getTotalInvestment());
        assertEquals(500.0, dto.getCurrentValue());
        assertEquals(1500.0, dto.getNetGainLoss());
        assertEquals(-150.0, dto.getNetGainLossPercent()); // Unusual but mathematically correct
    }

    // Edge Case: Negative current value (invalid input)
    @Test
    void testToDTOWithNegativeCurrentValue() {
        double investment = 1000.0;
        double current = -500.0;

        SummaryResponseDTO dto = mapper.toDTO(investment, current);

        assertEquals(1000.0, dto.getTotalInvestment());
        assertEquals(-500.0, dto.getCurrentValue());
        assertEquals(-1500.0, dto.getNetGainLoss());
        assertEquals(-150.0, dto.getNetGainLossPercent());
    }


}
