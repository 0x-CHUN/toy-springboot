package springboot.orm.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlResultCache {
    //sql result cache
    private static Map<String, Object> map = new ConcurrentHashMap<>();

    public void putCache(String sql, Object cache) {
        map.put(sql, cache);
    }

    public Object getCache(String sql) {
        return map.get(sql);
    }
    
    public void cleanCache(){
        map.clear();
    }
    
    public int getSize(){
        return map.size();
    }

    public void removeCache(String sql){
        map.remove(sql);
    }
}
