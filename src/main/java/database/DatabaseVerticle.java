package database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ProxyHelper;
import service.UserService;

public class DatabaseVerticle extends AbstractVerticle {

    public static final String CONFIG_MONGO_URL = "mongodb.url";

    public static final String DB_NAME = "hourse_property";

    public static final String CONFIG_USERDB_QUEUE = "userdb.queue";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        MongoClient mongoClient = MongoClient.createShared(vertx,new JsonObject()
        .put("url",config().getString(CONFIG_MONGO_URL,"localhost:27017"))
        .put("db_name",config().getString(DB_NAME, "hourse_property")));

        UserService.create(mongoClient,ready -> {
            if(ready.succeeded()){
                ProxyHelper.registerService(UserService.class,vertx,ready.result(),CONFIG_USERDB_QUEUE);
                startFuture.complete();
            } else {
                startFuture.fail(ready.cause());
            }
        });
    }
}
