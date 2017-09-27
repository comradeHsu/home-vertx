package http;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.rxjava.HouseService;
import service.rxjava.UserService;

public class HttpServerVerticle extends AbstractVerticle {

    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_USERDB_QUEUE = "userdb.queue";
    public static final String CONFIG_HOUSEDB_QUEUE = "housedb.queue";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private UserService userService;

    private HouseService houseService;


    public void start(Future<Void> startFuture) throws Exception {
        String userDbQueue = config().getString(CONFIG_USERDB_QUEUE, "userdb.queue");
        userService = service.UserService.createProxy(vertx.getDelegate(),userDbQueue);

        String houseDbQueue = config().getString(CONFIG_HOUSEDB_QUEUE,"housedb.queue");
        houseService = service.HouseService.createProxy(vertx.getDelegate(),houseDbQueue);

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/api/accounts").handler(this::getAllUsers);

        int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
        server
                .requestHandler(router::accept)
                .rxListen(portNumber)
                .subscribe(s -> {
                    LOGGER.info("HTTP server running on port " + portNumber);
                    startFuture.complete();
                }, t -> {
                    LOGGER.error("Could not start a HTTP server", t);
                    startFuture.fail(t);
                });
    }

    private void getAllUsers(RoutingContext context){
        JsonObject page = context.getBodyAsJson();
        int pageSize = page.getInteger("pageSize",10);
        int pageNumber = page.getInteger("pageNumber",0);
        userService.rxFetchAllUsers(pageSize,pageNumber)
                .zipWith(userService.rxCountAllUsers(),(array,count) -> new JsonObject()
                .put("data",array).put("totalCount",count))
                .subscribe(rs -> {
                    rs.put("pageSize",pageSize)
                            .put("pageNumber",pageNumber)
                            .put("msg","success");
                    apiResponse(context,200,"data",rs);
                },throwable -> apiFailure(context,throwable));
    }

    private void apiFailure(RoutingContext context, int statusCode, String error) {
        context.response().setStatusCode(statusCode);
        context.response().putHeader("Content-Type", "application/json");
        context.response().end(new JsonObject()
                .put("success", false)
                .put("error", error).encode());
    }

    private void apiFailure(RoutingContext context, Throwable t) {
        apiFailure(context, 500, t.getMessage());
    }


    private void apiResponse(RoutingContext context, int statusCode, String jsonField, Object jsonData) {
        context.response().setStatusCode(statusCode);
        context.response().putHeader("Content-Type", "application/json");
        JsonObject wrapped = new JsonObject().put("success", true);
        if (jsonField != null && jsonData != null) wrapped.put(jsonField, jsonData);
        context.response().end(wrapped.encode());
    }
}
