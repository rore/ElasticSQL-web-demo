package com.ElasticSQL.web

import akka.config.Supervision._
import akka.actor.Supervisor
import akka.actor.Actor._
import cc.spray._

class Boot {
  
  val mainModule = new HTMLService with APIService {
    // bake your module cake here
  }

  val htmlService = actorOf(new HttpService(mainModule.htmlService))
  val apiService = actorOf(new HttpService(mainModule.apiService))
  val rootService = actorOf(new RootService(htmlService,apiService))
 
  Supervisor(
    SupervisorConfig(
      OneForOneStrategy(List(classOf[Exception]), 3, 100),
      List(
        Supervise(htmlService, Permanent),
        Supervise(apiService, Permanent),
        Supervise(rootService, Permanent)
      )
    )
  )
}