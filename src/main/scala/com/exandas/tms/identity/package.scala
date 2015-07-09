package com.exandas.tms

import org.mindrot.jbcrypt.{BCrypt => B}

/**
 * @author Kostas Mamalis
 */
package object identity {
  implicit class Password(val pswrd: String) extends AnyVal {

    import org.mindrot.jbcrypt.BCrypt

    def bcrypt: String = B.hashpw(pswrd, BCrypt.gensalt())

    def bcrypt(rounds: Int): String = B.hashpw(pswrd, BCrypt.gensalt(rounds))

    def bcrypt(salt: String): String = B.hashpw(pswrd, salt)

    def isBcryptedWithCache(hash: String)(implicit cache: PasswordCache): Boolean = {
      val entry = PasswordCache.CacheEntry(pswrd, hash)
      cache.get(entry) match {
        case Some(value) => value
        case None =>
          val value = isBcrypted(hash)
          cache.put(entry, value)
          value
      }
    }

    def isBcrypted(hash: String): Boolean = B.checkpw(pswrd, hash)
  }

  def generateSalt: String = B.gensalt()

  object PasswordCache {

    import java.util.concurrent.TimeUnit

    case class CacheEntry(password: String, hash: String)

    implicit val Default = new ExpiringCache(1, TimeUnit.DAYS)
  }

  trait PasswordCache {
    import PasswordCache._

    def get(entry: CacheEntry): Option[Boolean]
    def put(entry: CacheEntry, value: Boolean): Option[Boolean]
  }

  import java.util.concurrent.TimeUnit

  import scala.collection.mutable

  class MappedCache
    extends mutable.HashMap[PasswordCache.CacheEntry, Boolean]
    with mutable.SynchronizedMap[PasswordCache.CacheEntry, Boolean]
    with PasswordCache

  class ExpiringCache(duration: Long, unit: TimeUnit, queryOverflow: Int = 1000) extends PasswordCache {

    import PasswordCache._

    case class ExpiringValue(value: Boolean, timestamp: Long)

    val map = new mutable.HashMap[CacheEntry, ExpiringValue]

    def get(key: CacheEntry) = synchronized {
      increaseQueryCount()
      maybeCleanExpired()
      map.get(key).map(_.value)
    }

    def put(entry: CacheEntry, value: Boolean) = synchronized {
      map.put(entry, ExpiringValue(value, currentMillis)).map(_.value)
    }

    def currentMillis = System.currentTimeMillis()

    private var queryCount = 0

    def increaseQueryCount() {
      queryCount = queryCount + 1
    }

    def maybeCleanExpired() {
      if (queryCount >= queryOverflow) cleanExpired()
    }

    def cleanExpired() {
      val expiration = unit.toMillis(duration)
      val current = currentMillis
      val expired = map.toList.collect {
        case (key, ExpiringValue(_, timestamp)) if (timestamp + expiration) <= current => key
      }
      expired.foreach(map.remove)
    }
  }
}
