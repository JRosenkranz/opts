package org.jrosenkranz.opts.parse_functions

import java.io.{File => JFile}

import scala.io.Source
import scala.reflect.io.File

/**
  * a utility for a set of frequently used parsers
  */
object Parsers {

  /**
    * @return a parser where given a string denoting a file path, return all lines of the file as an iterator
    */
  def fileLines: String => Iterator[String] = {
    s => Source.fromFile(s).getLines()
  }

  /**
    * @return a parser where given a string denoting a file path, return a [[File]] object
    */
  def file: String => File = {
    s => new File(new JFile(s))
  }

  /**
    * @return a parser where given a string, return an [[Int]]
    */
  def int: String => Int = {
    s => s.toInt
  }

  /**
    * @return a parser where given a string, return a [[Long]]
    */
  def long: String => Long = {
    s => s.toLong
  }

  /**
    * @return a parser where given a string, return a [[Double]]
    */
  def double: String => Double = {
    s => s.toDouble
  }

}
