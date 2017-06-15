package org.jon.lv.loadbalance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Package org.jon.lv.loadbalance.RoundRobin
 * @Description: 负载均衡-轮询算法
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/6/15 14:07
 * version V1.0.0
 */
public class RoundRobin {

    static Map<String,Integer> serverWeigthMap  = new HashMap<String,Integer>();

    static{
        serverWeigthMap.put("192.168.1.12", 1);
        serverWeigthMap.put("192.168.1.13", 1);
        serverWeigthMap.put("192.168.1.14", 2);
        serverWeigthMap.put("192.168.1.15", 2);
        serverWeigthMap.put("192.168.1.16", 3);
        serverWeigthMap.put("192.168.1.17", 3);
        serverWeigthMap.put("192.168.1.18", 1);
        serverWeigthMap.put("192.168.1.19", 2);
    }
    Integer  pos = 0;
    public  String roundRobin()
    {
        //重新建立一個map,避免出現由於服務器上線和下線導致的並發問題
        Map<String,Integer> serverMap  = new HashMap<String,Integer>();
        serverMap.putAll(serverWeigthMap);
        //獲取ip列表list
        Set<String> keySet = serverMap.keySet();
        ArrayList<String> keyList = new ArrayList<String>();
        keyList.addAll(keySet);

        String server = null;

        synchronized (pos) {
            if(pos >=keySet.size()){
                pos = 0;
            }
            server = keyList.get(pos);
            pos ++;
        }
        return server;
    }

    public static void main(String[] args) {
        RoundRobin robin = new RoundRobin();
        for (int i = 0; i < 16; i++) {
            String serverIp = robin.roundRobin();
            System.out.println(serverIp);
        }
    }
}
