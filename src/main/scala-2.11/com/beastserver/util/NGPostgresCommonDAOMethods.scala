package com.beastserver.util

import com.beastserver.dao.{NGDAO, SlickFilter}
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

/**
 * debal on 04.10.2015.
 */
protected sealed trait NGPostgresCommonMatchers [Identifier, RowType, TableType <: Table[RowType]]
{
  def table: Query[TableType, RowType, Seq]

  protected final def matchAllBy(count: Int, filters: Seq[SlickFilter[TableType]]): Query[TableType, RowType, Seq] = {
    matchAllBy(filters) take count
  }

  protected final def matchAllBy(filters: Seq[SlickFilter[TableType]]): Query[TableType, RowType, Seq] = {
    require(filters.nonEmpty)
    for {
      uni <- table if filters.foldLeft(filters.head(uni))((x, y) => x && y(uni))
    } yield uni
  }
}

//Contains some very common operations with collections in DB.
trait NGPostgresCommonDAOMethods[Identifier, RowType, TableType <: Table[RowType]]
  extends NGPostgresCommonMatchers [Identifier, RowType, TableType]
{
  this: NGDAO[Identifier, RowType, SlickFilter[TableType]] =>

  implicit def db: Database
  implicit def exec: ExecutionContext

  //Filter that should make a decision is identifier of current row in presented set or not.
  protected def inside: (Seq[Identifier]) => SlickFilter[TableType]

  override final def readAll(ids: Seq[Identifier]): Future[Seq[RowType]] = {
    readAllBy(ids.length, Seq(inside(ids)))
  }

  override final def deleteAll(ids: Seq[Identifier]): Future[Seq[RowType]] = {
    deleteAllBy(Seq(inside(ids)))
  }

  override final def readAllBy(count: Int, where: Seq[SlickFilter[TableType]]): Future[Seq[RowType]] = {
    db.run {
      val matched = matchAllBy(where) take count
      for {
        uni <- matched.result
      } yield uni
    }
  }

  override final def deleteAllBy(where: Seq[SlickFilter[TableType]]): Future[Seq[RowType]] = {
    db.run {
      val matched = matchAllBy(where)
      for {
        uni <- matched.result
        countAffected <- matched.delete
      } yield uni
    }
  }
}
