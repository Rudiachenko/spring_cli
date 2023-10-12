package com.learning.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class ApplicationTests {
    @Autowired
    private CommandLineRunner commandLineRunner;

    @Test
    public void testCommandLineRunner(CapturedOutput output) throws Exception {
        commandLineRunner.run();

        String expectedOutput = "Hello World";
        String actualOutput = output.getOut().trim();

        assertTrue(actualOutput.contains(expectedOutput));
    }
}
