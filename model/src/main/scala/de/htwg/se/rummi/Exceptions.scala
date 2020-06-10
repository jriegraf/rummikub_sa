package de.htwg.se.rummi

final case class AlreadyDrawnException(private val message: String = "",
                                       private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

final case class FieldIsOccupiedException(private val message: String = "",
                                          private val cause: Throwable = None.orNull)
  extends Exception(message, cause)


final case class InvalidGameStateException(private val message: String = "",
                                          private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

final case class FieldNotValidException(private val message: String = "",
                                           private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

