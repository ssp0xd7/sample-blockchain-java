package com.blockchain.simple.demo.web;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.blockchain.simple.demo.meta.Block;
import com.blockchain.simple.demo.service.BlockService;
import com.blockchain.simple.demo.service.P2PService;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
@Controller
public class WebController {
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private BlockService blockService;

    @Autowired
    private P2PService p2pService;

    /**
     * 获取所有区块
     * 
     * @return
     */
    @RequestMapping("/blocks")
    @ResponseBody
    public Object blocks() {
        return blockService.getBlockChain();
    }

    /**
     * 添加新区块
     * 
     * @param data
     * @return
     */
    @RequestMapping("/mineBlock")
    @ResponseBody
    public Object mineBlock(String data) {
        Block newBlock = blockService.generateNextBlock(data);
        blockService.addBlock(newBlock);
        p2pService.broatcast(p2pService.responseLatestMsg());
        logger.info("block added. block:{}", JSON.toJSONString(newBlock));
        return newBlock;
    }

    /**
     * 获取所有节点信息
     * 
     * @return
     */
    @RequestMapping("/peers")
    @ResponseBody
    public Object peers() {
        List<String> peersInfo = new ArrayList<>();
        for (WebSocket socket: p2pService.getSockets()) {
            InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
            peersInfo.add(remoteSocketAddress.getHostName() + ":" + remoteSocketAddress.getPort());
        }
        return peersInfo;
    }

    /**
     * 添加通知节点
     * 
     * @param peer
     * @return
     */
    @RequestMapping("/addPeer")
    @ResponseBody
    public Object addPeer(String peer) {
        p2pService.connectToPeer(peer);
        return "add OK. peer:" + peer;
    }
}
