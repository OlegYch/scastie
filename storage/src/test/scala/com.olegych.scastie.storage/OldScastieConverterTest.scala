package com.olegych.scastie.storage

import com.olegych.scastie.util.ScastieFileUtil.slurp
import com.olegych.scastie.api._

import java.nio.file._

import org.scalatest.FunSuite

class OldScastieConverterTest extends FunSuite {

  test("convert-simple") {
    val original =
      """|/***
         |scalaVersion := "2.11.8"
         |scalacOptions ++= Seq(""-deprecation", "-feature")
         |*/
         |
         |object Main extends App {
         |  println("Hello, World!")
         |}""".stripMargin

    val obtained =
      OldScastieConverter.convertOldInput(original)

    val expected =
      Inputs.default.copy(
        target = ScalaTarget.Jvm("2.11.8"),
        sbtConfigExtra =
          """scalacOptions ++= Seq(""-deprecation", "-feature")""",
        code = """|object Main extends App {
                  |  println("Hello, World!")
                  |}""".stripMargin,
        isWorksheetMode = false
      )

    assert(obtained == expected)
  }

  test("convert") {
    val path = Paths.get("storage", "src", "test", "resources")
    val original = slurp(path.resolve("convert-test.scala")).get

    val expected =
      Inputs.default.copy(
        sbtConfigExtra = slurp(path.resolve("config.sbt")).get,
        target = ScalaTarget.Typelevel("2.11.8"),
        code = slurp(path.resolve("code.scala")).get,
        isWorksheetMode = false
      )

    val obtained = OldScastieConverter.convertOldInput(original)

    assert(obtained.target == expected.target)
    assert(obtained.sbtConfigExtra == expected.sbtConfigExtra)
    assert(obtained == expected)
  }
}
