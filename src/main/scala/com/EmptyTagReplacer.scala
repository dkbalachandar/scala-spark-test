package com

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.Map

//A spark job is read set of XML files and replace empty middleName and countyName elements
object EmptyTagReplacer {

  def main(args: Array[String]) {

    if (args.length < 2) {
      println("Usage <Source File or Directory> <Destination File or Directory>")
    }
    val conf = new SparkConf().setAppName("EmptyTagReplacer")
    val sc = new SparkContext(conf)

    val inFile = args(0)
    val outFile = args(1)

    val input: Map[String, String] = sc.wholeTextFiles(inFile).collectAsMap()
    searchAndReplaceEmptyTags(sc, input, outFile)
    sc.stop()
  }

  def searchAndReplaceEmptyTags(sc: SparkContext, inputXml: Map[String, String], outFile: String):
  scala.collection.mutable.ListBuffer[String] = {

    var outputXml = new scala.collection.mutable.ListBuffer[String]()
    val htmlTags = List("<middleName/>", "<countyName/>")
    inputXml.foreach { case (fileName, content) =>
      var newContent = content
      for (tag <- htmlTags) {
       newContent = newContent.replace(newContent, "")
      }
      //Save the output if only if the outFile is not blank
      if (outFile.length > 0) {
        val data = sc.parallelize(newContent)
        data.saveAsTextFile(outFile + "/" + fileName)
      }
      outputXml += newContent
    }
    outputXml
  }

  def countTags(sc: SparkContext, xmlRecords: List[String]): List[Int] = {

    var middleNameTagCounter = sc.accumulator(0)
    var countyTagCounter = sc.accumulator(0)
    val middleNameRegex = "<middleName/>".r
    val countyRegEx = "<countyName/>".r
    xmlRecords.foreach { content =>
      middleNameTagCounter += middleNameRegex.findAllIn(content).length
      countyTagCounter += countyRegEx.findAllIn(content).length
    }
    List(middleNameTagCounter.value, countyTagCounter.value)
  }
}
