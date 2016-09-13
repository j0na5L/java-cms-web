package se.bitsplz.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import se.bitsplz.cms.model.UserData.UserStatus;
import se.bitsplz.model.UserWeb;


@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UserProvider implements MessageBodyWriter<UserWeb>, MessageBodyReader<UserWeb>{
   
   private static final Gson gson = new GsonBuilder().registerTypeAdapter(UserWeb.class, new UserAdapter()).create();
   
   
   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType){
      return type.isAssignableFrom(UserWeb.class);
   }
   
   
   @Override
   public long getSize(UserWeb t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType){
      return 0;
   }
   
   
   @Override
   public void writeTo(UserWeb user, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException{
      
      try(JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))){
         gson.toJson(user, UserWeb.class, writer);
      }
   }
   
   
   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType){
      return type.isAssignableFrom(UserWeb.class);
   }
   
   
   @Override
   public UserWeb readFrom(Class<UserWeb> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException{
      
      return gson.fromJson(new InputStreamReader(entityStream), UserWeb.class);
   }
   
   // GSON
   private static final class UserAdapter implements JsonSerializer<UserWeb>, JsonDeserializer<UserWeb>{
      
      @Override
      public JsonElement serialize(UserWeb user, Type type, JsonSerializationContext context){
         
         JsonObject json = new JsonObject();
         json.addProperty("id", user.getId());
         json.addProperty("userId", user.getUserId());
         json.addProperty("username", user.getUsername());
         json.addProperty("firstName", user.getFirstName());
         json.addProperty("lastName", user.getLastName());
         json.addProperty("userStatus", user.getUserStatus().toString());
         
         return json;
      }
      
      
      @Override
      public UserWeb deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException{
         
         JsonObject userJson = json.getAsJsonObject();
         Long id = userJson.get("id").getAsLong();
         int userId = userJson.get("userId").getAsInt();
         String username = userJson.get("username").getAsString();
         String firstName = userJson.get("firstName").getAsString();
         String lastName = userJson.get("lastName").getAsString();
         String userStatus = userJson.get("userStatus").getAsString();
         
         UserWeb user = new UserWeb(id, userId, username, firstName, lastName, UserStatus.valueOf(userStatus));
         
         return user;
      }
      
   }
   
}
