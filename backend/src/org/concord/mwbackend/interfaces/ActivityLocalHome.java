/*
 * Generated by XDoclet - Do not edit!
 */
package org.concord.mwbackend.interfaces;

/**
 * Local home interface for Activity.
 */
public interface ActivityLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/ActivityLocal";
   public static final String JNDI_NAME="ActivityLocal";

   public org.concord.mwbackend.interfaces.ActivityLocal create(java.lang.String url , java.lang.String title , java.lang.String instruction , java.lang.String userID , java.lang.String classID)
      throws javax.ejb.CreateException;

   public java.util.Collection findByUser(java.lang.String userID)
      throws javax.ejb.FinderException;

   public java.util.Collection findByUserAndClass(java.lang.String userID, java.lang.String classID)
      throws javax.ejb.FinderException;

   public org.concord.mwbackend.interfaces.ActivityLocal findByPrimaryKey(java.lang.Integer pk)
      throws javax.ejb.FinderException;

}
