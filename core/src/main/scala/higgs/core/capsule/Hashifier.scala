package higgs.core.capsule

import scala.reflect._

trait Hashifier[T <: LeafCapsule[T]] extends HashBasicImplicits {
  def hashAny: Hash[Any]
  final def hash(a: Any) = hashAny.hash(a)
}

abstract class Hash[T : ClassTag] { self =>

  val TClazz = implicitly[ClassTag[T]].runtimeClass

  def can(a: Any) = TClazz isInstance a

  def hash(a: T): Int

  def ::[B1 >: T : ClassTag, B <: B1](another: Hash[B]): Hash[B1] =
    new Hash[B1] {
      override def can(a: Any) = self.can(a) || another.can(a)
      def hash(a: B1) = {
        if(self.can(a)) self.hash(a.asInstanceOf[T])
        else if(another.can(a)) another.hash(a.asInstanceOf[B])
        else sys.error("no hash defined for")
      }
    }

}

trait HashBasicImplicits {

  implicit def toHashAny[T](a: Hash[T]) = a.asInstanceOf[Hash[Any]]

  implicit val intH = new Hash[Int] {
    def hash(a: Int) = a
  }

  implicit val longH = new Hash[Long] {
    def hash(a: Long) = a.hashCode
  }

}

object Hash {

  def tuple1[T1, T2](implicit t1hash: Hash[T1], t2hash: Hash[T2]) =
    new Hash[(T1, T2)] {
      def hash(t: (T1, T2)) = t1hash.hash(t._1) * 31 + t2hash.hash(t._2)
    }

  def by[For : ClassTag, By](f: For => By)(implicit byHash: Hash[By]) =
    new Hash[For] {
      def hash(a: For): Int = byHash.hash(f(a))
    }

}