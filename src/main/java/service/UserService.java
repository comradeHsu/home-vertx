package service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.mongo.MongoClient;

@ProxyGen
@VertxGen
public interface UserService {

    @Fluent
    UserService fetchAllUsers(Handler<AsyncResult<JsonArray>> resultHandler);

    @GenIgnore
    static UserService create(MongoClient client, Handler<AsyncResult<UserService>> readyHandle){
        return new UserServiceImpl(client,readyHandle);
    }

    @GenIgnore
    static service.rxjava.UserService createProxy(Vertx vertx, String address) {
        return new service.rxjava.UserService(new UserServiceVertxEBProxy(vertx, address));
    }
}
