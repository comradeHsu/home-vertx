/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package service.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rxjava.RxGen(service.HouseService.class)
public class HouseService {

  public static final io.vertx.lang.rxjava.TypeArg<HouseService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new HouseService((service.HouseService) obj),
    HouseService::getDelegate
  );

  private final service.HouseService delegate;
  
  public HouseService(service.HouseService delegate) {
    this.delegate = delegate;
  }

  public service.HouseService getDelegate() {
    return delegate;
  }

  public HouseService findAllHouseByType(int pageSize, int pageNumber, String type, Handler<AsyncResult<JsonArray>> resultHandler) { 
    delegate.findAllHouseByType(pageSize, pageNumber, type, resultHandler);
    return this;
  }

  public Single<JsonArray> rxFindAllHouseByType(int pageSize, int pageNumber, String type) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      findAllHouseByType(pageSize, pageNumber, type, fut);
    }));
  }


  public static  HouseService newInstance(service.HouseService arg) {
    return arg != null ? new HouseService(arg) : null;
  }
}
