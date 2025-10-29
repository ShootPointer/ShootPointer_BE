package com.midas.shootpointer;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class BaseSpringBootTest {

    @Autowired
    private HikariDataSource dataSource;

    @AfterAll
    static void tearDown(@Autowired HikariDataSource dataSource){
        if (dataSource!=null && !dataSource.isClosed()){
            dataSource.close();
        }
    }
}
