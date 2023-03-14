package com.ks.sd.util.svn;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.tigris.subversion.javahl.SVNClient;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.ISVNAdminEventHandler;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;
import org.tmatesoft.svn.core.wc2.SvnMerge;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import com.ks.sd.util.file.SdFileUtil;

@SpringBootTest
public class SvnRepositoryUtilTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private String SVN_URL = "";
    private String SVN_USERNAME = "";
    private String SVN_PASSWORD = "";
    private String CHECKOUT_PATH = "";

    @BeforeEach
    void setup() {
        SVN_URL = "svn://192.168.0.186/sd-test-repo/branches/stg-branch";
        SVN_USERNAME = "svndeploy";
        SVN_PASSWORD = "svndeploy1!";
        CHECKOUT_PATH = "C:\\deploy\\ASDF";
    }

    // 저장소 연결 확인 테스트
    boolean isConnectTest() {
        return SvnRepositoryUtil.isConnect(SVN_URL, SVN_USERNAME, SVN_PASSWORD);
    }

    // 저장소 체크아웃 테스트
    boolean checkoutTest() {
        return SvnRepositoryUtil.checkout(SVN_URL, SVN_USERNAME, SVN_PASSWORD, CHECKOUT_PATH);
    }

    // 프로젝트 생성중 저장소 연결
    @Test
    void integrationTest() {
        boolean isConnectRst = this.isConnectTest();
        assertTrue(isConnectRst);
        boolean isChekcoutRst = this.checkoutTest();
        assertTrue(isChekcoutRst);
        System.out.println(SvnRepositoryUtil.getLatestRevision(SVN_URL, SVN_USERNAME, SVN_PASSWORD) );
        SvnRepositoryUtil.update(SVN_URL, SVN_USERNAME, SVN_PASSWORD, CHECKOUT_PATH, -1);
    }

    @Test
    void fileMoveCheckTest() throws SVNException {
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

    @Test
    void createTrunkTest() throws SVNException {
        String svnUrl = "svn://192.168.0.186/sd-test-repo";
        String username = "svndeploy";
        String password = "svndeploy1!";
        String orgBranchNm = "/";
        String dstBranchNm = "/trunk";
        String branchMessage = "Creating trunk branch";
        boolean isResult = SvnRepositoryUtil.createBranch(svnUrl, username, password, orgBranchNm, dstBranchNm, branchMessage);
        System.out.println(isResult);
    }

    @Test
    void createStagingTest() throws SVNException {
        String svnUrl = "svn://192.168.0.186/sd-test-repo";
        String username = "svndeploy";
        String password = "svndeploy1!";
        String orgBranchNm = "/master";
        String dstBranchNm = "/branches/staging";
        String branchMessage = "Creating staging branch";
        boolean isResult = SvnRepositoryUtil.createBranch(svnUrl, username, password, orgBranchNm, dstBranchNm, branchMessage);
        System.out.println(isResult);
    }

    @Test
    void createIssueBranchTest() throws SVNException {
        String svnUrl = "svn://192.168.0.186/sd-test-repo";
        String username = "svndeploy";
        String password = "svndeploy1!";
        String orgBranchNm = "/trunk";
        String dstBranchNm = "/issues";
        String branchMessage = "Creating issues branch";
        boolean isResult = SvnRepositoryUtil.createBranch(svnUrl, username, password, orgBranchNm, dstBranchNm, branchMessage);
        System.out.println(isResult);
    }

    @Test
    void createMasterTest() throws SVNException {
        String svnUrl = "svn://192.168.0.186/sd-test-repo";
        String username = "svndeploy";
        String password = "svndeploy1!";
        String orgBranchNm = "/trunk";
        String dstBranchNm = "/branches/master";
        String branchMessage = "Creating master branch";
        boolean isResult = SvnRepositoryUtil.createBranch(svnUrl, username, password, orgBranchNm, dstBranchNm, branchMessage);
        System.out.println(isResult);
    }

    @Test
    void createStgDeployBranchTest() throws SVNException {
        String sdRootPath = "C:\\deploy\\";
        String issueRootDir = "issues\\";
        String pjtKey = "TEST_REPO";
        String dpDt = "20230310";
        String issue = pjtKey + "-" + dpDt + "-2";

        // SdFileUtil.mkdirs(sdRootPath + pjtKey + File.separator + issueRootDir + issue);

        String svnUrl = "svn://192.168.0.186/sd-test-repo";
        String username = "svndeploy";
        String password = "svndeploy1!";
        String orgBranchNm = "/branches/master";
        String dstBranchNm = "/deploy/" + issue;
        String branchMessage = "Creating " + issue + " branch";
        boolean isResult = SvnRepositoryUtil.createBranch(svnUrl, username, password, orgBranchNm, dstBranchNm, branchMessage);
        System.out.println(isResult);
    }

    @Test
    void exportTest() throws SVNException {
        SVNURL svnUrl = SVNURL.parseURIEncoded("svn://192.168.0.186/prj-siis/prj-siis-sisUI/guide/sample/sampleComFile.xfdl");
        String svnUsername = "svndeploy";
        String svnPassword = "svndeploy1!";
        String workingCopyDir = "C:\\deploy\\TEST_REPO\\deploy\\TEST_REPO-20230310-2";
        SVNRevision revision = SVNRevision.create(374); // 610, 374
        String sourceFullPath = "prj-siis-sisUI\\guide\\sample\\sampleComFile.xfdl";

        // SdFileUtil.mkdirs(workingCopyDir + File.separator + sourceFullPath);

        SVNClientManager clientManager = SVNClientManager.newInstance();
        ISVNAuthenticationManager authManager = 
            SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
        clientManager.setAuthenticationManager(authManager);
        
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        long doExport = updateClient.doExport(
            svnUrl,
            Paths.get(workingCopyDir + File.separator + sourceFullPath).toFile(),
            revision,
            revision,
            SVNProperty.EOL_STYLE_NATIVE, 
            true, 
            SVNDepth.INFINITY
        ); 

        System.out.println("doExport: " + doExport);
    }

    @Test
    void createBranchTest() throws SVNException {
        String svnUrl = "svn://192.168.0.186/sd-test-repo";
        String username = "svndeploy";
        String password = "svndeploy1!";
        String orgBranchNm = "/branches/staging";
        String dstBranchNm = "/branches/sd-6-20230307";
        String branchMessage = "Creating sd-6-20230307 branch";
        boolean isResult = SvnRepositoryUtil.createBranch(svnUrl, username, password, orgBranchNm, dstBranchNm, branchMessage);
        System.out.println(isResult);
    }

    @Test
    void mergeBranchTest() throws SVNException {
        String svnUsername = "svndeploy";
        String svnPassword = "svndeploy1!";
        SVNURL svnUrl1 = SVNURL.parseURIEncoded("svn://192.168.0.186/sd-test-repo/issues/TEST_REPO-20230310-1");
        SVNURL dstUrl = SVNURL.parseURIEncoded("svn://192.168.0.186/sd-test-repo/branches/staging");
        File workingCopyDir = new File("C:\\deploy\\TEST_REPO\\branches\\staging");

        SVNClientManager clientManager = SVNClientManager.newInstance();
        ISVNAuthenticationManager authManager = 
            SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
        clientManager.setAuthenticationManager(authManager);
 
        SVNDiffClient diffClient = clientManager.getDiffClient();

        Collection<SVNRevisionRange> ranges = new ArrayList<SVNRevisionRange>();
        ranges.add(new SVNRevisionRange(SVNRevision.create(143), SVNRevision.create(144)));
    
        // url1, pegRevision, rangesToMerge, dstPath, depth, useAncestry, force, dryRun, recordOnly
        diffClient.doMerge(
            svnUrl1,
            SVNRevision.HEAD,
            ranges,
            workingCopyDir,
            SVNDepth.INFINITY,
            true,
            false,
            false,
            false
        );
    }

    @Test
    void mergeMasterTest() throws SVNException {
        String svnUsername = "svndeploy";
        String svnPassword = "svndeploy1!";
        String svnUrl = "svn://192.168.0.186/sd-test-repo/branches/staging";
        SVNURL svnUrl1 = SVNURL.parseURIEncoded(svnUrl);
        String workingPath = "C:\\deploy\\TEST_REPO\\branches\\master";
        File workingCopyDir = new File(workingPath);

        SVNClientManager clientManager = SVNClientManager.newInstance();
        ISVNAuthenticationManager authManager = 
            SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
        clientManager.setAuthenticationManager(authManager);

        SVNDiffClient diffClient = clientManager.getDiffClient();

        Collection<SVNRevisionRange> ranges = new ArrayList<SVNRevisionRange>();
        ranges.add(new SVNRevisionRange(SVNRevision.create(90), SVNRevision.create(106)));
        
        // url1, pegRevision, rangesToMerge, dstPath, depth, useAncestry, force, dryRun, recordOnly
        diffClient.doMerge(svnUrl1, SVNRevision.HEAD, ranges, workingCopyDir, SVNDepth.INFINITY, false, false, false, false);
        SvnRepositoryUtil.update(svnUrl, svnUsername, svnPassword, workingPath, -1);
    }

    @Test
    void rollbackTest() {
        String svnUrl = "svn://192.168.0.186/sd-test-repo/branches/staging";
        String username = "svndeploy";
        String password = "svndeploy1!";
        String branchName = "staging";
        String fileName = "A.txt";
        long oldRevision = 115; // 이전 revision
        File workingCopyDir = new File("C:\\deploy\\TEST_REPO\\branches\\staging\\A.txt");

        SVNClientManager clientManager = SVNClientManager.newInstance();
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
        clientManager.setAuthenticationManager(authManager);

        try {
            SVNURL url = SVNURL.parseURIEncoded(svnUrl + "/" + fileName);

            SVNLogClient logClient = clientManager.getLogClient();
            logClient.doLog(
                url,
                null,
                SVNRevision.HEAD,
                SVNRevision.create(1),
                SVNRevision.HEAD, 
                false,
                false,
                0,
                new ISVNLogEntryHandler() {
                    @Override
                    public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                        System.out.println(logEntry.getRevision() + " > " + logEntry.getMessage());
                    }
                });
                System.out.println("=====================================");
    
            SVNUpdateClient updateClient = clientManager.getUpdateClient();
            long doExport = updateClient.doExport(
                url,
                workingCopyDir,
                SVNRevision.create(oldRevision), 
                SVNRevision.create(oldRevision), 
                "CRLF", 
                true, 
                SVNDepth.EMPTY
            ); 
            System.out.println("doExport: " + doExport);
            
            // SVNCommitInfo svnCommitInfo = clientManager.getCommitClient().doCommit(
            //         new File[]{workingCopyDir},
            //         false,
            //         "Rollback " + fileName + " to revision " + oldRevision,
            //         null,
            //         null,
            //         false,
            //         false,
            //         SVNDepth.INFINITY);
            // System.out.println(svnCommitInfo.getErrorMessage() + ", " + svnCommitInfo.getNewRevision());
            // System.out.println(fileName + " rolled back successfully!");
        } catch (SVNException e) {
            System.err.println("Error rolling back file: " + e.getMessage());
        } finally {
            clientManager.dispose();
        }
    }

    @Test
    void rollbackTest2() throws SVNException {
        String svnUrl = "svn://192.168.0.186/sd-test-repo/branches/staging";
        String username = "svndeploy";
        String password = "svndeploy1!";
        long oldRevision = 134; // 이전 revision
        String workingPath = "C:\\deploy\\TEST_REPO\\branches\\staging";
        
        SvnRepositoryUtil.update(svnUrl, username, password, workingPath, oldRevision);
    }
}
