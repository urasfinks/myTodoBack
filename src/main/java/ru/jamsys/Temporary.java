package ru.jamsys;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchProviderException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Temporary {

    private static volatile Temporary instance;

    public static Temporary getInstance() {
        Temporary localInstance = instance;
        if (localInstance == null) {
            synchronized (Temporary.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Temporary();
                }
            }
        }
        return localInstance;
    }

    static Map<String, Item> map = new ConcurrentHashMap();

    class Item{
        private String idPerson;
        private long timeout = System.currentTimeMillis() + (15 * 60 * 1000);

        public Item(String idPerson) {
            this.idPerson = idPerson;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > timeout;
        }

        public String getIdPerson(){
            return idPerson;
        }

    }

    public String get(String hash) {
        clear();
        return map.containsKey(hash) ?  map.get(hash).getIdPerson() : null;
    }

    public String createHash(String idPerson) throws NoSuchProviderException, UnsupportedEncodingException {
        clear();
        String hash = Util.getHashCharset(idPerson, "md5", "utf-8");
        if(!map.containsKey(hash)){
            map.put(hash, new Item(idPerson));
        }
        return hash;
    }

    private void clear() {
        try {
            String[] objects = map.keySet().toArray(new String[0]);
            for (String item : objects) {
                if (item != null && map.get(item) != null && map.get(item).isExpired()) {
                    map.remove(item);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
