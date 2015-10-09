package higgs.core.capsule

import neutrino.core.user._

import higgs.core.patterns._, Chain._


case class CompositeCapsule(capsule1: Capsule, capsule2: Capsule, userId: UserId) extends Capsule {

  private[capsule] val channel = {
    val combined =
      for(ch1 <- capsule1.channel; ch2 <- capsule2.channel) yield (ch1 + ch2)(userId)
    combined orElse capsule1.channel orElse capsule2.channel
  }

  val handleRequest = capsule1.handleRequest +> capsule2.handleRequest

}