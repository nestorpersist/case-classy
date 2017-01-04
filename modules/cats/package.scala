/* -
 * Case Classy [case-classy-cats]
 */

package classy

import _root_.cats._
import _root_.cats.data.Kleisli

import core._

package object cats {

  implicit class DecoderCatsOps[A, B](val decoder: Decoder[A, B]) extends AnyVal {

    /** A Kleisli arrow for this decoder's decode function */
    def kleisli: Kleisli[Either[DecodeError, ?], A, B] = Kleisli(decoder.decode)
  }

  implicit def decoderStdInstance[Z]: Monad[Decoder[Z, ?]] =
    new Monad[Decoder[Z, ?]] {

      override def map[A, B](a: Decoder[Z, A])(f: A => B): Decoder[Z, B] =
        a.map(f)

      def pure[A](x: A): Decoder[Z, A] =
        Decoder.const(x)

      def flatMap[A, B](fa: Decoder[Z, A])(f: A => Decoder[Z, B]): Decoder[Z, B] =
        fa.flatMap(f)

      // Currently *not* stack safe
      def tailRecM[A, B](a: A)(f: A => Decoder[Z, Either[A, B]]): Decoder[Z, B] =
        f(a).flatMap {
          case Left(a1) => tailRecM(a1)(f)
          case Right(b) => Decoder.const(b)
        }

    }

}