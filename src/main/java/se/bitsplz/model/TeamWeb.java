package se.bitsplz.model;

import se.bitsplz.cms.model.TeamData.TeamStatus;


public final class TeamWeb{
   
   private final Long id;
   private final int teamId;
   private final String teamName;
   private final TeamStatus teamStatus;
   
   
   public TeamWeb(Long id, int teamId, String teamName){
      this.id = id;
      this.teamId = teamId;
      this.teamName = teamName;
      this.teamStatus = TeamStatus.INACTIVE;
   }
   
   
   public TeamWeb(Long id, int teamId, String teamName, TeamStatus teamStatus){
      this.id = id;
      this.teamId = teamId;
      this.teamName = teamName;
      this.teamStatus = teamStatus;
   }
   
   
   public Long getId(){
      return this.id;
   }
   
   
   public int getTeamId(){
      return this.teamId;
   }
   
   
   public String getTeamName(){
      return this.teamName;
   }
   
   
   public TeamStatus getTeamStatus(){
      return this.teamStatus;
   }
   
   
   @Override
   public String toString(){
      return "Team Id: " + this.teamId + " Team name: " + this.teamName + " Team status: " + this.teamStatus;
   }
   
}