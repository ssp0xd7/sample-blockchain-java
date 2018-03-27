package com.blockchain.simple.demo.service;

import java.util.List;
import org.java_websocket.WebSocket;

/**
 * peer to peer
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
public interface P2PService {

    /**
     * 端口初始化
     * 
     * @param port
     */
    void initP2PServer(int port);

    /**
     * 添加节点
     * 
     * @param peer
     */
    void connectToPeer(String peer);

    /**
     * 广播
     * 
     * @param message
     */
    void broatcast(String message);

    /**
     * 响应查询
     * 
     * @return
     */
    String responseLatestMsg();

    /**
     * 获取所有的链接
     * 
     * @return
     */
    List<WebSocket> getSockets();
}
