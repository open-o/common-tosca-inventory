/**
 * Copyright  2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.commontosca.inventory.sdk.api;

public class InventoryException extends RuntimeException {

  private static final long serialVersionUID = -1412740989925771127L;
  private Object[] params;
  private String message;

  public InventoryException() {
    super();
  }

  public InventoryException(String message) {
    super(message);
  }

  public InventoryException(String messagePattern, Object... params) {
    super(messagePattern);
    this.params = params;
  }

  public InventoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public InventoryException(String messagePattern, Throwable cause, Object... params) {
    super(messagePattern, cause);
    this.params = params;
  }

  public InventoryException(Throwable cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    if (this.message == null) {
      this.message = super.getMessage();
      if (this.message == null) {
        this.message = "Exception without message";
      } else if (this.params != null && this.params.length > 0 && this.message != null) {
        this.message = String.format(this.message, this.params);
      }
    }
    return this.message;
  }

}
