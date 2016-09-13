package se.bitsplz.webtest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import se.bitsplz.cms.model.UserData;
import se.bitsplz.cms.model.UserData.UserStatus;


public class UserServiceTest extends AbstractServiceTest{
   
   private static GenericType<List<UserData>> userType = new GenericType<List<UserData>>(){
   };
   
   private UserData userToTest;
   
   
   @Before
   public void before(){
      this.userToTest = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1000).request().get(UserData.class);
   }
   
   
   @Test
   public void canGetUserById(){
      UserData persistedUser = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1000).request().get(UserData.class);
      assertThat(persistedUser, equalTo(this.userToTest));
   }
   
   
   @Test
   public void canGetUserBasedOnFirstName(){
      List<UserData> persistedUser = client.target(userUrl).path("/{enquiry}/{variable}").resolveTemplate("enquiry", "firstName").resolveTemplate("variable", "Katt").request().get(userType);
      assertThat(persistedUser.size(), equalTo(1));
      assertThat(persistedUser.get(0), is(equalTo(this.userToTest)));
   }
   
   
   @Test
   public void canGetUserBasedOnLastName(){
      List<UserData> persistedUser = client.target(userUrl).path("/{enquiry}/{variable}").resolveTemplate("enquiry", "lastName").resolveTemplate("variable", "Pappa").request().get(userType);
      assertThat(persistedUser.size(), equalTo(1));
      System.out.println(persistedUser.toString() + " " + user2.toString());
      assertThat(persistedUser.get(0), is(equalTo(user2)));
   }
   
   
   @Test
   public void canGetUserBasedOnUsername(){
      List<UserData> persistedUser = client.target(userUrl).path("/{enquiry}/{variable}").resolveTemplate("enquiry", "username").resolveTemplate("variable", "Kattmamman007").request().get(userType);
      assertThat(persistedUser.size(), equalTo(1));
      assertThat(persistedUser.get(0), is(equalTo(this.userToTest)));
   }
   
   
   // can get workitems for user
   
   @Test
   public void canGetAllUsers(){
      List<UserData> persistedUsers = client.target(userUrl).request().get(userType);
      assertThat(persistedUsers.size(), equalTo(2));
   }
   
   
   @Test
   public void canAddUserToTeam(){
      UserData userToAdd = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().get(UserData.class);
      
      client.target(userUrl).path("/{userId}/{teamId}").resolveTemplate("userId", 1001).resolveTemplate("teamId", 2000).request().put(Entity.entity(userToAdd, MediaType.APPLICATION_JSON), UserData.class);
      UserData persistedUser = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().get(UserData.class);
      assertThat(persistedUser.getTeam().getTeamId(), equalTo(2000));
   }
   
   
   @Test
   public void canUpdateUser(){
      UserData updated = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().get(UserData.class);
      updated.setUsername("NewUsername");
      System.out.println(updated.toString());
      client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().put(Entity.entity(updated, MediaType.APPLICATION_JSON), UserData.class);
      UserData persistedUser = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().get(UserData.class);
      System.out.println(persistedUser.toString());
      assertThat(persistedUser, equalTo(updated));
   }
   
   
   @Test
   public void canRemoveUser(){
      UserData user = new UserData(1002, "Hamsterbarnet007", "Hamster", "Barnet", UserStatus.ACTIVE);
      client.target(userUrl).request().post(Entity.entity(user, MediaType.APPLICATION_JSON), UserData.class);
      
      UserData persistedUser = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1002).request().get(UserData.class);
      assertThat(persistedUser, equalTo(user));
      
      UserData deleted = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1002).request().delete(UserData.class);
      assertThat(deleted, equalTo(null));
   }
   
}