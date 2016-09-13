package se.bitsplz.model;

public final class IssueWeb{
   
   private final String issue;
   
   
   public IssueWeb(String issue){
      this.issue = issue;
   }
   
   
   public String getIssue(){
      return this.issue;
   }
   
   
   @Override
   public String toString(){
      return "Issue " + this.issue;
   }
}
