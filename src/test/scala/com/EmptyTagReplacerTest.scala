package com

import java.io.File

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.commons.io.FileUtils
import org.scalatest.FunSuite
import collection.mutable.Map

//import scala.io.Source._

class EmptyTagReplacerTest extends FunSuite with SharedSparkContext {


  test("Empty HTML tag replacer test") {

    //Read the content and create a content Map.
    //val content: String = scala.io.Source.fromFile("./src/test/resources/text-files/xml1").mkString
    val content: String =  FileUtils.readFileToString(new File("./src/test/resources/text-files/xml1"), "UTF-8")

    println("content"+content)
    val contentMap = collection.mutable.Map[String, String]()
    contentMap.+=("fileName" -> content)
    //Call searchAndReplaceMethod to remove empty Nodes
    val outputContent: scala.collection.mutable.ListBuffer[String] = EmptyTagReplacer.searchAndReplaceEmptyTags(sc, contentMap, "")
    val counts: List[Int] = EmptyTagReplacer.countTags(sc, outputContent.toList)
    println(counts)
    val expected = List(0, 0)
    assert(counts == expected)
  }
}
