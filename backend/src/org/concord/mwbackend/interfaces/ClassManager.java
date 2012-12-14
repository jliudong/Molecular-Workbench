/*
 * Generated by XDoclet - Do not edit!
 */
package org.concord.mwbackend.interfaces;

/**
 * Remote interface for ClassManager.
 */
public interface ClassManager
   extends javax.ejb.EJBObject
{
   /**
    * Business method
    */
   public boolean create( java.lang.String classID,org.concord.mwbackend.business.Person teacher )
      throws javax.ejb.CreateException, java.rmi.RemoteException;

   /**
    * Business method
    */
   public java.util.Collection findClasses( java.lang.String teacherID,boolean archived )
      throws javax.ejb.CreateException, java.rmi.RemoteException;

   /**
    * Business method
    */
   public boolean doesClassExist( java.lang.String classID )
      throws javax.ejb.CreateException, java.rmi.RemoteException;

   /**
    * Business method
    */
   public boolean renameClass( java.lang.String oldID,java.lang.String newID )
      throws javax.ejb.CreateException, java.rmi.RemoteException;

   /**
    * Business method
    */
   public boolean removeClass( java.lang.String id )
      throws javax.ejb.CreateException, java.rmi.RemoteException;

   /**
    * Business method
    */
   public boolean archiveClass( java.lang.String id,boolean b )
      throws javax.ejb.CreateException, java.rmi.RemoteException;

}