package io.github.jrosenkranz.opts.java_api.options

object OptBuilder {
  def defaultOpt[T](key: String, defaultValue: T): DefaultOpt[Any] = {
    DefaultOpt(key,defaultValue)
  }

  def flagOpt(key: String): FlagOpt = {
    FlagOpt(key)
  }

  def requiredOpt[T](key: String): RequiredOpt[Any] = {
    RequiredOpt(key)
  }

  def commandOpt[T](key: String): CommandOpt = {
    CommandOpt(key)
  }

}
