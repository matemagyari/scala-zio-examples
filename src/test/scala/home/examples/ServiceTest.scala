package home.examples

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future

class ServiceTest extends FlatSpec with Matchers with ScalaFutures {

  "Asynch" should "work" in {
    import cats.instances.future._
    import scala.concurrent.ExecutionContext.Implicits.global
    val service: OrderServiceImpl[Future] =
      new OrderServiceImpl(new AsynchPaymentService, new AsynchOrderRepository)

    val order = Order(0)

    service.save(order).futureValue shouldBe Right()

    service.find(0).futureValue shouldBe Some(order)

  }

  "Synch" should "work" in {
    import cats.Id
    val service: OrderServiceImpl[Id] =
      new OrderServiceImpl(new InMemoryPaymentService, new InMemoryOrderRepository)
    val order = Order(0)

    service.save(order) shouldBe Right()

    service.find(0) shouldBe Some(order)
  }
}
