package example

import cats.effect.{IO, IOApp}

object Hello extends IOApp.Simple {
  override def run: IO[Unit] =
    IO.println("hello")
}
