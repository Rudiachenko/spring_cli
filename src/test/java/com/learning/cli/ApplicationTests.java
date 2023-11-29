package com.learning.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class ApplicationTests {
    @InjectMocks
    private Application application;

    @Test
    public void testCommandLineRunner(CapturedOutput output) {
        application.run();

        String expectedOutput = "Hello World";
        String actualOutput = output.getOut();

        assertTrue(actualOutput.contains(expectedOutput));
    }
}
