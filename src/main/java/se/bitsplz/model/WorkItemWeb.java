package se.bitsplz.model;

import se.bitsplz.cms.model.WorkItemData.WorkItemStatus;


public final class WorkItemWeb{
   
   private final Long id;
   private final int workItemId;
   private final String description;
   private final WorkItemStatus workItemStatus;
   
   
   public WorkItemWeb(Long id, int workItemId, String description, WorkItemStatus workItemStatus){
      this.id = id;
      this.workItemId = workItemId;
      this.description = description;
      this.workItemStatus = workItemStatus;
   }
   
   
   public Long getId(){
      return this.id;
   }
   
   
   public int getWorkItemId(){
      return this.workItemId;
   }
   
   
   public String getDescription(){
      return this.description;
   }
   
   
   public WorkItemStatus getWorkItemStatus(){
      return this.workItemStatus;
   }
   
   
   @Override
   public String toString(){
      return "id: " + this.workItemId + ", description: " + this.description;
   }
}
