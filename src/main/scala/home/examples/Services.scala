package home.examples

import cats.Monad
import cats.data.EitherT
import cats.syntax.all._

case class Order(id: Int)

trait OrderRepository[F[_]] {
  def find(id: Int): F[Option[Order]]
  def save(order: Order): F[Unit]
}

trait PaymentService[F[_]] {
  def payFor(order: Order): F[Unit]
}

sealed trait OrderError
case class OrderAlreadyExists(id: Int) extends OrderError
case class OrderDoesNotExist(id: Int) extends OrderError

class OrderServiceImpl[F[_]: Monad](
    paymentService: PaymentService[F],
    repository: OrderRepository[F]) //extends OrderService[F]
{
  def find(id: Int): F[Option[Order]] = repository.find(id)

  //demonstrates flatMap on the abstract Monad type and creating a new instance of it with 'pure'
  def save(order: Order): F[Either[OrderError, Unit]] = find(order.id).flatMap {
    case None =>
      (for {
        _ ← repository.save(order)
        _ ← paymentService.payFor(order)
      } yield ()).map(_.asRight)

    case Some(existingOrder) =>
      Monad[F].pure(OrderAlreadyExists(existingOrder.id).asLeft)
  }

  //demonstrates using for comprehension for combining multiple function calls with Monad return types
  def save(p1: Order, p2: Order): F[Either[OrderError, Unit]] = {

    val zero: F[Either[OrderError, Unit]] = Monad[F].pure(Right())
    List(p1, p2)
      .foldLeft(zero) { (acc, p) =>
        (for {
          _ <- EitherT(acc)
          _ <- EitherT(save(p))
        } yield {}).value
      }

    for {
      _ <- EitherT(save(p1))
      _ <- EitherT(save(p2))
    } yield {}
  }.value

}
