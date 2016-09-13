package se.bitsplz.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import se.bitsplz.cms.exception.CmsException;


@Provider
public class NotFoundMapper implements ExceptionMapper<CmsException>{
   
   public Response toResponse(CmsException exception){
      return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
      // return Response.status(Status.NOT_FOUND).build();
   }
   
}
