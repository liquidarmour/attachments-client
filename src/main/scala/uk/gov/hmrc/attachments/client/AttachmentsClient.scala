/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.attachments.client

import play.api.libs.iteratee.Enumerator
import uk.gov.hmrc.attachments.client.api.{UploadFileResponse, NewFileRequest, NewFileResponse}
import uk.gov.hmrc.domain.CtUtr
import uk.gov.hmrc.play.audit.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.streaming.StreamingPUT
import uk.gov.hmrc.play.http.ws.{WSGet, WSPost, WSPut}
import uk.gov.hmrc.play.http.{HttpGet, HttpPost, HttpPut}

import scala.concurrent._

trait AttachmentsClient {

  self: ServicesConfig =>

  def applicationName: String

  lazy val serviceURL: String = baseUrl("attachments")

  def http: HttpGet with HttpPost with StreamingPUT = new WSGet with WSPost with StreamingPUT {

    override def auditConnector: AuditConnector = AuditConnector(LoadAuditingConfig(key = "auditing"))

    override def appName: String = applicationName

  }

  def prepareForFileUpload(utr: CtUtr, newFileRequest: NewFileRequest)(implicit hc: HeaderCarrier, executionContext: ExecutionContext): Future[NewFileResponse] =
    http.POST[NewFileRequest, NewFileResponse](url = s"$serviceURL/ct/$utr/file", body = newFileRequest)

  def uploadAttachment(utr: CtUtr, fileId: String, file: Enumerator[Array[Byte]], fileName: String, contentType: Option[String])(implicit hc: HeaderCarrier, executionContext: ExecutionContext): Future[UploadFileResponse] = {
    import uk.gov.hmrc.play.http.writes._
    http.PUT[Enumerator[Array[Byte]], UploadFileResponse](s"$serviceURL/ct/${utr.value}/file/$fileId", body = file)
  }

//      .map { resp =>
//      Json.fromJson[NewFileResponse](resp.json).getOrElse(throw new IllegalStateException(s"The call to create the new file failed to return a correctly formatted response."))
//    }
//
//    val url = s"$serviceURL/ct/${utr.value}/file/$fileId"
//    multiPartClient.url(url)
//      .withParts(StringPart("name", fileName), FilePart("file", file, contentType.getOrElse("application/text"), "UTF-8")).withMethod("POST")
//      .execute().map(response => is2xx(response.status) match {
//      case true => readJson[UploadFileResponse](url, response.json, "POST")
//      case false => throw new Exception(s"error POSTing to attachments-store, status '${response.status}, body '${response.body}")
//    }
//      ).recover {
//      case e: ExecutionException => e.getCause match {
//        case e: TimeoutException => throw new BadGatewayException(s"POST of '$url' timed out with message '${e.getMessage}'")
//        case e: ConnectException => throw new BadGatewayException(s"POST of '$url' failed. Caused by: '${e.getMessage}'")
//        case e: Exception => throw new BadGatewayException(s"POST of '$url' failed. Caused by: '${e.getMessage}'")
//      }
//    }
//  }
//
//
//  def getAttachmentLink(id: String): String
//
//  def getAttachment(utr: CtUtr, id: String)(implicit hc: HeaderCarrier, requestAnyRef: Request[AnyRef]): Future[Either[Attachment, Int] with Product with Serializable] = {
//    val url = s"$serviceURL/ct/${utr.value}/file/$id"
//    wsClient.url(url).getStream().map {
//      case (response, body) =>
//        if (response.status == 200) {
//          val contentType = response.headers.get("Content-Type").flatMap(_.headOption).getOrElse("application/octet-stream")
//          response.headers.get("Content-Length") match {
//            case Some(Seq(length)) =>
//              Left(Attachment(contentType, Option(length), body))
//            case _ =>
//              Left(Attachment(contentType, None, body))
//          }
//        } else Right(response.status)
//    }
//  }
//
//
//  def removeAttachmentLink(id: String): String
//
//  def removeAttachment(userId: String, id: String)(implicit hc: HeaderCarrier, requestAnyRef: Request[AnyRef]): Future[Int] = {
//    val uri = removeAttachmentLink(id)
//
//    wsClient.url(uri).delete().map {
//      case (response) => response.status
//    }
//  }

//
//  def setAttachmentStateToPermanentLink(id: String): String
//
//
//  def setAttachmentStateToPermanent(id: String)(implicit hc: HeaderCarrier, requestAnyRef: Request[AnyRef]): Future[Boolean] = {
//    val uri = setAttachmentStateToPermanentLink(id)
//    wsClient.url(uri).put(Json.obj()).map { response => is2xx(response.status) match {
//      case true => true
//      case false => throw new Exception(s"error changing state. status '${response.status}, body '${response.body}")
//    }
//    }
//  }

//
//  private def is2xx(status: Int) = status >= 200 && status <= 299
//
//
//  def readJson[A](url: String, jsValue: JsValue, httpVerb: String)(implicit rds: Reads[A], mf: Manifest[A], hc: HeaderCarrier) = {
//    jsValue.validate[A].fold(
//      errs => throw new JsValidationException(httpVerb, url, Json.stringify(jsValue), mf.getClass, errs),
//      valid => valid)
//  }
}
