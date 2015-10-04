package com.beastserver.dao

import java.util.UUID

import com.beastserver.gen.Tables
import com.beastserver.util.NGPostgresCommonDAOMethods
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

/**
 * debal on 04.10.2015.
 */
class NGMediaCacheDAO(implicit val db: Database, implicit val exec: ExecutionContext)
  extends NGDAO[UUID, Tables.MediaCacheRow, SlickFilter[Tables.MediaCache]]
  with NGPostgresCommonDAOMethods[UUID, Tables.MediaCacheRow, Tables.MediaCache]
  with NGMediaCacheComponent
{
  lazy val filters = new NGMediaCacheFilters()

  override def readExactlyOne(id: UUID): Future[Option[Tables.MediaCacheRow]] = {
    db.run {
      for {
        mc <- matchMediaCache(id).result
      } yield mc.headOption
    }
  }

  override def createOne(inst: Tables.MediaCacheRow): Future[Option[Tables.MediaCacheRow]] = {
    db.run {
      for {
        mc <- insert(Seq(inst))
      } yield mc.headOption
    }
  }

  override def createAll(seq: Seq[Tables.MediaCacheRow]): Future[Seq[Tables.MediaCacheRow]] = {
    db.run {
      for {
        mc <- insert(seq)
      } yield mc
    }
  }

  override def delete(id: UUID): Future[Option[Tables.MediaCacheRow]] = {
    db.run {
      val matched = matchMediaCache(id)
      for {
        mc <- matched.result
        countAffected <- matched.delete
      } yield mc.headOption
    }
  }

  override protected def inside = filters.inside

  override def table: PostgresDriver.api.Query[Tables.MediaCache, Tables.MediaCacheRow, Seq] = Tables.MediaCache
}

final class NGMediaCacheFilters
{
  lazy val inside = (ids: Seq[UUID]) => new SlickFilter[Tables.MediaCache] {
    override def apply(inst: Tables.MediaCache) = inst.uuid.inSet(ids)
  }

  lazy val any = () => new SlickFilter[Tables.MediaCache] {
    override def apply(inst: Tables.MediaCache) = true
  }
}

protected sealed trait NGMediaCacheComponent
{
  private def matchMediaCacheForCompile(id: Rep[UUID]) = for {
    mc <- Tables.MediaCache if mc.uuid === id
  } yield mc

  protected lazy val matchMediaCache = Compiled {
    matchMediaCacheForCompile _
  }

  protected def insert(seq: Seq[Tables.MediaCacheRow]) = {
    (Tables.MediaCache returning Tables.MediaCache) ++= seq
  }
}