package com.smartclinic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.config.JwtUtil;
import com.smartclinic.models.mysql.User;
import com.smartclinic.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phone("+905551234567")
                .role(User.Role.PATIENT)
                .build();

        testAdmin = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@clinic.com")
                .password("admin123")
                .firstName("System")
                .lastName("Admin")
                .role(User.Role.ADMIN)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AsAdmin_ShouldReturnUsers() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(testUser, testAdmin);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "firstName")
                .param("direction", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[0].username", is("testuser")))
                .andExpect(jsonPath("$.data.content[1].username", is("admin")));

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_AsNonAdmin_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("User retrieved successfully")))
                .andExpect(jsonPath("$.data.username", is("testuser")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WhenUserNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithValidData_ShouldCreateUser() throws Exception {
        // Arrange
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("User created successfully")))
                .andExpect(jsonPath("$.data.username", is("testuser")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        User invalidUser = new User(); // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_WithValidData_ShouldUpdateUser() throws Exception {
        // Arrange
        User updatedUser = User.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("User updated successfully")))
                .andExpect(jsonPath("$.data.firstName", is("Updated")));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WhenUserExists_ShouldDeleteUser() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("User deleted successfully")));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersByRole_ShouldReturnFilteredUsers() throws Exception {
        // Arrange
        List<User> doctors = Arrays.asList(testUser);
        when(userService.getUsersByRole(User.Role.DOCTOR)).thenReturn(doctors);

        // Act & Assert
        mockMvc.perform(get("/api/users/role/DOCTOR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(userService, times(1)).getUsersByRole(User.Role.DOCTOR);
    }

    @Test
    void getAllUsers_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
