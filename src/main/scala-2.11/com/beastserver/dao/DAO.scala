package com.beastserver.dao

import slick.lifted.Rep

import scala.concurrent.Future

/**
*  debalid on 23.08.2015.
*/
trait DAO[Type, Identifier, PersistType]
{
  def getExactlyOne(id: Identifier): Future[Option[Type]]
  def getSequence(ids: Seq[Identifier]): Future[Seq[Type]]
  def getSequenceBy(where: Filter[PersistType]): Future[Seq[Type]]

  def insertOrUpdateOne(inst: Type): Future[Option[Type]]
  def insertOrUpdateAll(seq: Seq[Type]): Future[Seq[Type]]

  def delete(id: Identifier): Future[Option[Type]]
  def deleteAll(ids: Seq[Identifier]): Future[Seq[Type]]
  def deleteAllBy(where: Filter[PersistType]): Future[Seq[Type]]
}

trait Filter[PersistType] extends ((PersistType) => Rep[Boolean])
