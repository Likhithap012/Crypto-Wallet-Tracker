package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.request.WalletRequestDTO;
import com.gevernova.crypto_wallet_tracker.dto.response.WalletResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.User;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.mapper.WalletEntryMapper;
import com.gevernova.crypto_wallet_tracker.repository.UserRepository;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    private WalletRepository walletRepository;
    private WalletEntryMapper mapper;
    private UserRepository userRepository;
    private WalletService walletService;

    private final String email = "test@example.com";
    private final Long userId = 1L;
    private User mockUser;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    public void setUp() {
        walletRepository = mock(WalletRepository.class);
        mapper = mock(WalletEntryMapper.class);
        userRepository = mock(UserRepository.class);
        walletService = new WalletService(walletRepository, mapper, userRepository);

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail(email);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
    }

    @AfterEach
    public void tearDown() {
        if (securityContextHolderMock != null) {
            securityContextHolderMock.close();
        }
    }

    @Test
    public void testAddEntry_Positive() {
        WalletRequestDTO dto = new WalletRequestDTO("bitcoin", 2.0, 50000.0);
        WalletEntry entry = new WalletEntry();
        WalletEntry savedEntry = new WalletEntry();
        WalletResponseDTO responseDTO = new WalletResponseDTO(1L, "bitcoin", 2.0, 50000.0);

        when(mapper.toEntity(dto)).thenReturn(entry);
        when(walletRepository.save(entry)).thenReturn(savedEntry);
        when(mapper.toResponseDTO(savedEntry)).thenReturn(responseDTO);

        WalletResponseDTO result = walletService.addEntry(dto);

        assertNotNull(result);
        assertEquals("bitcoin", result.getCoin());
    }

    @Test
    public void testGetAllEntries_Positive() {
        WalletEntry entry = new WalletEntry();
        WalletResponseDTO responseDTO = new WalletResponseDTO(1L, "bitcoin", 2.0, 50000.0);

        when(walletRepository.findByUser(mockUser)).thenReturn(List.of(entry));
        when(mapper.toResponseDTO(entry)).thenReturn(responseDTO);

        List<WalletResponseDTO> result = walletService.getAllEntries();

        assertEquals(1, result.size());
        assertEquals("bitcoin", result.get(0).getCoin());
    }

    @Test
    public void testUpdateEntry_Positive() {
        WalletRequestDTO updatedDTO = new WalletRequestDTO("ethereum", 5.0, 30000.0);
        WalletEntry existing = new WalletEntry();
        existing.setUser(mockUser);

        WalletEntry saved = new WalletEntry();
        WalletResponseDTO responseDTO = new WalletResponseDTO(1L, "ethereum", 5.0, 30000.0);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(walletRepository.save(existing)).thenReturn(saved);
        when(mapper.toResponseDTO(saved)).thenReturn(responseDTO);

        Optional<WalletResponseDTO> result = walletService.updateEntry(1L, updatedDTO);

        assertTrue(result.isPresent());
        assertEquals("ethereum", result.get().getCoin());
    }

    @Test
    public void testUpdateEntry_UnauthorizedUser() {
        WalletRequestDTO updatedDTO = new WalletRequestDTO("ethereum", 5.0, 30000.0);
        WalletEntry entry = new WalletEntry();
        User anotherUser = new User();
        anotherUser.setId(2L);
        entry.setUser(anotherUser);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(entry));

        Optional<WalletResponseDTO> result = walletService.updateEntry(1L, updatedDTO);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteEntry_Positive() {
        WalletEntry entry = new WalletEntry();
        entry.setUser(mockUser);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(entry));

        assertDoesNotThrow(() -> walletService.deleteEntry(1L));
        verify(walletRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteEntry_Unauthorized() {
        WalletEntry entry = new WalletEntry();
        User anotherUser = new User();
        anotherUser.setId(2L);
        entry.setUser(anotherUser);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(entry));

        Exception exception = assertThrows(RuntimeException.class, () -> walletService.deleteEntry(1L));
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    public void testDeleteEntry_NotFound() {
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> walletService.deleteEntry(1L));
        assertEquals("Entry not found", exception.getMessage());
    }
}
