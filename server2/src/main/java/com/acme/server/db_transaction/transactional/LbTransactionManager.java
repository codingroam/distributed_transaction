package com.acme.server.db_transaction.transactional;

import com.alibaba.fastjson.JSONObject;
import com.acme.server.db_transaction.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LbTransactionManager {


    private static NettyClient nettyClient;

    private static ThreadLocal<LbTransaction> current = new ThreadLocal<>();
    private static ThreadLocal<String> currentGroupId = new ThreadLocal<>();
    private static ThreadLocal<Integer> transactionCount = new ThreadLocal<>();

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        LbTransactionManager.nettyClient = nettyClient;
    }

    public static Map<String, LbTransaction> LB_TRANSACION_MAP = new HashMap<>();

    public static String createLbTransactionGroup() {
        String groupId = UUID.randomUUID().toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId", groupId);
        jsonObject.put("command", "create");
        nettyClient.send(jsonObject);
        System.out.println("创建事务组");

        currentGroupId.set(groupId);
        return groupId;
    }

    public static LbTransaction createLbTransaction(String groupId) {
        String transactionId = UUID.randomUUID().toString();
        LbTransaction lbTransaction = new LbTransaction(groupId, transactionId);
        LB_TRANSACION_MAP.put(groupId, lbTransaction);
        current.set(lbTransaction);
        addTransactionCount();

        System.out.println("创建事务");

        return lbTransaction;
    }

    public static LbTransaction addLbTransaction(LbTransaction lbTransaction, Boolean isEnd, TransactionType transactionType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("groupId", lbTransaction.getGroupId());
        jsonObject.put("transactionId", lbTransaction.getTransactionId());
        jsonObject.put("transactionType", transactionType);
        jsonObject.put("command", "add");
        jsonObject.put("isEnd", isEnd);
        jsonObject.put("transactionCount", LbTransactionManager.getTransactionCount());
        nettyClient.send(jsonObject);
        System.out.println("添加事务");
        return lbTransaction;
    }

    public static LbTransaction getLbTransaction(String groupId) {
        return LB_TRANSACION_MAP.get(groupId);
    }

    public static LbTransaction getCurrent() {
        return current.get();
    }
    public static String getCurrentGroupId() {
        return currentGroupId.get();
    }

    public static void setCurrentGroupId(String groupId) {
        currentGroupId.set(groupId);
    }

    public static Integer getTransactionCount() {
        return transactionCount.get();
    }

    public static void setTransactionCount(int i) {
        transactionCount.set(i);
    }

    public static Integer addTransactionCount() {
        int i = (transactionCount.get() == null ? 0 : transactionCount.get()) + 1;
        transactionCount.set(i);
        return i;
    }
}
