package ru.otus.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class AclServiceWrapperServiceImpl implements AclServiceWrapperService {

    private final MutableAclService mutableAclService;

    @Override
    public void createPermission(Object object, Set<Permission> permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid owner = new PrincipalSid(authentication);
        ObjectIdentity oid = new ObjectIdentityImpl(object);

        final Sid reader = new GrantedAuthoritySid("ROLE_READER");

        MutableAcl acl = mutableAclService.createAcl(oid);
        for (Permission permission : permissions) {
            acl.insertAce(acl.getEntries().size(), permission, owner, true);
        }
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, reader, true);

        mutableAclService.updateAcl(acl);
    }
}
