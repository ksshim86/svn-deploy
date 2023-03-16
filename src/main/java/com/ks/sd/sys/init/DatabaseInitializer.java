package com.ks.sd.sys.init;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ks.sd.api.role.entity.Role;
import com.ks.sd.api.role.repository.RoleRepository;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() > 0) {
            return;
        }

        LOGGER.debug("Role data initialization start");
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().roleLvl(1).roleCd("ROLE_LVL1").roleNm("개발자").build());
        roles.add(Role.builder().roleLvl(2).roleCd("ROLE_LVL2").roleNm("PL").build());
        roles.add(Role.builder().roleLvl(3).roleCd("ROLE_LVL3").roleNm("PM").build());
        roles.add(Role.builder().roleLvl(4).roleCd("ROLE_ADMIN").roleNm("관리자").build());
        roleRepository.saveAll(roles);
    }
}
