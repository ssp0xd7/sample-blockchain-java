package com.blockchain.simple.demo.service.impl;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.blockchain.simple.demo.meta.Block;
import com.blockchain.simple.demo.meta.Message;
import com.blockchain.simple.demo.service.BlockService;
import com.blockchain.simple.demo.service.P2PService;

import static com.blockchain.simple.demo.meta.MsgType.QUERY_ALL;
import static com.blockchain.simple.demo.meta.MsgType.QUERY_LATEST;
import static com.blockchain.simple.demo.meta.MsgType.RESPONSE_BLOCKCHAIN;

/**
 * @author kevin(ssp0xd7 @ gmail.com) 23/01/2018
 */
@Service("p2pService")
public class P2PServiceImpl implements P2PService {

    @Autowired
    private BlockService blockService;

    private static List<WebSocket> sockets = Collections.synchronizedList(new LinkedList<>());

    private static final Logger logger = LoggerFactory.getLogger(P2PService.class);

    @Override
    public void initP2PServer(int port) {
        final WebSocketServer server = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                write(webSocket, queryChainLengthMsg());
                sockets.add(webSocket);
            }

            @Override
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                logger.info("disconnection to peer:{}", webSocket.getRemoteSocketAddress());
                sockets.remove(webSocket);
            }

            @Override
            public void onMessage(WebSocket webSocket, String s) {
                handleMessage(webSocket, s);
            }

            @Override
            public void onError(WebSocket webSocket, Exception e) {
                logger.error("connection failed to peer:{}", webSocket.getRemoteSocketAddress());
                sockets.remove(webSocket);
            }

            @Override
            public void onStart() {

            }
        };
        server.start();
        logger.info("listening websocket p2p port on:{}", port);
    }

    @Override
    public void connectToPeer(String peer) {
        try {
            final WebSocketClient socket = new WebSocketClient(new URI(peer)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    write(this, queryChainLengthMsg());
                    sockets.add(this);
                }

                @Override
                public void onMessage(String s) {
                    handleMessage(this, s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    logger.info("connection off.");
                    sockets.remove(this);
                }

                @Override
                public void onError(Exception e) {
                    logger.error("connection failed.");
                    sockets.remove(this);
                }
            };
            socket.connect();
        } catch (URISyntaxException e) {
            logger.error("p2p connect cause error:", e.getMessage());
        }
    }

    @Override
    public void broatcast(String message) {
        for (WebSocket socket: sockets) {
            this.write(socket, message);
        }
    }

    @Override
    public String responseLatestMsg() {
        Block[] blocks = { blockService.getLatestBlock() };
        return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(blocks)));
    }

    @Override
    public List<WebSocket> getSockets() {
        return sockets;
    }

    /**
     * 处理端口收到的消息
     * 
     * @param webSocket
     * @param s
     */
    private void handleMessage(WebSocket webSocket, String s) {
        try {
            Message message = JSON.parseObject(s, Message.class);
            logger.info("Received message:{}", JSON.toJSONString(message));
            switch (message.getType()) {
                case QUERY_LATEST:
                    write(webSocket, responseLatestMsg());
                    break;
                case QUERY_ALL:
                    write(webSocket, responseChainMsg());
                    break;
                case RESPONSE_BLOCKCHAIN:
                    handleBlockChainResponse(message.getData());
                    break;
            }
        } catch (Exception e) {
            logger.error("hanle message is error:{}", e.getMessage());
        }
    }

    //write
    private void write(WebSocket ws, String message) {
        ws.send(message);
    }

    //查询最后一个block
    private String queryChainLengthMsg() {
        return JSON.toJSONString(new Message(QUERY_LATEST));
    }

    private String queryAllMsg() {
        return JSON.toJSONString(new Message(QUERY_ALL));
    }

    //响应链信息
    private String responseChainMsg() {
        return JSON.toJSONString(new Message(RESPONSE_BLOCKCHAIN, JSON.toJSONString(blockService.getBlockChain())));
    }

    //挖矿
    private void handleBlockChainResponse(String message) {
        List<Block> receiveBlocks = JSON.parseArray(message, Block.class);
        Collections.sort(receiveBlocks, new Comparator<Block>() {
            public int compare(Block o1, Block o2) {
                return (int) (o1.getIndex() - o1.getIndex());
            }
        });

        Block latestBlockReceived = receiveBlocks.get(receiveBlocks.size() - 1);
        Block latestBlock = blockService.getLatestBlock();
        if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
            if (latestBlock.getHash().equals(latestBlockReceived.getPreHash())) {
                logger.info("We can append the received block to our chain.");
                blockService.addBlock(latestBlockReceived);
                broatcast(responseLatestMsg());
            } else if (receiveBlocks.size() == 1) {
                logger.info("We have to query the chain from our peer.");
                broatcast(queryAllMsg());
            } else {
                blockService.replaceChain(receiveBlocks);
            }
        } else {
            logger.info("Received blockchain is not longer than received blockchain. Do nothing");
        }
    }
}
