package higgs.core.result

import scala.concurrent.{Future, ExecutionContext}

import play.api.libs.json._
import play.api.mvc.Controller

sealed trait JsonResult

case class JsonSuccess(jsObj: JsObject) extends JsonResult
case class JsonError(jsObj: JsObject, statusCode: Int) extends JsonResult


trait HttpResponseImplicits { self: Controller =>

  implicit class ToHttpResponse(res: Future[JsonResult])(implicit ec: ExecutionContext) {

    def toHttpResponse = res map {
      case JsonSuccess(obj)     => Ok(obj)
      case JsonError(obj, code) => toHttpErrorResponse(obj, code)
    }

    private def toHttpErrorResponse(obj: JsObject, code: Int) = code match {
      case BAD_REQUEST           => BadRequest(obj)
      case INTERNAL_SERVER_ERROR => InternalServerError(obj)
    }

  }

}