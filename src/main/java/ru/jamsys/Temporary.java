package ru.jamsys;

import ru.jamsys.sub.TemporaryItem;
import ru.jamsys.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Temporary {

    static Map<String, TemporaryItem> map = new ConcurrentHashMap();
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

    public String get(String hash) {
        clear();
        return map.containsKey(hash) ?  map.get(hash).getIdPerson() : null;
    }

    public String createHash(String idPerson) throws UnsupportedEncodingException {
        clear();
        String hash = Util.getHashCharset(idPerson, "md5", "utf-8");
        if(!map.containsKey(hash)){
            map.put(hash, new TemporaryItem(idPerson));
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
