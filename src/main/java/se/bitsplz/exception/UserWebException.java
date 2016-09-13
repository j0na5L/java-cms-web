package se.bitsplz.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


public class UserWebException extends WebApplicationException{
   
   private static final long serialVersionUID = -1781199883737476971L;
   
   
   public UserWebException(String message){
      super(Response.status(Status.NOT_FOUND).entity(message).build());
   }
}
