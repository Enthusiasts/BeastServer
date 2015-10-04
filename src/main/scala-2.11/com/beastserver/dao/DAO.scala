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

trait NGDAO[Identifier, PersistType, F <: NGFilter]
{
  def readExactlyOne(id: Identifier): Future[Option[PersistType]]
  def readAll(ids: Seq[Identifier]): Future[Seq[PersistType]]
  def readAllBy(count: Int, where: Seq[F]): Future[Seq[PersistType]]

  def createOne(inst: PersistType): Future[Option[PersistType]]
  def createAll(seq: Seq[PersistType]): Future[Seq[PersistType]]

  def delete(id: Identifier): Future[Option[PersistType]]
  def deleteAll(ids: Seq[Identifier]): Future[Seq[PersistType]]
  def deleteAllBy(where: Seq[F]): Future[Seq[PersistType]]
}

trait Filter[Type] extends ((Type) => Rep[Boolean])

trait NGFilter
trait SlickFilter[TableType] extends NGFilter with ((TableType) => Rep[Boolean])