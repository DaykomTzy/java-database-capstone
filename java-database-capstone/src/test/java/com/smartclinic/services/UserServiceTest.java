package com.smartclinic.services;

import com.smartclinic.exceptions.ResourceNotFoundException;
import com.smartclinic.models.mysql.User;
import com.smartclinic.repositories.mysql.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testAdmin;
    private User testDoctor;
    private User testPatient;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phone("+905551234567")
                .role(User.Role.PATIENT)
                .build();

        testAdmin = User.builder()
                .username("admin")
                .email("admin@clinic.com")
                .password("admin123")
                .firstName("System")
                .lastName("Admin")
                .role(User.Role.ADMIN)
                .build();

        testDoctor = User.builder()
                .username("doctor")
                .email("doctor@clinic.com")
                .password("doctor123")
                .firstName("John")
                .lastName("Doctor")
                .role(User.Role.DOCTOR)
                .build();

        testPatient = User.builder()
                .username("patient")
                .email("patient@example.com")
                .password("patient123")
                .firstName("Jane")
                .lastName("Patient")
                .role(User.Role.PATIENT)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testAdmin);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<User> result = userService.getAllUsers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        Long userId = 1L;
        testUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnEmpty() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).existsByUsername(testUser.getUsername());
        verify(userRepository, times(1)).existsByEmail(testUser.getEmail());
        verify(passwordEncoder, times(1)).encode(testUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(testUser);
        });

        assertEquals("Username already exists: " + testUser.getUsername(), exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(testUser.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // Arrange
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .username("olduser")
                .email("old@example.com")
                .password("oldpassword")
                .firstName("Old")
                .lastName("User")
                .phone("+905559876543")
                .role(User.Role.PATIENT)
                .build();

        User updateData = User.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .phone("+905551112233")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(userId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("+905551112233", result.getPhone());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, testUser);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        Long userId = 1L;
        testUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void getUsersByRole_ShouldReturnFilteredUsers() {
        // Arrange
        User.Role role = User.Role.DOCTOR;
        List<User> doctors = Arrays.asList(testDoctor);
        when(userRepository.findByRole(role)).thenReturn(doctors);

        // Act
        List<User> result = userService.getUsersByRole(role);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(role, result.get(0).getRole());
        verify(userRepository, times(1)).findByRole(role);
    }

    @Test
    void searchUsersByName_ShouldReturnMatchingUsers() {
        // Arrange
        String searchName = "test";
        List<User> matchingUsers = Arrays.asList(testUser);
        when(userRepository.findByNameContaining(searchName)).thenReturn(matchingUsers);

        // Act
        List<User> result = userService.searchUsersByName(searchName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByNameContaining(searchName);
    }

    @Test
    void userExists_WhenUserExists_ShouldReturnTrue() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        boolean result = userService.userExists(userId);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void getDoctorsBySpecialization_ShouldReturnDoctors() {
        // Arrange
        String specialization = "Cardiology";
        List<User> doctors = Arrays.asList(testDoctor);
        when(userRepository.findDoctorsBySpecialization(specialization)).thenReturn(doctors);

        // Act
        List<User> result = userService.getDoctorsBySpecialization(specialization);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findDoctorsBySpecialization(specialization);
    }
}
