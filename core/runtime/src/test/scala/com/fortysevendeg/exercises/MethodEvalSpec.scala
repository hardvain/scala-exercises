/*
 * scala-exercises-runtime
 * Copyright (C) 2015-2016 47 Degrees, LLC. <http://www.47deg.com>
 */

package com.fortysevendeg.exercises

import org.scalatest._

import cats.data.Xor

class MethodEvalSpec extends FunSpec with Matchers {

  import MethodEval._

  describe("runtime evaluation") {

    val methodEval = new MethodEval()

    it("fails when incorrect parameter types are provided") {
      val res = methodEval.eval(
        "com.fortysevendeg.exercises.ExampleTarget.intStringMethod",
        "\"hello\"" :: "\"world\"" :: Nil
      )
      assert(!res.didRun)
      assert(res.toSuccessXor.isLeft)
      assert(res.toExecutionXor.isLeft)
    }

    it("fails when too many parameters are provided") {
      val res = methodEval.eval(
        "com.fortysevendeg.exercises.ExampleTarget.intStringMethod",
        "\"hello\"" :: "\"world\"" :: "1" :: Nil
      )
      assert(!res.didRun)
      assert(res.toSuccessXor.isLeft)
      assert(res.toExecutionXor.isLeft)
    }

    it("works when the parameters are appropriate") {
      val res = methodEval.eval(
        "com.fortysevendeg.exercises.ExampleTarget.intStringMethod",
        "1 + 2" :: "\"world\"" :: Nil
      )

      res should equal(EvaluationSuccess[String]("3world"))
      assert(res.toSuccessXor.isRight)
      assert(res.toExecutionXor.isRight)
    }

    it("captures exceptions thrown by the called method") {
      val res = methodEval.eval(
        "com.fortysevendeg.exercises.ExampleTarget.throwsExceptionMethod",
        Nil
      )

      res should matchPattern {
        case EvaluationException(_: ExampleTarget.ExampleException) ⇒
      }
      assert(res.toSuccessXor.isLeft)
      assert(res.toExecutionXor.isRight)
    }

  }
}

object ExampleTarget {
  def intStringMethod(a: Int, b: String): String = {
    s"$a$b"
  }

  class ExampleException extends Exception("this is an example exception")

  def throwsExceptionMethod() {
    throw new ExampleException
  }
}