// Copyright 2005 Nick Mathewson, Roger Dingledine
// See LICENSE file for copying information
package steamarbitrage.steamio.tor.control.examples;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Iterator;

import steamarbitrage.steamio.tor.control.ConfigEntry;
import steamarbitrage.steamio.tor.control.PasswordDigest;
import steamarbitrage.steamio.tor.control.TorControlCommands;
import steamarbitrage.steamio.tor.control.TorControlConnection;
import steamarbitrage.steamio.tor.control.TorControlError;

public class Main implements TorControlCommands {

    public static void main(String args[]) {
        if (args.length < 1) {
            System.err.println("No command given.");
            return;
        }
        try {
            if (args[0].equals("set-config")) {
                setConfig(args);
            } else if (args[0].equals("get-config")) {
                getConfig(args);
            } else if (args[0].equals("get-info")) {
                getInfo(args);
            } else if (args[0].equals("listen")) {
                listenForEvents(args);
            } else if (args[0].equals("signal")) {
                signal(args);
            } else if (args[0].equals("auth")) {
                authDemo(args);
            } else {
                System.err.println("Unrecognized command: "+args[0]);
            }
        } catch (java.io.EOFException ex) {
            System.out.println("Control socket closed by Tor.");
        } catch (IOException ex) {
            System.err.println("IO exception when talking to Tor process: "+
                               ex);
            ex.printStackTrace(System.err);
        } catch (TorControlError ex) {
            System.err.println("Error from Tor process: "+
                               ex+" ["+ex.getErrorMsg()+"]");
        }
    }

    private static TorControlConnection getConnection(String[] args,
                                                      boolean daemon)
        throws IOException {
        TorControlConnection conn = TorControlConnection.getConnection(
                                    new java.net.Socket("127.0.0.1", 8118));
        //if (conn instanceof TorControlConnection1) {
        //    System.err.println("Debugging");
        //    ((TorControlConnection1)conn).setDebugging(System.err);
        //}
        conn.launchThread(daemon);
        conn.authenticate(new byte[0]);
        return conn;
    }

    private static TorControlConnection getConnection(String[] args)
        throws IOException {
        return getConnection(args, true);
    }

    public static void setConfig(String[] args) throws IOException {
        // Usage: "set-config [-save] key value key value key value"
        TorControlConnection conn = getConnection(args);
        ArrayList<String> lst = new ArrayList<String>();
        int i = 1;
        boolean save = false;
        if (args[i].equals("-save")) {
            save = true;
            ++i;
        }
        for (; i < args.length; i +=2) {
            lst.add(args[i]+" "+args[i+1]);
        }
        conn.setConf(lst);
        if (save) {
            conn.saveConf();
        }
    }

    public static void getConfig(String[] args) throws IOException {
        // Usage: get-config key key key
        TorControlConnection conn = getConnection(args);
        List<ConfigEntry> lst = conn.getConf(Arrays.asList(args).subList(1,args.length));
        for (Iterator<ConfigEntry> i = lst.iterator(); i.hasNext(); ) {
            ConfigEntry e = i.next();
            System.out.println("KEY: "+e.key);
            System.out.println("VAL: "+e.value);
        }
    }

    public static void getInfo(String[] args) throws IOException {
        TorControlConnection conn = getConnection(args);
        Map<String,String> m = conn.getInfo(Arrays.asList(args).subList(1,args.length));
        for (Iterator<Map.Entry<String, String>> i = m.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<String,String> e = i.next();
            System.out.println("KEY: "+e.getKey());
            System.out.println("VAL: "+e.getValue());
        }
    }

    public static void listenForEvents(String[] args) throws IOException {
        // Usage: listen [circ|stream|orconn|bw|newdesc|info|notice|warn|error]*
        TorControlConnection conn = getConnection(args, false);
        ArrayList<String> lst = new ArrayList<String>();
        for (int i = 1; i < args.length; ++i) {
            lst.add(args[i]);
        }
        conn.setEventHandler(
            new DebuggingEventHandler(new PrintWriter(System.out, true)));
        conn.setEvents(lst);
    }

    public static void signal(String[] args) throws IOException {
        // Usage signal [reload|shutdown|dump|debug|halt]
        TorControlConnection conn = getConnection(args, false);
        // distinguish shutdown signal from other signals
        if ("SHUTDOWN".equalsIgnoreCase(args[1])
        		|| "HALT".equalsIgnoreCase(args[1])) {
        	conn.shutdownTor(args[1].toUpperCase());
        } else {
        	conn.signal(args[1].toUpperCase());
        }
    }

    public static void authDemo(String[] args) throws IOException {

        PasswordDigest pwd = PasswordDigest.generateDigest();
        java.net.Socket s = new java.net.Socket("127.0.0.1", 9100);
        TorControlConnection conn = TorControlConnection.getConnection(s);
        conn.launchThread(true);
        conn.authenticate(new byte[0]);

        conn.setConf("HashedControlPassword", pwd.getHashedPassword());

        conn = TorControlConnection.getConnection(
                                    new java.net.Socket("127.0.0.1", 9100));
        conn.launchThread(true);
        conn.authenticate(pwd.getSecret());
    }

}

