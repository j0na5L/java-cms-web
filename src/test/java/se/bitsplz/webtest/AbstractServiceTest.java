package se.bitsplz.webtest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import se.bitsplz.cms.model.TeamData;
import se.bitsplz.cms.model.UserData;
import se.bitsplz.cms.model.WorkItemData;
import se.bitsplz.cms.model.UserData.UserStatus;
import se.bitsplz.provider.TeamProvider;
import se.bitsplz.provider.UserProvider;
import se.bitsplz.provider.WorkItemProvider;


public class AbstractServiceTest{
   
   protected static final String userUrl = "http://localhost:8080/web-cms/users";
   protected static final String teamUrl = "http://localhost:8080/web-cms/teams";
   protected static final String workItemUrl = "http://localhost:8080/web-cms/workItems";
   
   protected static Client client;
   
   protected static UserData user1, user2;
   protected static WorkItemData workItem1, workItem2;
   protected static TeamData team1, team2;
   
   
   @BeforeClass
   public static void initializeTests(){
      client = ClientBuilder.newClient().register(WorkItemProvider.class).register(TeamProvider.class).register(UserProvider.class);
      
      user1 = new UserData(-1, "Kattmamman007", "Katt", "Mamma", UserStatus.ACTIVE);
      user2 = new UserData(-1, "Hundpappan007", "Hund", "Pappa", UserStatus.ACTIVE);
      workItem1 = new WorkItemData(-1, "Klappa katten");
      workItem2 = new WorkItemData(-1, "Klappa hunden");
      
      client.target(userUrl).request().post(Entity.entity(user1, MediaType.APPLICATION_JSON), UserData.class);
      client.target(userUrl).request().post(Entity.entity(user2, MediaType.APPLICATION_JSON), UserData.class);
      
      team1 = new TeamData(-1, "AwesomeTeam");
      team2 = new TeamData(-1, "DreamTeam");
      
      client.target(teamUrl).request().post(Entity.entity(team1, MediaType.APPLICATION_JSON), TeamData.class);
      client.target(teamUrl).request().post(Entity.entity(team2, MediaType.APPLICATION_JSON), TeamData.class);
      
      client.target(workItemUrl).request().post(Entity.entity(workItem1, MediaType.APPLICATION_JSON), WorkItemData.class);
      client.target(workItemUrl).request().post(Entity.entity(workItem2, MediaType.APPLICATION_JSON), WorkItemData.class);
      
   }
   
   
   @AfterClass
   public static void afterClass(){
      client.close();
   }
}