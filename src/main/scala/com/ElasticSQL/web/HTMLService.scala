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

trait HTMLService extends Directives {
  
  val htmlService = {
  	pathPrefix("ElasticSQL-web-demo"){
    path("") {  
    	getResource("main");
      } ~
      path("/?([^./]+)[.]htm".r){ rest =>
      	if (null == rest || rest.isEmpty)
      		_.fail(NotFound);
      	else{
      		getResource(rest);
      	}
      		
    }
  }
 }
 
 def getResource(path:String):Route = {
	  var filename = path;
	  if (filename.startsWith("/")) filename = filename.substring(1);
      getFromResource2(path + ".htm");
 }
  
 def getFromResource2(resourceName: String, charset: Option[HttpCharset] = None,
                      resolver: ContentTypeResolver = DefaultContentTypeResolver): Route = {
      get { ctx =>
        responseFromResource2(resourceName, charset) match {
          case Some(response) => ctx.complete(response)
          case None => ctx.fail(NotFound) // reject without specific rejection => same as unmatched "path" directive
        }
      }
  }
  
  /**
* Builds an HttpResponse from the content of the given classpath resource. If the resource cannot be read a
* "404 NotFound" response is returned. Note that this method is using disk IO which may block the current thread.
*/
  def responseFromResource2(resourceName: String, charset: Option[HttpCharset] = None,
                           resolver: ContentTypeResolver = DefaultContentTypeResolver): Option[HttpResponse] = {
    Option(getClass.getClassLoader.getResource(resourceName)).flatMap { resource =>
      val urlConn = resource.openConnection()
      val inputStream = urlConn.getInputStream
      val response = responseFromBuffer(
        lastModified = DateTime(math.min(urlConn.getLastModified, System.currentTimeMillis())),
        buffer = FileUtils.readAllBytes(inputStream),
        contentType = resolver(resourceName, charset)
      )
      inputStream.close()
      response
    }
  }
  
  private def responseFromBuffer(lastModified: DateTime, buffer: Array[Byte],
                                 contentType: => ContentType): Option[HttpResponse] = {
    Option(buffer).map { buffer =>
      HttpResponse(OK, List(`Last-Modified`(lastModified)), HttpContent(contentType, buffer))
    }
  }


  
}