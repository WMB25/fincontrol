package com.fincontrol.infrastructure.persistence.user;

import com.fincontrol.domain.entity.User;
import com.fincontrol.domain.repository.IUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryImpl implements IUserRepository {
    private final SpringDataUserRepository springDataUserRepository;

    public UserRepositoryImpl(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public User save(User savedUser) {
        UserEntity userEntity = UserEntity.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .password(savedUser.getPassword())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
        UserEntity savedEntity = springDataUserRepository.save(userEntity);

        return new User (
            savedEntity.getId(),
            savedEntity.getName(),
            savedEntity.getEmail(),
            savedEntity.getPassword(),
            savedEntity.getCreatedAt(),
            savedEntity.getUpdatedAt()
        );
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
                .map(savedEntity -> new User (
                        savedEntity.getId(),
                        savedEntity.getName(),
                        savedEntity.getEmail(),
                        savedEntity.getPassword(),
                        savedEntity.getCreatedAt(),
                        savedEntity.getUpdatedAt()
                ));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
                .map(savedEntity -> new User (
                        savedEntity.getId(),
                        savedEntity.getName(),
                        savedEntity.getEmail(),
                        savedEntity.getPassword(),
                        savedEntity.getCreatedAt(),
                        savedEntity.getUpdatedAt()
                ));
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }
}