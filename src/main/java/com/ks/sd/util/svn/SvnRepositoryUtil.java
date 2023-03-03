package com.ks.sd.util.svn;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
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
        try {
            if (svnUrl != null && svnUrl.length() > 0 && 
                svnUsername != null && svnUsername.length() > 0 && 
                svnPassword != null && svnPassword.length() > 0) {

                String encodedUrl = SVNEncodingUtil.autoURIEncode(svnUrl);
                SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(encodedUrl));
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
        SVNClientManager deploySvnClientManager = SVNClientManager.newInstance(options, svnUsername, svnPassword);
        SVNUpdateClient updateClient = deploySvnClientManager.getUpdateClient();
        
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
        }

        return isResult;
    }

    public static Long getLatestRevision(String svnUrl, String svnUsername, String svnPassword) {
        Long latestRevision = -1L;

        try {
            SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
            ISVNAuthenticationManager authManager = 
                            SVNWCUtil.createDefaultAuthenticationManager(svnUsername, svnPassword.toCharArray());
            repository.setAuthenticationManager(authManager);

            latestRevision = repository.getLatestRevision();
        } catch (SVNException e) {
            LOGGER.debug(e.toString());
        }
        
        return latestRevision;
    }
}
