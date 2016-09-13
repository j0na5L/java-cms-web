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

import se.bitsplz.model.TeamWeb;


@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamListProvider implements MessageBodyWriter<ArrayList<TeamWeb>>, MessageBodyReader<ArrayList<TeamWeb>>{
   
   private static final Gson gson = new GsonBuilder().registerTypeAdapter(ArrayListTeamAdapter.class, new ArrayListTeamAdapter()).create();
   
   
   @Override
   public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType){
      return aClass.isAssignableFrom(ArrayList.class);
   }
   
   
   @Override
   public long getSize(ArrayList<TeamWeb> teams, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType){
      return 0;
   }
   
   
   @Override
   public void writeTo(ArrayList<TeamWeb> teams, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException{
      try(JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream))){
         gson.toJson(teams, ArrayList.class, writer);
      }
   }
   
   
   @Override
   public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType){
      return aClass.isAssignableFrom(ArrayList.class);
   }
   
   
   @Override
   public ArrayList<TeamWeb> readFrom(Class<ArrayList<TeamWeb>> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException{
      return gson.fromJson(new InputStreamReader(inputStream), ArrayList.class);
   }
   
   private static final class ArrayListTeamAdapter implements JsonSerializer<ArrayList<TeamWeb>>, JsonDeserializer<ArrayList<TeamWeb>>{
      
      @Override
      public JsonElement serialize(ArrayList<TeamWeb> teams, Type type, JsonSerializationContext jsonSerializationContext){
         JsonArray jsonArray = new JsonArray();
         
         for(TeamWeb team : teams){
            JsonObject json2 = new JsonObject();
            json2.addProperty("id", team.getId());
            json2.addProperty("teamId", team.getTeamId());
            json2.addProperty("teamName", team.getTeamName());
            json2.addProperty("teamStatus", team.getTeamStatus().toString());
            jsonArray.add(json2);
         }
         return jsonArray;
      }
      
      
      @Override
      public ArrayList<TeamWeb> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException{
         JsonArray jArray = jsonElement.getAsJsonArray();
         Iterator<JsonElement> iterator = jArray.iterator();
         ArrayList<TeamWeb> teams = new ArrayList<>();
         
         while(iterator.hasNext()){
            JsonElement json = iterator.next();
            TeamWeb team = gson.fromJson(json, (Class<TeamWeb>)type);
            teams.add(team);
         }
         return teams;
      }
   }
}
