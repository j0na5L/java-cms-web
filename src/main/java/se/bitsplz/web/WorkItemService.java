package se.bitsplz.web;

import static se.bitsplz.loader.ContextLoader.getBean;

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
import se.bitsplz.cms.model.WorkItemData;
import se.bitsplz.cms.model.WorkItemData.WorkItemStatus;
import se.bitsplz.cms.service.CmsService;
import se.bitsplz.model.WorkItemWeb;


@Path("/workItems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WorkItemService{
   
   private static final AtomicInteger ids = new AtomicInteger(3000);
   
   @Context
   UriInfo uriInfo;
   
   
   @POST
   public Response createWorkItem(WorkItemData workItem){
      Integer maxMysqlId = getBean(CmsService.class).findWorkItemByMaxWorkItemId();
      if(maxMysqlId != null){
         ids.getAndSet(maxMysqlId + 1);
      }
      int id = ids.getAndIncrement();
      workItem.setWorkItemId(id);
      
      getBean(CmsService.class).saveWorkItem(workItem);
      return Response.status(Status.CREATED).header("Location", "workItems/" + id).build();
   }
   
   
   @GET
   @Path("{workItemId}")
   public Response getWorkItem(@PathParam("workItemId") int workItemId){
      WorkItemData workItem = getBean(CmsService.class).findWorkItemById(workItemId);
      
      if(workItem == null){
         throw new WebApplicationException(Status.NOT_FOUND);
      }
      return Response.ok(workItem).build();
   }
   
   
   @GET
   @Path("{userId}/user")
   public Response getUsersWorkItems(@PathParam("userId") int userId){
      Collection<WorkItemData> result = getBean(CmsService.class).findAllWorkItemsByUser(userId);
      Collection<WorkItemWeb> workItems = new ArrayList();
      result.forEach(w -> workItems.add(workItemToWeb(w)));
      if(workItems.isEmpty()){
         throw new WebApplicationException(Status.NOT_FOUND);
      }
      return Response.ok(workItems).build();
   }
   
   
   @GET
   @Path("{enquiry}/{variable}")
   public Response getWorkItemsBasedOnEnquiry(@PathParam("enquiry") String enquiry, @PathParam("variable") String variable){
      Collection<WorkItemData> result = null;
      Collection<WorkItemWeb> workItems = new ArrayList();
      
      switch(enquiry){
         case "status":
            result = getBean(CmsService.class).findAllWorkItemsByStatus(WorkItemStatus.valueOf(variable));
            result.forEach(w -> workItems.add(workItemToWeb(w)));
            break;
         case "description":
            result = getBean(CmsService.class).findWorkItemsByDescription(variable);
            result.forEach(w -> workItems.add(workItemToWeb(w)));
            break;
         default:
            return Response.status(Status.BAD_REQUEST).build();
      }
      if(workItems.isEmpty()){
         throw new WebApplicationException(Status.NOT_FOUND);
      }
      return Response.ok(workItems).build();
   }
   
   
   @GET
   public Response getAllWorkItems(){
      Collection<WorkItemData> result = (Collection<WorkItemData>)getBean(CmsService.class).findAllWorkItems();
      Collection<WorkItemWeb> workItems = new ArrayList();
      result.forEach(w -> workItems.add(workItemToWeb(w)));
      
      return Response.ok(workItems).build();
   }
   
   
   @PUT
   @Path("{workItemId}")
   public Response updateWorkItem(@PathParam("workItemId") int workItemId, WorkItemData inputWorkItem) throws CmsException{
      getBean(CmsService.class).updateWorkItem(workItemId, inputWorkItem);
      return Response.noContent().build();
   }
   
   
   @PUT
   @Path("{workItemId}/{type}/{id}")
   public Response addWorkItemToSpecificTypesId(@PathParam("workItemId") int workItemId, @PathParam("type") String type, @PathParam("id") int id) throws CmsException{
      switch(type){
         case "user":
            getBean(CmsService.class).assignWorkItem(workItemId, id);
            break;
         case "team":
            getBean(CmsService.class).addWorkItemToTeam(id, workItemId);
            break;
         default:
            return Response.status(Status.BAD_REQUEST).build();
      }
      return Response.noContent().build();
   }
   
   
   @DELETE
   @Path("{workItemId}")
   public Response deleteWorkItem(@PathParam("workItemId") int workItemId) throws CmsException{
      getBean(CmsService.class).removeWorkItem(workItemId);
      return Response.noContent().build();
   }
   
   
   private WorkItemWeb workItemToWeb(WorkItemData workItemData){
      return new WorkItemWeb(workItemData.getId(), workItemData.getWorkItemId(), workItemData.getDescription(), workItemData.getWorkItemStatus());
   }
}
