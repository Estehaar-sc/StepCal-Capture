package com.UniProject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StepCalCaptureApplicationTests {

    @Test
    void applicationClassHasSpringBootAnnotation() {
        assertNotNull(
                StepCalCaptureApplication.class.getAnnotation(
                        SpringBootApplication.class
                )
        );
    }
}