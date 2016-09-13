package se.bitsplz.web;

import static se.bitsplz.loader.ContextLoader.getBean;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.bitsplz.cms.exception.CmsException;
import se.bitsplz.cms.model.UserData;
import se.bitsplz.cms.service.CmsService;
import se.bitsplz.exception.UserWebException;
import se.bitsplz.model.UserWeb;


@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UserService{
   
   private static final AtomicInteger ids = new AtomicInteger(1000);
   
   @Context
   private UriInfo uriInfo;
   
   
   @POST
   public Response createSUser(UserWeb userWeb) throws CmsException{
      Integer maxMysqlId = getBean(CmsService.class).findUserByMaxUserId();
      if(maxMysqlId != null){
         ids.getAndSet(maxMysqlId + 1);
      }
      int id = ids.getAndIncrement();
      UserData userData = new UserData(id, userWeb.getUsername(), userWeb.getFirstName(), userWeb.getLastName(), userWeb.getUserStatus());
      UserData savedUser = getBean(CmsService.class).saveUser(userData);
      URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedUser.getUserId())).build();
      
      return Response.created(location).build();
   }
   
   
   @GET
   @Path("{userId}")
   public Response getUser(@PathParam("userId") int userId){
      UserData userData = getBean(CmsService.class).findUserByUserId(userId);
      if(userData == null){
         throw new UserWebException("user with id " + userId);
      }
      return Response.ok(userToWeb(userData)).build();
   }
   
   
   @GET
   public Response getAllUsers(){
      Collection<UserData> result = (Collection<UserData>)getBean(CmsService.class).findAllUsers();
      Collection<UserWeb> users = new ArrayList();
      result.forEach(u -> users.add(userToWeb(u)));
      // GenericEntity<Collection<UserWeb>> userWebEntity = new GenericEntity<Collection<UserWeb>>(users);
      
      return Response.ok(users).build();
   }
   
   
   @PUT
   @Path("{userId}/{teamId}")
   public Response addUserToTeam(@PathParam("userId") int userId, @PathParam("teamId") int teamId) throws CmsException{
      getBean(CmsService.class).addUserToTeam(teamId, userId);
      return Response.noContent().build();
   }
   
   
   @DELETE
   @Path("{userId}")
   public Response deleteUser(@PathParam("userId") int userId) throws CmsException{
      getBean(CmsService.class).deleteUser(userId);
      return Response.noContent().build();
   }
   
   
   @GET
   @Path("{enquiry}/{variable}")
   public Response getBasedOnEnquiryAndVariable(@PathParam("enquiry") String enquiry, @PathParam("variable") String variable){
      Collection<UserData> userResult = null;
      Collection<UserWeb> users = new ArrayList();
      switch(enquiry){
         case "firstName":
            userResult = getBean(CmsService.class).findUserByFirstName(variable);
            userResult.forEach(u -> users.add(userToWeb(u)));
            if(users.isEmpty()){
               throw new WebApplicationException(Status.NOT_FOUND);
            }
            return Response.ok(users).build();
         case "lastName":
            userResult = getBean(CmsService.class).findUserByLastName(variable);
            userResult.forEach(u -> users.add(userToWeb(u)));
            if(users.isEmpty()){
               throw new WebApplicationException(Status.NOT_FOUND);
            }
            return Response.ok(users).build();
         case "username":
            userResult = getBean(CmsService.class).findUserByUsername(variable);
            userResult.forEach(u -> users.add(userToWeb(u)));
            if(users.isEmpty()){
               throw new WebApplicationException(Status.NOT_FOUND);
            }
            return Response.ok(users).build();
         default:
            return Response.status(Status.BAD_REQUEST).build();
      }
   }
   
   
   private UserWeb userToWeb(UserData userData){
      return new UserWeb(userData.getId(), userData.getUserId(), userData.getUsername(), userData.getFirstName(), userData.getLastName(), userData.getUserStatus());
   }
   
}