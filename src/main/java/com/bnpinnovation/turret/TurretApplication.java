package com.bnpinnovation.turret;

import com.bnpinnovation.turret.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TurretApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(TurretApplication.class, args);
    }

    @Autowired
    private AccountService accountService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        ne
//        accountService.newAccount()
    }
}
