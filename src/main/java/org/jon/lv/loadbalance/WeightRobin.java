package org.jon.lv.loadbalance;

import java.util.*;

/**
 * @Package org.jon.lv.loadbalance.WeightRobin
 * @Description: 负载均衡-权重随机轮询
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/6/15 14:12
 * version V1.0.0
 */
public class WeightRobin {
    static Map<String,Integer> serverWeigthMap  = new HashMap<String,Integer>();
    static{
        serverWeigthMap.put("192.168.1.12", 1);
        serverWeigthMap.put("192.168.1.13", 1);
        serverWeigthMap.put("192.168.1.14", 2);
        serverWeigthMap.put("192.168.1.15", 2);
        serverWeigthMap.put("192.168.1.16", 3);
        serverWeigthMap.put("192.168.1.17", 5);
        serverWeigthMap.put("192.168.1.18", 1);
        serverWeigthMap.put("192.168.1.19", 2);
    }

    public static String weightRandom()
    {
        //重新建立一個map,避免出現由於服務器上線和下線導致的並發問題
        Map<String,Integer> serverMap  = new HashMap<String,Integer>();
        serverMap.putAll(serverWeigthMap);
        //獲取ip列表list
        Set<String> keySet = serverMap.keySet();
        Iterator<String> it = keySet.iterator();

        List<String> serverList = new ArrayList<String>();

        while (it.hasNext()) {
            String server = it.next();
            Integer weight = serverMap.get(server);
            for (int i = 0; i < weight; i++) {
                serverList.add(server);
            }
        }
        Random random = new Random();
        int randomPos = random.nextInt(serverList.size());

        String server = serverList.get(randomPos);
        return server;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            String serverIp = weightRandom();
            System.out.println(serverIp);
        }
    }
}
