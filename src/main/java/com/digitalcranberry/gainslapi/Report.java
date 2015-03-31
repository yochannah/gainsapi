package com.digitalcranberry.gainslapi;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Entity;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Entity
public class Report {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Persistent
    private String content;

    @Persistent
    private Date dateCreated; 

    @Persistent
    private User author;

    @Persistent
    private String status;
    
    public Report(User anAuthor, String theContent) {
        this.author = anAuthor;
        this.content = theContent;
        this.dateCreated = new Date();
        this.status = "new";
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String theContent) {
        this.content = theContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String aStatus) {
        this.status = aStatus;
    }    

    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User anAuthor) {
        this.author = anAuthor;
    }    

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date date) {
        this.dateCreated = date;
    }
}