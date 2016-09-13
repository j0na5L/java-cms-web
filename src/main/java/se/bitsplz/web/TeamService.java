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
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.transaction.annotation.Transactional;

import se.bitsplz.cms.exception.CmsException;
import se.bitsplz.cms.model.TeamData;
import se.bitsplz.cms.model.UserData;
import se.bitsplz.cms.model.WorkItemData;
import se.bitsplz.cms.service.CmsService;
import se.bitsplz.exception.TeamNotFoundException;
import se.bitsplz.model.TeamWeb;
import se.bitsplz.model.UserWeb;
import se.bitsplz.model.WorkItemWeb;


@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class TeamService{
   
   private static final AtomicInteger ids = new AtomicInteger(2000);
   @Context
   private UriInfo uriInfo;
   
   
   @POST
   public Response createTeam(TeamWeb teamWeb) throws CmsException{
      Integer maxMysqlId = getBean(CmsService.class).findTeamByMaxTeamId();
      if(maxMysqlId != null){
         ids.getAndSet(maxMysqlId + 1);
      }
      int id = ids.getAndIncrement();
      TeamData teamData = new TeamData(id, teamWeb.getTeamName());
      TeamData savedTeam = getBean(CmsService.class).saveTeam(teamData);
      URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedTeam.getTeamId())).build();
      
      return Response.created(location).build();
   }
   
   
   @GET
   public Collection<TeamWeb> getAllTeams(){
      Collection<TeamData> result = (Collection<TeamData>)getBean(CmsService.class).findAllTeams();
      Collection<TeamWeb> teams = new ArrayList();
      result.forEach(u -> teams.add(teamToWeb(u)));
      
      return teams;
   }
   
   
   private TeamWeb teamToWeb(TeamData teamWeb){
      return new TeamWeb(teamWeb.getId(), teamWeb.getTeamId(), teamWeb.getTeamName(), teamWeb.getStatus());
   }
   
   
   // @POST
   // public Response createTeam(TeamData team){
   // Integer maxMysqlId = getBean(CmsService.class).findTeamByMaxTeamId();
   // if(maxMysqlId != null){
   // ids.getAndSet(maxMysqlId + 1);
   // }
   // int id = ids.getAndIncrement();
   // team.setTeamId(id);
   //
   // getBean(CmsService.class).saveTeam(team);
   // return Response.status(Status.CREATED).header("Location", "teams/" + id).build();
   // }
   
   @GET
   @Path("{teamId}")
   public Response getTeam(@PathParam("teamId") int teamId){
      TeamData team = getBean(CmsService.class).findTeamByTeamId(teamId);
      if(team == null){
         throw new TeamNotFoundException(teamId);
      }
      return Response.ok(team).build();
   }
   
   
   @GET
   @Path("{teamId}/{enquiry}")
   public Response makeAnEnquiryInTheSecifikTeam(@PathParam("enquiry") String enquiry, @PathParam("teamId") int teamId){
      Collection<UserData> userResult = null;
      GenericEntity<Collection<UserData>> userEntity = null;
      
      Collection<WorkItemData> workItemResult = null;
      GenericEntity<Collection<WorkItemData>> workItemEntity = null;
      
      switch(enquiry){
         case "users":
            userResult = getBean(CmsService.class).returnAllUsersInTeam(teamId);
            userEntity = new GenericEntity<Collection<UserData>>(userResult){
            };
         case "workItems":
            workItemResult = getBean(CmsService.class).findAllWorkItemsInTeam(teamId);
            workItemEntity = new GenericEntity<Collection<WorkItemData>>(workItemResult){
            };
            break;
         default:
            return Response.status(Status.BAD_REQUEST).build();
      }
      
      if(userResult == null){
         if(workItemResult != null){
            return Response.ok(workItemEntity).build();
         }
         throw new WebApplicationException(Status.NOT_FOUND);
      }
      return Response.ok(userEntity).build();
   }
   
   
   @GET
   @Path("{id}/user")
   public Response getAllUsersForTeam(@PathParam("id") int teamId){
      Collection<UserData> result = getBean(CmsService.class).returnAllUsersInTeam(teamId);
      Collection<UserWeb> users = new ArrayList();
      result.forEach(u -> users.add(new UserWeb(u.getId(), u.getUserId(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getUserStatus())));
      if(users.isEmpty()){
         throw new WebApplicationException(Status.NOT_FOUND);
      }
      return Response.ok(users).build();
   }
   
   
   @GET
   @Path("{id}/workItems")
   public Response getAllWorkItemsInTeam(@PathParam("id") int teamId){
      Collection<WorkItemData> result = getBean(CmsService.class).findAllWorkItemsInTeam(teamId);
      Collection<WorkItemWeb> workItems = new ArrayList();
      result.forEach(w -> workItems.add(new WorkItemWeb(w.getId(), w.getWorkItemId(), w.getDescription(), w.getWorkItemStatus())));
      if(workItems.isEmpty()){
         throw new WebApplicationException(Status.NOT_FOUND);
      }
      
      return Response.ok(workItems).build();
   }
   
   
   // @GET
   // public Response getAllTeams(){
   // Collection<TeamData> teams = (Collection<TeamData>)getBean(CmsService.class).findAllTeams();
   // GenericEntity<Collection<TeamData>> entity = new GenericEntity<Collection<TeamData>>(teams){
   // };
   // return Response.ok(entity).build();
   // }
   
   @PUT
   @Path("{teamId}")
   public Response updateTeam(@PathParam("teamId") int teamId, TeamData inputTeam) throws CmsException{
      getBean(CmsService.class).updateTeam(teamId, inputTeam);
      return Response.noContent().build();
   }
   
   
   @DELETE
   @Path("{teamId}")
   @Transactional
   public Response deleteTeam(@PathParam("teamId") int teamId) throws CmsException{
      getBean(CmsService.class).deleteTeam(teamId);
      return Response.noContent().build();
   }
}
