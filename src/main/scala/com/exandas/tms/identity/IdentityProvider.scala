package com.exandas.tms.identity

/**
 * Created by kostas on 09/07/2015.
 */
trait IdentityProvider {
  this : Memory =>
   def storeUser(username:String, password: String): Unit ={
      val salt = generateSalt
      val salted = password.bcrypt(salt)
      store(UserLoginInfo(username,salted,salt))
   }

   def authenticate(username:String, password:String): Boolean = {
     getAll.find(userInfo => {
       userInfo.username.equals(username)
     }) match {
       case Some(u) => {
         val salt = u.salt
         val realPass = u.password
         password.bcrypt(salt).equals(realPass)
       }
       case None => false
     }

   }
}

case class UserLoginInfo(username:String, password:String, salt:String)

trait Memory {
   def store(s:UserLoginInfo)
   def getAll : Seq[UserLoginInfo]

}

trait InMemory extends Memory {
  import scala.collection.mutable._
  var storeSeq : Seq[UserLoginInfo] = Seq()

  override def store(s: UserLoginInfo): Unit = {
    storeSeq = storeSeq.+:(s)
  }
  override def getAll = storeSeq

}
