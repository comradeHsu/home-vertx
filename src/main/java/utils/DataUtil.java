package utils;

import io.vertx.core.json.JsonObject;

public class DataUtil {

    public static void handler(JsonObject res){
        String id = res.getJsonObject("_id").getString("$oid");
        res.remove("_id");
        res.remove("_class");
        res.put("id",id);
    }

    public static JsonObject noVoidhandler(JsonObject res){
        String id = res.getJsonObject("_id").getString("$oid");
        res.remove("_id");
        res.remove("_class");
        res.put("id",id);
        return res;
    }
}
