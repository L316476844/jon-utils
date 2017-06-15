package org.jon.lv.ranodm;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;

/**
 * @Package org.jon.lv.ranodm.IdGenerator
 * @Description: 生成24位字符串ID
 * @Copyright: Copyright (c) 2016
 * Author lv bin
 * @date 2017/6/15 14:48
 * version V1.0.0
 */
public class IdGenerator implements Comparable<IdGenerator> {
    final int _time;
    final int _machine;
    final int _inc;
    boolean _new;

    private static int _nextInc = (new java.util.Random()).nextInt();
    private static final String _incLock = new String("IdGenerator._incLock");

    private static int _gentime = _flip((int) (System.currentTimeMillis() / 1000));

    static final Thread _timeFixer;

    private static final int _genmachine;

    static {
        try {
            final int machinePiece;
            {
                StringBuilder sb = new StringBuilder();
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements()) {
                    NetworkInterface ni = e.nextElement();
                    sb.append(ni.toString());
                }
                machinePiece = sb.toString().hashCode() << 16;
            }

            final int processPiece = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode() & 0xFFFF;
            _genmachine = machinePiece | processPiece;
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(ioe);
        }

        _timeFixer = new Thread("IdGenerator-TimeFixer") {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(499);
                    } catch (InterruptedException e) {
                    }
                    _gentime = _flip((int) (System.currentTimeMillis() / 1000));
                }
            }
        };
        _timeFixer.setDaemon(true);
        _timeFixer.start();
    }

    public IdGenerator() {
        _time = _gentime;
        _machine = _genmachine;
        synchronized (_incLock) {
            _inc = _nextInc++;
        }
        _new = true;
    }

    /**
     * 调用该方法获取24位字符串ID
     *
     * @return
     */
    public static String get() {
        return new IdGenerator().toString();
    }

    public int hashCode() {
        return _inc;
    }

    public String toStringMongod() {
        byte b[] = toByteArray();

        StringBuilder buf = new StringBuilder(24);

        for (int i = 0; i < b.length; i++) {
            int x = b[i] & 0xFF;
            String s = Integer.toHexString(x);
            if (s.length() == 1)
                buf.append("0");
            buf.append(s);
        }

        return buf.toString();
    }

    public byte[] toByteArray() {
        byte b[] = new byte[12];
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.putInt(_inc);
        bb.putInt(_machine);
        bb.putInt(_time);
        reverse(b);
        return b;
    }

    static void reverse(byte[] b) {
        for (int i = 0; i < b.length / 2; i++) {
            byte t = b[i];
            b[i] = b[b.length - (i + 1)];
            b[b.length - (i + 1)] = t;
        }
    }

    static String _pos(String s, int p) {
        return s.substring(p * 2, (p * 2) + 2);
    }

    public String toString() {
        return toStringMongod();
    }

    public int compareTo(IdGenerator id) {
        if (id == null)
            return -1;

        long xx = id.getTime() - getTime();
        if (xx > 0)
            return -1;
        else if (xx < 0)
            return 1;

        int x = id._machine - _machine;
        if (x != 0)
            return -x;

        x = id._inc - _inc;
        if (x != 0)
            return -x;

        return 0;
    }

    public int getMachine() {
        return _machine;
    }

    public long getTime() {
        long z = _flip(_time);
        return z * 1000;
    }

    public int getInc() {
        return _inc;
    }


    static int _flip(int x) {
        byte b[] = new byte[4];
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(x);
        bb.flip();
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getInt();
    }


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(IdGenerator.get());
        }
    }
}
