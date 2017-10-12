package http;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.SessionHandler;
import io.vertx.rxjava.ext.web.sstore.LocalSessionStore;
import io.vertx.rxjava.ext.web.sstore.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.rxjava.HouseService;
import service.rxjava.UserService;
import utils.DataUtil;

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
        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(BodyHandler.create());
        router.route().handler(sessionHandler);
        router.post("/api/accounts").handler(this::getAllUsers);
        router.post("/api/login").handler(this::login);

        //restful url 这样声名
        router.route(HttpMethod.POST,"/api/:userId/hourses/:type").handler(this::findAllHouseByUserAndType);

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

    private void login(RoutingContext context){
        JsonObject user = context.getBodyAsJson();
        userService.rxFindUser(user.getString("username",null))
                .subscribe(res -> {
                    if(res != null && res.getString("password").equals(user.getString("password"))){
                        user.put("type",res.getValue("type"));
                        Session session = context.session();
                        session.put("user",user);
                        DataUtil.handler(res);
                        apiResponse(context,200,"data",res);
                    } else {
                        apiResponse(context,201,"data","用户名或密码不正确");
                    }
                },throwable -> apiFailure(context,throwable));
    }

    private void findAllHouseByType(RoutingContext context){
        JsonObject page = context.getBodyAsJson();
        int pageSize = page.getInteger("pageSize",10);
        int pageNumber = page.getInteger("pageNumber",0);
        String type = context.pathParam("type");
        houseService.rxFindAllHouseByType(pageSize,pageNumber,type)
                .zipWith(houseService.rxCountByType(type),(array,count) -> new JsonObject()
                        .put("data",array).put("totalCount",count))
                .subscribe(rs -> {
                    rs.put("pageSize",pageSize)
                            .put("pageNumber",pageNumber)
                            .put("msg","success");
                    apiResponse(context,200,"data",rs);
                },throwable -> apiFailure(context,throwable));
    }

    private void findAllHouseByUserAndType(RoutingContext context){
        JsonObject page = context.getBodyAsJson();
        int pageSize = page.getInteger("pageSize",10);
        int pageNumber = page.getInteger("pageNumber",0);
        String userId = context.pathParam("userId");
        String type = context.pathParam("type");
        houseService.rxFindAllHouseByUserAndType(pageSize,pageNumber,userId,type)
                .zipWith(houseService.rxCountByUserAndType(userId,type),(array,count) -> new JsonObject()
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
