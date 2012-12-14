/*
 * Generated by XDoclet - Do not edit!
 */
package org.concord.mwbackend.interfaces;

/**
 * Local home interface for LaunchRecord.
 */
public interface LaunchRecordLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/LaunchRecordLocal";
   public static final String JNDI_NAME="LaunchRecordLocal";

   public org.concord.mwbackend.interfaces.LaunchRecordLocal create(java.lang.String userName , java.lang.String os , java.lang.String clientHost , java.lang.String clientIpAddress , java.lang.String javaVersion , java.lang.String mwVersion , java.lang.String jwsLaunch)
      throws javax.ejb.CreateException;

   public java.util.Collection findRecordsAfter(long timeMillis)
      throws javax.ejb.FinderException;

   public org.concord.mwbackend.interfaces.LaunchRecordLocal findByPrimaryKey(java.lang.Integer pk)
      throws javax.ejb.FinderException;

}