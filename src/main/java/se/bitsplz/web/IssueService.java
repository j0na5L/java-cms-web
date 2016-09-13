package se.bitsplz.web;

import static se.bitsplz.loader.ContextLoader.getBean;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.bitsplz.cms.exception.CmsException;
import se.bitsplz.cms.model.IssueData;
import se.bitsplz.cms.model.WorkItemData;
import se.bitsplz.cms.service.CmsService;


@Path("/issues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueService{
   
   @Context
   UriInfo uriInfo;
   
   
   @POST
   @Path("{workItemId}")
   public Response addIssueToWorkItem(@PathParam("workItemId") int workItemId, IssueData issue) throws CmsException{
      getBean(CmsService.class).createIssue(issue, workItemId);
      return Response.status(Status.CREATED).header("Location", "workItems/" + workItemId).build();
   }
   
   
   @GET
   public Response getAllWorkItemsWithAnIssue(){
      Collection<WorkItemData> workItemResult = getBean(CmsService.class).returnAllWorkItemsWithAnIssue();
      GenericEntity<Collection<WorkItemData>> workItemEntity = new GenericEntity<Collection<WorkItemData>>(workItemResult){
      };
      return Response.ok(workItemEntity).build();
      // return workItemResult;
   }
   
   
   @PUT
   @Path("{workItemId}")
   public Response updateIssue(@PathParam("workItemId") int workItemId, IssueData issue) throws CmsException{
      getBean(CmsService.class).updateIssue(workItemId, issue);
      return Response.noContent().build();
   }
   
}