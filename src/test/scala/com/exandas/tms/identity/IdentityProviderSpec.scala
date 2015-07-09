package com.exandas.tms.identity

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by kostas on 09/07/2015.
 */
class IdentityProviderSpec extends FlatSpec with Matchers{
   behavior of "IdentityProvider"


   it should "store a user" in {
      val identity = new IdentityProvider with InMemory
      identity.storeUser("kostas","mamalis")
      identity.storeUser("kostas12","mamalis12")
      assert(identity.getAll.size === 2)
   }

   it should "safeguard the password of a user" in {
      val identity = new IdentityProvider with InMemory
      identity.storeUser("kostas","mamalis")
      assert(identity.getAll.find(_.username.equals("kostas")).get.password !== "mamalis")
   }

   it should "allow a user when providing the right password" in {
      val identity = new IdentityProvider with InMemory
      identity.storeUser("kostas","mamalis")
      assert(identity.authenticate("kostas", "mamalis") === true)
   }


   it should "block a user when providing the right password" in {
      val identity = new IdentityProvider with InMemory
      identity.storeUser("kostas","mamalis")
      assert(identity.authenticate("kostas", "mamalis2") === false)
   }


}
