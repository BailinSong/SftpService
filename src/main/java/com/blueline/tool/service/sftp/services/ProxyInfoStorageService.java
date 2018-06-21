package com.blueline.tool.service.sftp.services;

import com.blueline.tool.proxy.tcp.domain.ProxyInfo;

public interface ProxyInfoStorageService {
    public void loadProxyInfo();
    public void saveProxyInfo(ProxyInfo proxyInfo);
    public void deleteProxyInfo(ProxyInfo proxyInfo);

}
