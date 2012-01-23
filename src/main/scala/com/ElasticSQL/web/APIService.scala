package com.ElasticSQL.web

import cc.spray._
import cc.spray.http._
import http.MediaTypes._
import utils.ActorHelpers._
import java.util.concurrent.TimeUnit
import akka.actor.{Scheduler, Actor, Kill}
import directives._
import org.parboiled.common.FileUtils
import StatusCodes._
import HttpHeaders._
import com.ElasticSQL._

trait APIService extends Directives {
  
  val apiService = {
  	pathPrefix("ElasticSQL-web-demo" / "api"){
    path("search") {
    	get {
    		parameters("ip"?, "sql"?) { (ip, sql) =>
    			ctx => {
    				val s = sql;
    				ctx.complete("ok");
    			}
    		}
    	} ~
    	post { 
    		formFields("ip"?, "sql"?) { (ip, sql) =>
    			 try{
    				 search(ip.getOrElse(null), sql.getOrElse(null));
	    			 jsonpWithParameter("callback") {
	    				 _.complete(HttpContent(`application/json`, """{ "res": "ok" }"""))
	    			 }
    			 }
    			 catch{
    				 case e => {
		    			 jsonpWithParameter("callback") {
		    				 _.complete(HttpContent(`application/json`, """{ "res": "error", "msg": "%s"  }""".format(e.toString())))
		    			 }
    				 }
    			 }
    		}
    	}  
    }
  }
  }
  
  protected def search(ip:String, sql:String){
  	if (null == ip || ip.isEmpty) throw new Exception("no ip");
  	if (null == sql || sql.isEmpty) throw new Exception("no sql");
  }
  
  
}