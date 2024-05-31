package me.dev.onlinestoreapi.service.impl;

import lombok.RequiredArgsConstructor;
import me.dev.onlinestoreapi.model.Role;
import me.dev.onlinestoreapi.repository.RoleRepository;
import me.dev.onlinestoreapi.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
