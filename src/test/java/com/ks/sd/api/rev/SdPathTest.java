package com.ks.sd.api.rev;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;
import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.api.rev.entity.SdRevision;
import com.ks.sd.api.rev.entity.SdRevision.SdRevisionId;
import com.ks.sd.api.rev.repository.SdPathRepository;
import com.ks.sd.api.rev.repository.SdRevisionRepository;

@SpringBootTest
public class SdPathTest {
    @Autowired
    private SdRevisionRepository sdRevisionRepository;

    @Autowired
    private SdPathRepository sdPathRepository;

    @Test
    public void updateCombinedKeyHash() {
        String combinedKey = String.format("%d|%s|%s", 1, "/src/java/test/", "foo.java");
        String combined = sha256(combinedKey);

        System.out.println(combined);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }

    @Test
    public void revisionInsertTest() {
        // Project project = Project.builder().pjtNo(1).build();
        // SdRevision sdRevision = 
        //     SdRevision.builder()
        //         .project(project)
        //         .id(SdRevisionId.builder().revNo(1).pjtNo(1).build())
        //         .author("foo")
        //         .revDt(LocalDateTime.now())
        //         .msg("bar2")
        //         .build();

        // SdRevision saveSdRevision = sdRevisionRepository.save(sdRevision);

        // SdPath sdPath = 
        //     SdPath.builder()
        //         .pjtNo(saveSdRevision.getId().getPjtNo())
        //         .revNo(saveSdRevision.getId().getRevNo())
        //         .sdRevision(saveSdRevision)
        //         .ordr(1)
        //         .action("A")
        //         .kind("F")
        //         .filePath("/src/java/test/")
        //         .fileNm("foo.java")
        //         .subPjtNo(1)
        //         .build();
        
        // sdPathRepository.save(sdPath);
    }
}
