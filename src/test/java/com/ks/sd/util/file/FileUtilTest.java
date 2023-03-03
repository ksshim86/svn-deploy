package com.ks.sd.util.file;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileUtilTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    
    @Test
    void mkdirTest() {
        String path = "C:\\deploy\\TEST";
        String dirName = "TEST_KEY";
        
        boolean isResult = SdFileUtil.mkdirs(Paths.get(path, dirName).toString());

        LOGGER.debug("isResult: " + isResult);
    }
}
