package ru.otus.hw.security;

import org.springframework.security.acls.model.Permission;

import java.util.Set;

public interface AclServiceWrapperService {
    void createPermission(Object object, Set<Permission> permission);
}
