package se.bitsplz.webtest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.bitsplz.cms.model.TeamData;
import se.bitsplz.provider.TeamProvider;


public class TeamServiceTest{
   
   private static final String userUrl = "http://localhost:8080/web-cms/users";
   private static final String teamUrl = "http://localhost:8080/web-cms/teams";
   private static final String workItemUrl = "http://localhost:8080/web-cms/workItems";
   private static WebTarget target;
   private static Client client;
   private TeamData team1, team2;
   private static TeamData newTeam1, newTeam2;
   private static GenericType<List<TeamData>> teamType = new GenericType<List<TeamData>>(){
   };
   
   
   @BeforeClass
   public static void initializeTests(){
      client = ClientBuilder.newBuilder().build();
      target = client.target(teamUrl).register(TeamProvider.class);
      newTeam1 = new TeamData(-1, "AwsomeTeam");
      newTeam2 = new TeamData(-1, "DreamTeam");
      
      client.target(teamUrl).request().post(Entity.entity(newTeam1, MediaType.APPLICATION_JSON), TeamData.class);
      client.target(teamUrl).request().post(Entity.entity(newTeam2, MediaType.APPLICATION_JSON), TeamData.class);
      
   }
   
   
   @Before
   public void setUpMethods(){
      this.team1 = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2000).request().get(TeamData.class);
      this.team2 = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2001).request().get(TeamData.class);
   }
   
   
   @Test
   public void canGetTeamById(){
      TeamData persistedTeam = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2000).request().get(TeamData.class);
   }
   
   
   @Test
   public void canGetAllTeams(){
      List<TeamData> persistedTeams = client.target(teamUrl).request().get(teamType);
   }
   
   
   @Test
   public void canUpdateTeam(){
      TeamData updatedTeam = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2001).request().get(TeamData.class);
      updatedTeam.setTeamName("UpdatedTeamName");
      
      client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2001).request().put(Entity.entity(updatedTeam, MediaType.APPLICATION_JSON), TeamData.class);
      TeamData persistedTeam = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2001).request().get(TeamData.class);
      
      assertThat(persistedTeam, equalTo(updatedTeam));
   }
   
   
   @Test
   public void canRemoveTeam(){
      TeamData team = new TeamData(2002, "LazyTeam");
      client.target(teamUrl).request().post(Entity.entity(team, MediaType.APPLICATION_JSON), TeamData.class);
      
      TeamData persistedTeam = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2002).request().get(TeamData.class);
      assertThat(persistedTeam, is(equalTo(team)));
      
      TeamData deletedTeam = client.target(teamUrl).path("/{teamId}").resolveTemplate("teamId", 2002).request().delete(TeamData.class);
      assertThat(deletedTeam, equalTo(null));
   }
   
}
