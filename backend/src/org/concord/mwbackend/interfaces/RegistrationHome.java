/*
 * Generated by XDoclet - Do not edit!
 */
package org.concord.mwbackend.interfaces;

/**
 * Home interface for Registration.
 */
public interface RegistrationHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/Registration";
   public static final String JNDI_NAME="ejb/Registration";

   public org.concord.mwbackend.interfaces.Registration create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}
