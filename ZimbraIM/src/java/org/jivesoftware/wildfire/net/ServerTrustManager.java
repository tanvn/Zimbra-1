/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.Log;

import javax.net.ssl.X509TrustManager;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * ServerTrustManager is a Trust Manager that is only used for s2s connections. This TrustManager
 * is used both when the server connects to another server as well as when receiving a connection
 * from another server. In both cases, it is possible to indicate if self-signed certificates
 * are going to be accepted. In case of accepting a self-signed certificate a warning is logged.
 * Future version of the server might include a small workflow so admins can review self-signed
 * certificates or certificates of unknown issuers and manually accept them.
 *
 * @author Gaston Dombiak
 */
class ServerTrustManager implements X509TrustManager {

    /**
     * KeyStore that holds the trusted CA
     */
    private KeyStore trustStore;
    /**
     * Holds the domain of the remote server we are trying to connect
     */
    private String server;

    public ServerTrustManager(String server, KeyStore trustTrust) {
        super();
        this.server = server;
        this.trustStore = trustTrust;
    }

    public void checkClientTrusted(X509Certificate[] x509Certificates,
            String string) {
        // Do not validate the certificate at this point. The certificate is going to be used
        // when the remote server requests to do EXTERNAL SASL
    }

    /**
     * Given the partial or complete certificate chain provided by the peer, build a certificate
     * path to a trusted root and return if it can be validated and is trusted for server SSL
     * authentication based on the authentication type. The authentication type is the key
     * exchange algorithm portion of the cipher suites represented as a String, such as "RSA",
     * "DHE_DSS". Note: for some exportable cipher suites, the key exchange algorithm is
     * determined at run time during the handshake. For instance, for
     * TLS_RSA_EXPORT_WITH_RC4_40_MD5, the authType should be RSA_EXPORT when an ephemeral
     * RSA key is used for the key exchange, and RSA when the key from the server certificate
     * is used. Checking is case-sensitive.<p>
     *
     * By default certificates are going to be verified. This includes verifying the certificate
     * chain, the root certificate and the certificates validity. However, it is possible to
     * disable certificates validation as a whole or each specific validation.
     *
     * @param x509Certificates an ordered array of peer X.509 certificates with the peer's own
     *        certificate listed first and followed by any certificate authorities.
     * @param string the key exchange algorithm used.
     * @throws CertificateException if the certificate chain is not trusted by this TrustManager.
     */
    public void checkServerTrusted(X509Certificate[] x509Certificates, String string)
            throws CertificateException {

        // Flag that indicates if certificates of the remote server should be validated. Disabling
        // certificate validation is not recommended for production environments.
        boolean verify = IMConfig.XMPP_SERVER_CERTIFICATE_VERIFY.getBoolean();
        
        if (verify) {
            int nSize = x509Certificates.length;

            List<String> peerIdentities = TLSStreamHandler.getPeerIdentities(x509Certificates[0]);

            if (IMConfig.XMPP_SERVER_CERTIFICATE_VERIFY_CHAIN.getBoolean()) {
                // Working down the chain, for every certificate in the chain,
                // verify that the subject of the certificate is the issuer of the
                // next certificate in the chain.
                Principal principalLast = null;
                for (int i = nSize -1; i >= 0 ; i--) {
                    X509Certificate x509certificate = x509Certificates[i];
                    Principal principalIssuer = x509certificate.getIssuerDN();
                    Principal principalSubject = x509certificate.getSubjectDN();
                    if (principalLast != null) {
                        if (principalIssuer.equals(principalLast)) {
                            try {
                                PublicKey publickey =
                                        x509Certificates[i + 1].getPublicKey();
                                x509Certificates[i].verify(publickey);
                            }
                            catch (GeneralSecurityException generalsecurityexception) {
                                throw new CertificateException(
                                        "signature verification failed of " + peerIdentities);
                            }
                        }
                        else {
                            throw new CertificateException(
                                    "subject/issuer verification failed of " + peerIdentities);
                        }
                    }
                    principalLast = principalSubject;
                }
            }

            if (IMConfig.XMPP_SERVER_CERTIFICATE_VERIFY_ROOT.getBoolean()) {
                // Verify that the the last certificate in the chain was issued
                // by a third-party that the client trusts.
                boolean trusted = false;
                try {
                    trusted = trustStore.getCertificateAlias(x509Certificates[nSize - 1]) != null;
                    if (!trusted && nSize == 1 && 
                        IMConfig.XMPP_SERVER_CERTIFICATE_ACCEPT_SELFSIGNED.getBoolean()                                     
                        )
                    {
                        Log.warn("Accepting self-signed certificate of remote server: " +
                                peerIdentities);
                        trusted = true;
                    }
                }
                catch (KeyStoreException e) {
                    Log.error(e);
                }
                if (!trusted) {
                    throw new CertificateException("root certificate not trusted of " + peerIdentities);
                }
            }

            // Verify that the first certificate in the chain corresponds to
            // the server we desire to authenticate.
            // Check if the certificate uses a wildcard indicating that subdomains are valid
            if (peerIdentities.size() == 1 && peerIdentities.get(0).startsWith("*.")) {
                // Remove the wildcard
                String peerIdentity = peerIdentities.get(0).replace("*.", "");
                // Check if the requested subdomain matches the certified domain
                if (!server.endsWith(peerIdentity)) {
                    throw new CertificateException("target verification failed of " + peerIdentities);
                }
            }
            else if (!peerIdentities.contains(server)) {
                throw new CertificateException("target verification failed of " + peerIdentities);
            }

            if (IMConfig.XMPP_SERVER_CERTIFICATE_VERIFY_VALIDITY.getBoolean()) {
                // For every certificate in the chain, verify that the certificate
                // is valid at the current time.
                Date date = new Date();
                for (int i = 0; i < nSize; i++) {
                    try {
                        x509Certificates[i].checkValidity(date);
                    }
                    catch (GeneralSecurityException generalsecurityexception) {
                        throw new CertificateException("invalid date of " + peerIdentities);
                    }
                }
            }
        }
    }

    private boolean isChainTrusted(X509Certificate[] chain) {
        boolean trusted = false;
        try {
            // Start with the root and see if it is in the Keystore.
            // The root is at the end of the chain.
            for (int i = chain.length - 1; i >= 0; i--) {
                if (trustStore.getCertificateAlias(chain[i]) != null) {
                    trusted = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            Log.error(e);
            trusted = false;
        }
        return trusted;
    }

    public X509Certificate[] getAcceptedIssuers() {
        if (IMConfig.XMPP_SERVER_CERTIFICATE_ACCEPT_SELFSIGNED.getBoolean()) {
            // Answer an empty list since we accept any issuer
            return new X509Certificate[0];
        }
        else {
            X509Certificate[] X509Certs = null;
            try {
                // See how many certificates are in the keystore.
                int numberOfEntry = trustStore.size();
                // If there are any certificates in the keystore.
                if (numberOfEntry > 0) {
                    // Create an array of X509Certificates
                    X509Certs = new X509Certificate[numberOfEntry];

                    // Get all of the certificate alias out of the keystore.
                    Enumeration aliases = trustStore.aliases();

                    // Retrieve all of the certificates out of the keystore
                    // via the alias name.
                    int i = 0;
                    while (aliases.hasMoreElements()) {
                        X509Certs[i] =
                                (X509Certificate) trustStore.
                                        getCertificate((String) aliases.nextElement());
                        i++;
                    }

                }
            }
            catch (Exception e) {
                Log.error(e);
                X509Certs = null;
            }
            return X509Certs;
        }
    }
}
