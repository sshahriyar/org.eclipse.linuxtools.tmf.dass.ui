/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.live;

import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;

import com.jcraft.jsch.*;

/**
 * This class implements the UserInfo Interface in JSch package. This interface
 * is necessary to implment in order to connect to an SSH server
 *
 * @author <p>
 *         Syed Shariyar Murtaza justsshary@hotmail.com
 *         </p>
 *
 */
public class UserInfoSSH implements UserInfo {
    private String fPassword;
    private ProgressConsole fProgConsole;

    /**
     * Constructor
     *
     * @param password
     *            Password
     * @param console
     *            Console object
     */
    public UserInfoSSH(String password, ProgressConsole console) {
        this.fPassword = password;
        this.fProgConsole = console;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
     */
    @Override
    public void showMessage(String msg) {
        fProgConsole.println(msg);
        // System.out.println(msg);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
     */
    @Override
    public boolean promptYesNo(String arg0) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
     */
    @Override
    public boolean promptPassword(String arg0) {

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
     */
    @Override
    public boolean promptPassphrase(String arg0) {

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jcraft.jsch.UserInfo#getPassword()
     */
    @Override
    public String getPassword() {

        return fPassword;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jcraft.jsch.UserInfo#getPassphrase()
     */
    @Override
    public String getPassphrase() {

        return null;
    }

}
