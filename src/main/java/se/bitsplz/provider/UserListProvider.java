package se.bitsplz.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import se.bitsplz.model.UserWeb;


@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserListProvider implements MessageBodyWriter<ArrayList<UserWeb>>, MessageBodyReader<ArrayList<UserWeb>>{
   
   private static final Gson gson = new GsonBuilder().registerTypeAdapter(ArrayListUserAdapter.class, new ArrayListUserAdapter()).create();
   
   
   @Override
   public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType){
      return aClass.isAssignableFrom(ArrayList.class);
   }
   
   
   @Override
   public long getSize(ArrayList<UserWeb> users, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType){
      return 0;
   }
   
   
   @Override
   public void writeTo(ArrayList<UserWeb> users, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException{
      try(JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream))){
         gson.toJson(users, ArrayList.class, writer);
      }
   }
   
   
   @Override
   public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType){
      return aClass.isAssignableFrom(ArrayList.class);
   }
   
   
   @Override
   public ArrayList<UserWeb> readFrom(Class<ArrayList<UserWeb>> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException{
      return gson.fromJson(new InputStreamReader(inputStream), ArrayList.class);
   }
   
   private static final class ArrayListUserAdapter implements JsonSerializer<ArrayList<UserWeb>>, JsonDeserializer<ArrayList<UserWeb>>{
      
      @Override
      public JsonElement serialize(ArrayList<UserWeb> users, Type type, JsonSerializationContext jsonSerializationContext){
         JsonArray jsonArray = new JsonArray();
         
         for(UserWeb user : users){
            JsonObject json2 = new JsonObject();
            json2.addProperty("id", user.getId());
            json2.addProperty("userId", user.getUserId());
            json2.addProperty("username", user.getUsername());
            json2.addProperty("firstName", user.getFirstName());
            json2.addProperty("lastName", user.getLastName());
            json2.addProperty("userStatus", user.getUserStatus().toString());
            jsonArray.add(json2);
         }
         return jsonArray;
      }
      
      
      @Override
      public ArrayList<UserWeb> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException{
         JsonArray jArray = jsonElement.getAsJsonArray();
         Iterator<JsonElement> iterator = jArray.iterator();
         ArrayList<UserWeb> users = new ArrayList<>();
         
         while(iterator.hasNext()){
            JsonElement json = iterator.next();
            UserWeb user = gson.fromJson(json, (Class<UserWeb>)type);
            users.add(user);
         }
         return users;
      }
   }
}
