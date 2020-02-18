package com.laboschcst.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class ApiSupplierExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ApiSupplierExecutor.class);

    public <T> ResponseEntity<T> executeAndGet(Supplier<T> supplier) {
        try {
            return new ResponseEntity<>(supplier.get(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception caught while executing api request!", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
