package scalaz

/** Endomorphisms have special properties among arrows, so are captured in this newtype.
  *
  * Endomorphic[Function1, A] is equivalent to Endo[A]
  */
final case class Endomorphic[=>:[_, _], A](run: A =>: A) {

  final def compose(that: Endomorphic[=>:, A])(implicit F: Compose[=>:]): Endomorphic[=>:, A] =
    Endomorphic[=>:, A](F.compose(run, that.run))

  final def andThen(that: Endomorphic[=>:, A])(implicit F: Compose[=>:]): Endomorphic[=>:, A] =
    that.compose(this)

}

object Endomorphic extends EndomorphicInstances with EndomorphicFunctions

trait EndomorphicFunctions {

  /** Endomorphic Kleisli arrow */
  final def endoKleisli[F[_]: Monad, A](f: A => F[A]): Endomorphic[({type λ[α, β] = Kleisli[F, α, β]})#λ, A] =
    Endomorphic[({type λ[α, β] = Kleisli[F, α, β]})#λ, A](Kleisli(f))
}

sealed abstract class EndomorphicInstances extends EndomorphicInstances0 {

  // for binary compatibility
  def endomorphicMonoid[=>:[_, _], A](implicit G: Category[=>:]): Monoid[Endomorphic[=>:, A]] =
    new Monoid[Endomorphic[=>:, A]] with EndomorphicSemigroup[=>:, A] {
      val F = G
      def zero: Endomorphic[=>:, A] = Endomorphic(G.id)
    }

}

sealed abstract class EndomorphicInstances0 extends EndomorphicInstances1 {

  implicit def kleisliEndoInstance[F[_]: Monad, A]: Monoid[Endomorphic[({type λ[α, β] = Kleisli[F, α, β]})#λ, A]] =
    Endomorphic.endomorphicMonoid[({type λ[α, β] = Kleisli[F, α, β]})#λ, A]

  implicit def cokleisliEndoInstance[F[_]: Comonad, A]: Monoid[Endomorphic[({type λ[α, β] = Cokleisli[F, α, β]})#λ, A]] =
    Endomorphic.endomorphicMonoid[({type λ[α, β] = Cokleisli[F, α, β]})#λ, A]

}

sealed abstract class EndomorphicInstances1 extends EndomorphicInstances2 {

  // for binary compatibility
  def endomorphicSemigroup[=>:[_, _], A](implicit G: Compose[=>:]): Semigroup[Endomorphic[=>:, A]] =
    new EndomorphicSemigroup[=>:, A] {
      val F = G
    }

}

sealed abstract class EndomorphicInstances2 extends EndomorphicInstances3 {

  implicit def kleisliEndoSemigroup[F[_]: Bind, A]: Semigroup[Endomorphic[({type λ[α, β] = Kleisli[F, α, β]})#λ, A]] =
    Endomorphic.endomorphicSemigroup[({type λ[α, β] = Kleisli[F, α, β]})#λ, A]

  implicit def cokleisliEndoSemigroup[F[_]: Cobind, A]: Semigroup[Endomorphic[({type λ[α, β] = Cokleisli[F, α, β]})#λ, A]] =
    Endomorphic.endomorphicSemigroup[({type λ[α, β] = Cokleisli[F, α, β]})#λ, A]

}

sealed abstract class EndomorphicInstances3 extends EndomorphicInstances4 {
  implicit def endomorphicMonoid0[=>:[_, _], A](implicit G: Category[=>:]): Monoid[Endomorphic[=>:, A]] =
    new Monoid[Endomorphic[=>:, A]] with EndomorphicSemigroup[=>:, A] {
      val F = G
      def zero: Endomorphic[=>:, A] = Endomorphic(G.id)
    }
}

sealed abstract class EndomorphicInstances4 {
  implicit def endomorphicSemigroup0[=>:[_, _], A](implicit G: Compose[=>:]): Semigroup[Endomorphic[=>:, A]] =
    new EndomorphicSemigroup[=>:, A] {
      val F = G
    }
}

private trait EndomorphicSemigroup[=>:[_, _], A] extends Semigroup[Endomorphic[=>:, A]] {
  implicit def F: Compose[=>:]
  def append(f1: Endomorphic[=>:, A], f2: => Endomorphic[=>:, A]) = Endomorphic(F.compose(f1.run, f2.run))
}
