package com.beastserver.dao
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema = Array(Comment.schema, Course.schema, CourseTeacher.schema, File.schema, Mime.schema, Teacher.schema, TeacherFile.schema, University.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Comment
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param text Database column text SqlType(varchar), Length(140,true), Default(None)
   *  @param author Database column author SqlType(varchar), Length(35,true), Default(None)
   *  @param datetime Database column datetime SqlType(timestamp), Default(None)
   *  @param fileId Database column file_id SqlType(int4) */
  case class CommentRow(id: Int, text: Option[String] = None, author: Option[String] = None, datetime: Option[java.sql.Timestamp] = None, fileId: Int)
  /** GetResult implicit for fetching CommentRow objects using plain SQL queries */
  implicit def GetResultCommentRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]]): GR[CommentRow] = GR{
    prs => import prs._
    CommentRow.tupled((<<[Int], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<[Int]))
  }
  /** Table description of table comment. Objects of this class serve as prototypes for rows in queries. */
  class Comment(_tableTag: Tag) extends Table[CommentRow](_tableTag, "comment") {
    def * = (id, text, author, datetime, fileId) <> (CommentRow.tupled, CommentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), text, author, datetime, Rep.Some(fileId)).shaped.<>({r=>import r._; _1.map(_=> CommentRow.tupled((_1.get, _2, _3, _4, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column text SqlType(varchar), Length(140,true), Default(None) */
    val text: Rep[Option[String]] = column[Option[String]]("text", O.Length(140,varying=true), O.Default(None))
    /** Database column author SqlType(varchar), Length(35,true), Default(None) */
    val author: Rep[Option[String]] = column[Option[String]]("author", O.Length(35,varying=true), O.Default(None))
    /** Database column datetime SqlType(timestamp), Default(None) */
    val datetime: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("datetime", O.Default(None))
    /** Database column file_id SqlType(int4) */
    val fileId: Rep[Int] = column[Int]("file_id")

    /** Foreign key referencing File (database name fk_comment) */
    lazy val fileFk = foreignKey("fk_comment", fileId, File)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Comment */
  lazy val Comment = new TableQuery(tag => new Comment(tag))

  /** Entity class storing rows of table Course
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param title Database column title SqlType(varchar), Length(20,true), Default(None)
   *  @param universityId Database column university_id SqlType(int4) */
  case class CourseRow(id: Int, title: Option[String] = None, universityId: Int)
  /** GetResult implicit for fetching CourseRow objects using plain SQL queries */
  implicit def GetResultCourseRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[CourseRow] = GR{
    prs => import prs._
    CourseRow.tupled((<<[Int], <<?[String], <<[Int]))
  }
  /** Table description of table course. Objects of this class serve as prototypes for rows in queries. */
  class Course(_tableTag: Tag) extends Table[CourseRow](_tableTag, "course") {
    def * = (id, title, universityId) <> (CourseRow.tupled, CourseRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, Rep.Some(universityId)).shaped.<>({r=>import r._; _1.map(_=> CourseRow.tupled((_1.get, _2, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column title SqlType(varchar), Length(20,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(20,varying=true), O.Default(None))
    /** Database column university_id SqlType(int4) */
    val universityId: Rep[Int] = column[Int]("university_id")

    /** Foreign key referencing University (database name fk_course) */
    lazy val universityFk = foreignKey("fk_course", universityId, University)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Course */
  lazy val Course = new TableQuery(tag => new Course(tag))

  /** Entity class storing rows of table CourseTeacher
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param courseId Database column course_id SqlType(int4), Default(None)
   *  @param teacherId Database column teacher_id SqlType(int4), Default(None) */
  case class CourseTeacherRow(id: Int, courseId: Option[Int] = None, teacherId: Option[Int] = None)
  /** GetResult implicit for fetching CourseTeacherRow objects using plain SQL queries */
  implicit def GetResultCourseTeacherRow(implicit e0: GR[Int], e1: GR[Option[Int]]): GR[CourseTeacherRow] = GR{
    prs => import prs._
    CourseTeacherRow.tupled((<<[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table course_teacher. Objects of this class serve as prototypes for rows in queries. */
  class CourseTeacher(_tableTag: Tag) extends Table[CourseTeacherRow](_tableTag, "course_teacher") {
    def * = (id, courseId, teacherId) <> (CourseTeacherRow.tupled, CourseTeacherRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), courseId, teacherId).shaped.<>({r=>import r._; _1.map(_=> CourseTeacherRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column course_id SqlType(int4), Default(None) */
    val courseId: Rep[Option[Int]] = column[Option[Int]]("course_id", O.Default(None))
    /** Database column teacher_id SqlType(int4), Default(None) */
    val teacherId: Rep[Option[Int]] = column[Option[Int]]("teacher_id", O.Default(None))

    /** Foreign key referencing Course (database name fk_course_teacher) */
    lazy val courseFk = foreignKey("fk_course_teacher", courseId, Course)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Teacher (database name fk_course_teacher_0) */
    lazy val teacherFk = foreignKey("fk_course_teacher_0", teacherId, Teacher)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table CourseTeacher */
  lazy val CourseTeacher = new TableQuery(tag => new CourseTeacher(tag))

  /** Entity class storing rows of table File
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(20,true), Default(None)
   *  @param datetime Database column datetime SqlType(timestamp), Default(None)
   *  @param content Database column content SqlType(bytea), Default(None)
   *  @param description Database column description SqlType(varchar), Length(140,true), Default(None)
   *  @param courseId Database column course_id SqlType(int4)
   *  @param taskfileId Database column taskfile_id SqlType(int4), Default(None)
   *  @param mimeId Database column mime_id SqlType(int4) */
  case class FileRow(id: Int, name: Option[String] = None, datetime: Option[java.sql.Timestamp] = None, content: Option[Array[Byte]] = None, description: Option[String] = None, courseId: Int, taskfileId: Option[Int] = None, mimeId: Int)
  /** GetResult implicit for fetching FileRow objects using plain SQL queries */
  implicit def GetResultFileRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[Array[Byte]]], e4: GR[Option[Int]]): GR[FileRow] = GR{
    prs => import prs._
    FileRow.tupled((<<[Int], <<?[String], <<?[java.sql.Timestamp], <<?[Array[Byte]], <<?[String], <<[Int], <<?[Int], <<[Int]))
  }
  /** Table description of table file. Objects of this class serve as prototypes for rows in queries. */
  class File(_tableTag: Tag) extends Table[FileRow](_tableTag, "file") {
    def * = (id, name, datetime, content, description, courseId, taskfileId, mimeId) <> (FileRow.tupled, FileRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), name, datetime, content, description, Rep.Some(courseId), taskfileId, Rep.Some(mimeId)).shaped.<>({r=>import r._; _1.map(_=> FileRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(20,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(20,varying=true), O.Default(None))
    /** Database column datetime SqlType(timestamp), Default(None) */
    val datetime: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("datetime", O.Default(None))
    /** Database column content SqlType(bytea), Default(None) */
    val content: Rep[Option[Array[Byte]]] = column[Option[Array[Byte]]]("content", O.Default(None))
    /** Database column description SqlType(varchar), Length(140,true), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(140,varying=true), O.Default(None))
    /** Database column course_id SqlType(int4) */
    val courseId: Rep[Int] = column[Int]("course_id")
    /** Database column taskfile_id SqlType(int4), Default(None) */
    val taskfileId: Rep[Option[Int]] = column[Option[Int]]("taskfile_id", O.Default(None))
    /** Database column mime_id SqlType(int4) */
    val mimeId: Rep[Int] = column[Int]("mime_id")

    /** Foreign key referencing Course (database name fk_file) */
    lazy val courseFk = foreignKey("fk_file", courseId, Course)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing File (database name fk_file_file) */
    lazy val fileFk = foreignKey("fk_file_file", taskfileId, File)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Mime (database name fk_file_0) */
    lazy val mimeFk = foreignKey("fk_file_0", mimeId, Mime)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table File */
  lazy val File = new TableQuery(tag => new File(tag))

  /** Entity class storing rows of table Mime
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param mimeType Database column mime_type SqlType(int4) */
  case class MimeRow(id: Int, mimeType: Int)
  /** GetResult implicit for fetching MimeRow objects using plain SQL queries */
  implicit def GetResultMimeRow(implicit e0: GR[Int]): GR[MimeRow] = GR{
    prs => import prs._
    MimeRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table mime. Objects of this class serve as prototypes for rows in queries. */
  class Mime(_tableTag: Tag) extends Table[MimeRow](_tableTag, "mime") {
    def * = (id, mimeType) <> (MimeRow.tupled, MimeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(mimeType)).shaped.<>({r=>import r._; _1.map(_=> MimeRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column mime_type SqlType(int4) */
    val mimeType: Rep[Int] = column[Int]("mime_type")
  }
  /** Collection-like TableQuery object for table Mime */
  lazy val Mime = new TableQuery(tag => new Mime(tag))

  /** Entity class storing rows of table Teacher
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param fullname Database column fullname SqlType(varchar), Length(35,true), Default(None) */
  case class TeacherRow(id: Int, fullname: Option[String] = None)
  /** GetResult implicit for fetching TeacherRow objects using plain SQL queries */
  implicit def GetResultTeacherRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[TeacherRow] = GR{
    prs => import prs._
    TeacherRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table teacher. Objects of this class serve as prototypes for rows in queries. */
  class Teacher(_tableTag: Tag) extends Table[TeacherRow](_tableTag, "teacher") {
    def * = (id, fullname) <> (TeacherRow.tupled, TeacherRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), fullname).shaped.<>({r=>import r._; _1.map(_=> TeacherRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column fullname SqlType(varchar), Length(35,true), Default(None) */
    val fullname: Rep[Option[String]] = column[Option[String]]("fullname", O.Length(35,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Teacher */
  lazy val Teacher = new TableQuery(tag => new Teacher(tag))

  /** Entity class storing rows of table TeacherFile
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param teacherId Database column teacher_id SqlType(int4), Default(None)
   *  @param fileId Database column file_id SqlType(int4), Default(None) */
  case class TeacherFileRow(id: Int, teacherId: Option[Int] = None, fileId: Option[Int] = None)
  /** GetResult implicit for fetching TeacherFileRow objects using plain SQL queries */
  implicit def GetResultTeacherFileRow(implicit e0: GR[Int], e1: GR[Option[Int]]): GR[TeacherFileRow] = GR{
    prs => import prs._
    TeacherFileRow.tupled((<<[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table teacher_file. Objects of this class serve as prototypes for rows in queries. */
  class TeacherFile(_tableTag: Tag) extends Table[TeacherFileRow](_tableTag, "teacher_file") {
    def * = (id, teacherId, fileId) <> (TeacherFileRow.tupled, TeacherFileRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), teacherId, fileId).shaped.<>({r=>import r._; _1.map(_=> TeacherFileRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column teacher_id SqlType(int4), Default(None) */
    val teacherId: Rep[Option[Int]] = column[Option[Int]]("teacher_id", O.Default(None))
    /** Database column file_id SqlType(int4), Default(None) */
    val fileId: Rep[Option[Int]] = column[Option[Int]]("file_id", O.Default(None))

    /** Foreign key referencing File (database name fk_teacher_file_0) */
    lazy val fileFk = foreignKey("fk_teacher_file_0", fileId, File)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Teacher (database name fk_teacher_file) */
    lazy val teacherFk = foreignKey("fk_teacher_file", teacherId, Teacher)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table TeacherFile */
  lazy val TeacherFile = new TableQuery(tag => new TeacherFile(tag))

  /** Entity class storing rows of table University
   *  @param title Database column title SqlType(varchar), Length(20,true), Default(None)
   *  @param id Database column id SqlType(int4), PrimaryKey */
  case class UniversityRow(title: Option[String] = None, id: Int)
  /** GetResult implicit for fetching UniversityRow objects using plain SQL queries */
  implicit def GetResultUniversityRow(implicit e0: GR[Option[String]], e1: GR[Int]): GR[UniversityRow] = GR{
    prs => import prs._
    UniversityRow.tupled((<<?[String], <<[Int]))
  }
  /** Table description of table university. Objects of this class serve as prototypes for rows in queries. */
  class University(_tableTag: Tag) extends Table[UniversityRow](_tableTag, "university") {
    def * = (title, id) <> (UniversityRow.tupled, UniversityRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (title, Rep.Some(id)).shaped.<>({r=>import r._; _2.map(_=> UniversityRow.tupled((_1, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column title SqlType(varchar), Length(20,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(20,varying=true), O.Default(None))
    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table University */
  lazy val University = new TableQuery(tag => new University(tag))
}
