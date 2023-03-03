package com.ks.sd.util.svn;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.tigris.subversion.javahl.SVNClient;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

@SpringBootTest
public class SvnRepositoryUtilTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private String SVN_URL = "";
    private String SVN_USERNAME = "";
    private String SVN_PASSWORD = "";
    private String CHECKOUT_PATH = "";

    @BeforeEach
    void setup() {
        SVN_URL = "svn://192.168.0.186/prj-siis-sisReport-deploy";
        SVN_USERNAME = "svndeploy";
        SVN_PASSWORD = "svndeploy1!";
        CHECKOUT_PATH = "C:\\deploy\\TEST";
    }

    // 저장소 연결 확인 테스트
    boolean isConnectTest(String svnUrl, String svnUsername, String svnPassword) {
        return SvnRepositoryUtil.isConnect(svnUrl, svnUsername, svnPassword);
    }

    // 저장소 체크아웃 테스트
    boolean checkoutTest(String svnUrl, String svnUsername, String svnPassword, String checkoutPath) {
        return SvnRepositoryUtil.checkout(svnUrl, svnUsername, svnPassword, checkoutPath);
    }

    // 프로젝트 생성중 저장소 연결
    @Test
    void integrationTest() {
        boolean isConnectRst = this.isConnectTest(SVN_URL, SVN_USERNAME, SVN_PASSWORD);
        // LOGGER.debug("isConnectRst: " + isConnectRst);
        assertTrue(isConnectRst);
        boolean isChekcoutRst = this.checkoutTest(SVN_URL, SVN_USERNAME, SVN_PASSWORD, CHECKOUT_PATH);
        assertTrue(isChekcoutRst);
    }

    @Test
    void getHeadRevision() throws SVNException {
        String url="svn://192.168.0.186/prj-siis2";
        String name="svndeploy";
        String password="svndeploy1!";
        SVNRepository repository = null;
        
        System.out.println(SvnRepositoryUtil.getLatestRevision(url, name, password));
    }

    @Test
    void fileMoveTest() throws SVNException {
        SVNURL url = SVNURL.parseURIEncoded("svn://192.168.0.186/prj-siis");
        SVNRepository repository = SVNRepositoryFactory.create(url);
        ISVNAuthenticationManager authManager = 
                            SVNWCUtil.createDefaultAuthenticationManager(SVN_USERNAME, SVN_PASSWORD.toCharArray());
        repository.setAuthenticationManager(authManager);
        
        repository.log(new String[]{""}, 549, 549, true, false,  new ISVNLogEntryHandler() {
            @Override
            public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                for (Map.Entry<String, SVNLogEntryPath> changedPath : logEntry.getChangedPaths().entrySet()) {
                    SVNLogEntryPath logEntryPath = changedPath.getValue();
                    if (logEntryPath.getCopyPath() != null) {
                        System.out.println(logEntry.getRevision() + " > " + logEntryPath.getCopyRevision() + "File " + logEntryPath.getPath() + " has been moved to " + logEntryPath.getCopyPath());
                    }
                }
            }
        });
    }
}
