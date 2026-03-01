package com.wms.common.exception;

/** Exception thrown when an entity is not found. */
public class EntityNotFoundException extends WmsException {
  private static final String ERROR_CODE = "ENTITY_NOT_FOUND";
  private static final int HTTP_STATUS = 404;

  /**
   * Constructs an EntityNotFoundException with the specified entity name and ID.
   *
   * @param entityName the name of the entity that was not found
   * @param id the ID of the entity
   */
  public EntityNotFoundException(String entityName, Object id) {
    super(ERROR_CODE, HTTP_STATUS, String.format("%s not found with id: %s", entityName, id));
  }

  /**
   * Constructs an EntityNotFoundException with a custom message.
   *
   * @param message the detail message
   */
  public EntityNotFoundException(String message) {
    super(ERROR_CODE, HTTP_STATUS, message);
  }
}
