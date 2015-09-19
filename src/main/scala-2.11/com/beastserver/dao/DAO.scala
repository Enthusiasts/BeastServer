package com.beastserver.dao

import slick.lifted.Rep

import scala.concurrent.Future

/**
*  debalid on 23.08.2015.
*/
trait DAO[Type, Identifier, PersistType]
{
  def getExactlyOne(id: Identifier): Future[Option[Type]]
  def getSequence(ids: Seq[Identifier]): Future[Book[Type]]
  def getSequenceBy(count: Int, where: Seq[Filter[PersistType]]): Future[Book[Type]]

  def insertOrUpdateOne(inst: Type): Future[Option[Type]]
  def insertOrUpdateAll(seq: Seq[Type]): Future[Book[Type]]

  def delete(id: Identifier): Future[Option[Type]]
  def deleteAll(ids: Seq[Identifier]): Future[Book[Type]]
  def deleteAllBy(where: Seq[Filter[PersistType]]): Future[Book[Type]]
}

trait Filter[PersistType] extends ((PersistType) => Rep[Boolean])
