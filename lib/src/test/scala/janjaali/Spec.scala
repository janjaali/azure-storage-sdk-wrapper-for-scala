package janjaali

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

trait Spec extends AnyFreeSpec with Matchers with ScalaCheckDrivenPropertyChecks
