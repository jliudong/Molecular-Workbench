/*
 * Generated by XDoclet - Do not edit!
 */
package org.concord.mwbackend.interfaces;

/**
 * Home interface for CommentManager.
 */
public interface CommentManagerHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/CommentManager";
   public static final String JNDI_NAME="ejb/CommentManager";

   public org.concord.mwbackend.interfaces.CommentManager create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
