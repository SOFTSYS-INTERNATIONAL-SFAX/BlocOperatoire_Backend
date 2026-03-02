package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.user.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.UserMapper;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /* =====================================================
       CREATE USER
       ===================================================== */

    public UserResponseDTO create(UserCreateRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        User user = userMapper.toEntity(dto);
        user = userRepository.save(user);

        return userMapper.toDTO(user);
    }

    /* =====================================================
       GET BY ID
       ===================================================== */

    @Transactional(readOnly = true)
    public UserResponseDTO getById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + id));

        return userMapper.toDTO(user);
    }

    /* =====================================================
       GET ALL
       ===================================================== */

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    public UserResponseDTO update(UUID id, UserUpdateRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + id));

        userMapper.updateEntity(user, dto);

        user = userRepository.save(user);

        return userMapper.toDTO(user);
    }

    /* =====================================================
       SOFT DELETE
       ===================================================== */

    public void deactivate(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + id));

        user.setEnabled(false);

        userRepository.save(user);
    }

    /* =====================================================
       HARD DELETE
       ===================================================== */

    public void delete(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }

        userRepository.deleteById(id);
    }
}
