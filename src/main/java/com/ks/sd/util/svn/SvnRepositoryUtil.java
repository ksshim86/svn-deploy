package com.ks.sd.util.svn;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnRepositoryUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(SvnRepositoryUtil.class);
    
    /**
     * SVN 저장소 연결 확인
     * @param svnUrl
     * @param svnUsername
     * @param svnPassword
     * @return isConnect
     */
    public static boolean isConnect(String svnUrl, String svnUsername, String svnPassword) {
        boolean isConnect = false;
        SVNRepository repository = null;
         
        try {
            if (svnUrl != null && svnUrl.length() > 0 && 
                svnUsername != null && svnUsername.length() > 0 && 
                svnPassword != null && svnPassword.length() > 0) {

                String encodedUrl = SVNEncodingUtil.autoURIEncode(svnUrl);
                repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(encodedUrl));
                ISVNAuthenticationManager authManager = 
                    SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
                repository.setAuthenticationManager(authManager);
                repository.testConnection();
                isConnect = true;    
            } else {
                LOGGER.debug("SVN 접속정보 필요");
                isConnect = false;
            }
        } catch (SVNException e) {
            LOGGER.debug(e.toString());
            isConnect = false;
        } finally {
            if (repository != null) {
                repository.closeSession();
            }
        }

        LOGGER.debug(svnUrl + " isConnect: " + isConnect);

        return isConnect;
    }

    /**
     * SVN 체크아웃
     * @param svnUrl
     * @param svnUsername
     * @param svnPassword
     * @param checkoutPath
     * @return
     */
    public static boolean checkout(String svnUrl, String svnUsername, String svnPassword, String checkoutPath) {
        boolean isResult = false;
        String encodeSvnUrl = SVNEncodingUtil.autoURIEncode(svnUrl);
        
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options, svnUsername, svnPassword);
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        
        try {
            updateClient.setEventHandler(new ISVNEventHandler() {
                @Override
                public void handleEvent(SVNEvent evt, double progress) throws SVNException {
                    LOGGER.debug(progress + " > " + evt.toString());
                }

                @Override
                public void checkCancelled() throws SVNCancelException {
                    // throw new UnsupportedOperationException("Unimplemented method 'checkCancelled'");
                }
            });
            Long revision = updateClient.doCheckout(SVNURL.parseURIEncoded(encodeSvnUrl), Paths.get(checkoutPath).toFile(),
            			SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
            LOGGER.debug(encodeSvnUrl + " > checkout revision: " + revision);            
            isResult = true;
        } catch (SVNException e) {
            LOGGER.debug(e.toString());
            isResult = false;
        } finally {
            clientManager.dispose();
        }

        return isResult;
    }

    /**
     * SVN 최신 리비전 조회
     * @param svnUrl
     * @param svnUsername
     * @param svnPassword
     * @return
     */
    public static Long getLatestRevision(String svnUrl, String svnUsername, String svnPassword) {
        Long latestRevision = -1L;
        SVNRepository repository = null;

        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
            ISVNAuthenticationManager authManager = 
                            SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
            repository.setAuthenticationManager(authManager);
            latestRevision = repository.getLatestRevision();
        } catch (SVNException e) {
            LOGGER.debug(e.toString());
        } finally {
            if (repository != null) {
                repository.closeSession();
            }
        }
        
        return latestRevision;
    }

    /**
     * SVN 브랜치 생성
     * @param svnUrl
     * @param svnUsername
     * @param svnPassword
     * @param branchName
     * @param message
     */
    public static boolean createBranch(
        String svnUrl, String svnUsername, String svnPassword, 
        String orgBranchNm, String dstBranchNm, String message
    ) {
        boolean isResult = false;
        SVNClientManager clientManager = SVNClientManager.newInstance();
        ISVNAuthenticationManager authManager = 
            SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
        clientManager.setAuthenticationManager(authManager);
        
        try {
            SVNURL orgBranchUrl = SVNURL.parseURIEncoded(svnUrl + orgBranchNm);
            SVNURL dstBranchUrl = SVNURL.parseURIEncoded(svnUrl + dstBranchNm);

            try {
                SVNInfo svnInfo = 
                    clientManager.getWCClient().doInfo(dstBranchUrl, SVNRevision.HEAD, SVNRevision.HEAD);
                if (svnInfo != null && svnInfo.getKind() == SVNNodeKind.DIR) {
                    LOGGER.debug("Branch already exists: " + dstBranchUrl);
                    isResult = false;
    
                    return isResult;
                }
            } catch (SVNException e) {
                // exception이 발생했다면 생성되어 있지 않은 branch이기 때문에 문제없음.
            }

            SVNCopySource[] sources = new SVNCopySource[] {
                new SVNCopySource(
                    SVNRevision.HEAD,
                    SVNRevision.HEAD,
                    orgBranchUrl
                )
            };
            
            clientManager.getCopyClient().doCopy(
                sources,
                dstBranchUrl,
                false,
                true,
                false,
                message,
                null
            );

            isResult = true;
        } catch (SVNException e) {
            LOGGER.debug(e.toString());
            isResult = false;
        } finally {
            clientManager.dispose();
        }

        return isResult;
    }

    /**
     * SVN update
     * @param svnUrl
     * @param svnUsername
     * @param svnPassword
     * @param workingPath
     * @param revision
     * @return
     */
    public static boolean update(String svnUrl, String svnUsername, String svnPassword, String workingPath, long revision) {
        boolean isResult = false;
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options, svnUsername, svnPassword);
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        
        try {
            File wcDir = new File(workingPath);
            updateClient.doUpdate(wcDir, SVNRevision.create(revision), SVNDepth.INFINITY, false, false);
            isResult = true;
        } catch (SVNException e) {
            LOGGER.debug(e.toString());
            isResult = false;
        } finally {
            clientManager.dispose();
       }

       return isResult;
    }

    /**
     * SVN log 조회
     * @param svnUrl
     * @param svnUsername
     * @param svnPassword
     * @param startRev
     * @param endRev
     * @return
     */
    public static List<SVNLogEntry> fetchSvnLog(
        String svnUrl, String svnUsername, String svnPassword, long startRev, long endRev
    ) {
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options, svnUsername, svnPassword);
        SVNLogClient logClient = clientManager.getLogClient();
        List<SVNLogEntry> logEntryList = new ArrayList<>();

        try {
            String[] paths = {"/"};
            fetchLogEntries(logClient, svnUrl, paths, startRev, endRev, logEntryList);
        } catch (Exception e) {
            LOGGER.debug(e.toString());
        } finally {
            clientManager.dispose();
        }

        return logEntryList;
    }

    private static void fetchLogEntries(
        SVNLogClient logClient, String svnUrl, String[] paths,
        long startRev, long endRev, List<SVNLogEntry> logEntryList
    ) throws SVNException {
        logClient.doLog(
            SVNURL.parseURIEncoded(svnUrl),
            paths,
            SVNRevision.create(startRev),
            SVNRevision.create(startRev),
            SVNRevision.create(endRev),
            false, true, false, 999999L, null,
            logEntryList::add
        );
    }
}
