package ru.jamsys.trash;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

public class SmsAuth {

    private static volatile SmsAuth instance;

    public static SmsAuth getInstance() {
        SmsAuth localInstance = instance;
        if (localInstance == null) {
            synchronized (SmsAuth.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SmsAuth();
                }
            }
        }
        return localInstance;
    }

    private long maxLifeTime = 15 * 60 * 1000; //15 minutes

    class Try {
        private int maxTry = 30;
        private int curTry = 0;
        private long nextTry;
        private long dateAdd = System.currentTimeMillis();

        private String code;

        public Try() {
            int minimum = 1;
            int maximum = 9999;
            Random rn = new Random();
            int range = maximum - minimum + 1;
            int randomNum = rn.nextInt(range) + minimum;
            code = String.format("%04d", randomNum);
            nextTry = System.currentTimeMillis() + 1000; //Попытку отгадат можно будет через секунду
        }

        public boolean isExpired() {
            return curTry > maxTry || (System.currentTimeMillis() - dateAdd > maxLifeTime);
        }

        public boolean checkCode(String pCode) throws Exception {
            if (curTry > maxTry) {
                throw new Exception("Исчерпано максимальное кол-о попыток: " + maxTry);
            }
            long cur = System.currentTimeMillis();
            if (cur < nextTry) {
                int x = (int) ((nextTry - cur) / 1000);
                throw new Exception("Попробуйте через: " + x + " сек.; попытка: " + curTry);
            }
            boolean status = code.equals(pCode);
            if (status == false) {
                long mn = factorialUsingForLoop(++curTry);
                nextTry = System.currentTimeMillis() + (1000 * (mn + 2));
            }
            return code.equals(pCode);
        }
    }

    public long factorialUsingForLoop(int n) {
        long fact = 1;
        for (int i = 2; i <= n; i++) {
            fact = fact * i;
        }
        return fact;
    }

    Map<BigDecimal, Try> map = new ConcurrentHashMap();

    public void generateCode(BigDecimal idPerson) {
        if (!map.containsKey(idPerson)) {
            map.put(idPerson, new Try());
        }
    }

    public boolean checkCode(BigDecimal idPerson, String code) throws Exception {
        clear();
        if (map.containsKey(idPerson)) {
            return map.get(idPerson).checkCode(code);
        } else {
            throw new Exception("Ключ не найден, либо исчерпано максимальное кол-во попыток");
        }
    }

    private void clear() {
        try {
            BigDecimal[] objects = map.keySet().toArray(new BigDecimal[0]);
            for (BigDecimal item : objects) {
                if (item != null && map.get(item) != null && map.get(item).isExpired()) {
                    map.remove(item);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
