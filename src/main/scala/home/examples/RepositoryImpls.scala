package home.examples

import cats.Id

import scala.concurrent.{ExecutionContext, Future}

class InMemoryPaymentService extends PaymentService[Id] {
  override def payFor(order: Order): Unit = {
    //done
  }
}

class AsynchPaymentService(implicit ec: ExecutionContext) extends PaymentService[Future] {
  override def payFor(order: Order): Future[Unit] = Future {
    //done
  }
}

class InMemoryOrderRepository extends OrderRepository[Id] {

  private var orders: Map[Int, Order] = Map.empty

  override def find(id: Int): Option[Order] = orders.get(id)

  override def save(order: Order): Unit = {
    orders = orders + (order.id → order)
  }
}

class AsynchOrderRepository(implicit ec: ExecutionContext) extends OrderRepository[Future] {

  private var orders: Map[Int, Order] = Map.empty

  override def find(id: Int): Future[Option[Order]] = Future.successful(orders.get(id))

  override def save(order: Order): Future[Unit] = Future {
    orders = orders + (order.id → order)
  }
}
