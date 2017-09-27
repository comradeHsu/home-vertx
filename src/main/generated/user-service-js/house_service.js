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

/** @module user-service-js/house_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JHouseService = Java.type('service.HouseService');

/**
 @class
*/
var HouseService = function(j_val) {

  var j_houseService = j_val;
  var that = this;

  /**

   @public
   @param pageSize {number} 
   @param pageNumber {number} 
   @param type {string} 
   @param resultHandler {function} 
   @return {HouseService}
   */
  this.findAllHouseByType = function(pageSize, pageNumber, type, resultHandler) {
    var __args = arguments;
    if (__args.length === 4 && typeof __args[0] ==='number' && typeof __args[1] ==='number' && typeof __args[2] === 'string' && typeof __args[3] === 'function') {
      j_houseService["findAllHouseByType(int,int,java.lang.String,io.vertx.core.Handler)"](pageSize, pageNumber, type, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_houseService;
};

HouseService._jclass = utils.getJavaClass("service.HouseService");
HouseService._jtype = {
  accept: function(obj) {
    return HouseService._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(HouseService.prototype, {});
    HouseService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
HouseService._create = function(jdel) {
  var obj = Object.create(HouseService.prototype, {});
  HouseService.apply(obj, arguments);
  return obj;
}
module.exports = HouseService;