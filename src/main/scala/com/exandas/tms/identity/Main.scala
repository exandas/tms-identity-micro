package com.exandas.tms.identity

/**
 * Created by kostas on 09/07/2015.
 */
object Main extends App {

  println("test".bcrypt)
  println("test".bcrypt(generateSalt))

  val salt = generateSalt
  val singleHash = "test".bcrypt(salt)
  val doubleHash = singleHash.bcrypt(salt)
  println(s"this is my salt: $salt")
  println(s"this is my single salrted: $singleHash")
  println(s"this is my double salted: $doubleHash")

  val identity = new com.exandas.tms.identity.IdentityProvider with InMemory
  identity.storeUser("kostas","mamalis")
  println(identity.getAll)
  println(identity.authenticate("kostas","mamalis"))
  println(identity.authenticate("kostas","mdamalis"))
}
