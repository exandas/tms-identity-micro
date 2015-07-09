import akka.event.NoLogging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Flow
import org.scalatest._

class ServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with Service {
  override def testConfigSource = "akka.loglevel = WARNING"
  override def config = testConfig
  override val logger = NoLogging

  val userInfo = UserLoginInfo("kostas","temp")
  val userInfo2 = UserLoginInfo("jim","temp")


  "Service" should "respond to a single user query" in {
    Get(s"/user/${userInfo.username}") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[UserLoginInfo] shouldBe userInfo
    }

    Get(s"/user/${userInfo2.username}") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[UserLoginInfo] shouldBe userInfo2
    }
  }


}
