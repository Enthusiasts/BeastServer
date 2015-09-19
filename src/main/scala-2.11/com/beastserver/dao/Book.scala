package com.beastserver.dao

import com.beastserver.boot.Config

/**
 * debal on 19.09.2015.
 * Represents current projection of models collection.
 */
class Book[Type] (private val content: Seq[Type]) extends Config
{
  val fingerprint = java.util.UUID.randomUUID()

  def apply(page: Int, pageSize: Int = BeastDefaults.pageSize): (Seq[Type], Map[String, String]) = {
    require(page >= 0, pageSize >= 0)
    (content slice (page * pageSize, (page + 1) * pageSize), Map (
        "page-current" -> page.toString,
        "page-size" -> pageSize.toString,
        "length" -> content.length.toString,
        "fingerprint" -> fingerprint.toString
      ))
  }
}

object Book
{
  def apply[T](seq: Seq[T]) = new Book(seq)
}
