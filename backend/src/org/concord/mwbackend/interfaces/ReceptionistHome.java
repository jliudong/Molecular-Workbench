/*
 * Generated by XDoclet - Do not edit!
 */
package org.concord.mwbackend.interfaces;

/**
 * Home interface for Receptionist.
 */
public interface ReceptionistHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/Receptionist";
   public static final String JNDI_NAME="ejb/Receptionist";

   public org.concord.mwbackend.interfaces.Receptionist create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
