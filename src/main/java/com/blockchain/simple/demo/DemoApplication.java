package com.blockchain.simple.demo;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.blockchain.simple.demo.service.P2PService;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    private P2PService p2pService;

    private static P2PService p2pServiceStatic;

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    @PostConstruct
    public void init() {
        p2pServiceStatic = p2pService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        if (args != null && (args.length == 2 || args.length == 3)) {
            try {
                int p2pPort = Integer.valueOf(args[1]);
                p2pServiceStatic.initP2PServer(p2pPort);
                if (args.length == 3 && args[2] != null) {
                    p2pServiceStatic.connectToPeer(args[2]);
                }
            } catch (Exception e) {
                logger.error("startup is error:{}", e.getMessage());
            }
        } else {
            logger.error("usage: java -jar simple-block-chain.jar 7001 --server.port:8080");
            logger.error("usage: java -jar simple-block-chain.jar 7001 ws://localhost:7001 --server.port:8080");
        }
    }
}
