package home.examples

import cats.Id

import scala.concurrent.Future

object Main extends App {

  {
    val service: OrderServiceImpl[Id] =
      new OrderServiceImpl(new InMemoryPaymentService, new InMemoryOrderRepository)
    service.save(Order(0))
    val p: Option[Order] = service.find(0)
  }

  {
    import cats.instances.future._
    import scala.concurrent.ExecutionContext.Implicits.global
    val service: OrderServiceImpl[Future] =
      new OrderServiceImpl(new AsynchPaymentService, new AsynchOrderRepository)

    val p: Future[Option[Order]] = service.find(0)
  }

}
