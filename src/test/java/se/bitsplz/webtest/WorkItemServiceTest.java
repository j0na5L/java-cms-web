package se.bitsplz.webtest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.bitsplz.cms.model.TeamData;
import se.bitsplz.cms.model.WorkItemData;
import se.bitsplz.cms.model.WorkItemData.WorkItemStatus;
import se.bitsplz.provider.TeamProvider;
import se.bitsplz.provider.WorkItemProvider;


public class WorkItemServiceTest{
   
   private static final String workItemUrl = "http://localhost:8080/web-cms/workItems";
   private static final String teamUrl = "http://localhost:8080/web-cms/teams";
   
   private static Client client;
   
   private static WorkItemData workItem1, workItem2, workItem3;
   
   static GenericType<List<WorkItemData>> workItemType = new GenericType<List<WorkItemData>>(){
   };
   
   
   @BeforeClass
   public static void initTest(){
      client = ClientBuilder.newClient().register(WorkItemProvider.class).register(TeamProvider.class);
      
      workItem1 = new WorkItemData(-1, "Clean my room");
      workItem2 = new WorkItemData(-1, "Feed the cat");
      workItem3 = new WorkItemData(-1, "Fix the computor");
      
      client.target(workItemUrl).request().post(Entity.entity(workItem1, MediaType.APPLICATION_JSON), WorkItemData.class);
      client.target(workItemUrl).request().post(Entity.entity(workItem2, MediaType.APPLICATION_JSON), WorkItemData.class);
      client.target(workItemUrl).request().post(Entity.entity(workItem3, MediaType.APPLICATION_JSON), WorkItemData.class);
   }
   
   
   @Before
   public void setUp(){
      workItem1 = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3000).request().get(WorkItemData.class);
      workItem2 = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3001).request().get(WorkItemData.class);
      workItem3 = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3002).request().get(WorkItemData.class);
   }
   
   
   @Test
   public void canGetWorkItemById(){
      WorkItemData persistedWorkItem = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3000).request().get(WorkItemData.class);
      assertThat(persistedWorkItem, equalTo(workItem1));
   }
   
   
   @Test
   public void canGetWorkItemBasedOnDescription(){
      List<WorkItemData> persistedWorkItems = client.target(workItemUrl).path("/{enquiry}/{variable}").resolveTemplate("enquiry", "description").resolveTemplate("variable", "feed").request().get(workItemType);
      
      assertThat(persistedWorkItems.size(), equalTo(1));
      assertThat(persistedWorkItems.get(0), equalTo(workItem2));
   }
   
   // based on team.
   
   // based on user
   
   
   //
   
   @Test
   public void canAddWorkItemToTeam(){
      TeamData team = new TeamData(2000, "AwsomeTeam");
      client.target(teamUrl).request().post(Entity.entity(team, MediaType.APPLICATION_JSON), TeamData.class);
      
      TeamData persistedTeam = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2000).request().get(TeamData.class);
      assertThat(team, equalTo(persistedTeam));
      
      WorkItemData workItemToAdd = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3000).request().get(WorkItemData.class);
      workItemToAdd.setTeam(team);
      
      client.target(workItemUrl).path("/{workItemId}/{type}/{id}").resolveTemplate("workItemId", 3000).resolveTemplate("type", "team").resolveTemplate("id", 2000).request().put(Entity.entity(workItemToAdd, MediaType.APPLICATION_JSON), WorkItemData.class);
      
      WorkItemData persistedWorkItem = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3000).request().get(WorkItemData.class);
      assertThat(persistedWorkItem, equalTo(workItemToAdd));
   }
   
   
   @Test
   public void canUpdateWorkItemStatus(){
      WorkItemData updatedWorkItem = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3002).request().get(WorkItemData.class);
      updatedWorkItem.setWorkItemStatus(WorkItemStatus.STARTED);
      
      client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3002).request().put(Entity.entity(updatedWorkItem, MediaType.APPLICATION_JSON), WorkItemData.class);
      
      WorkItemData persistedWorkItem = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3002).request().get(WorkItemData.class);
      assertThat(persistedWorkItem, equalTo(updatedWorkItem));
   }
   
   
   @Test
   public void canRemoveWorkItem(){
      WorkItemData workItem = new WorkItemData(3003, "Fix the sink");
      client.target(workItemUrl).request().post(Entity.entity(workItem, MediaType.APPLICATION_JSON), WorkItemData.class);
      
      WorkItemData persistedWorkItem = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3003).request().get(WorkItemData.class);
      assertThat(persistedWorkItem, is(equalTo(workItem)));
      
      WorkItemData deletedWorkItem = client.target(workItemUrl).path("/{workItemId}").resolveTemplate("workItemId", 3003).request().delete(WorkItemData.class);
      assertThat(deletedWorkItem, equalTo(null));
   }
}