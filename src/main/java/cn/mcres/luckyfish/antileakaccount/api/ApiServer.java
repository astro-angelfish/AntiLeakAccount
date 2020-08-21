package cn.mcres.luckyfish.antileakaccount.api;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.exception.PlayerNotFoundException;
import org.bukkit.Bukkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class ApiServer {
    private ServerSocket socket;

    public ApiServer() {
        try {
            this.socket = new ServerSocket(AntiLeakAccount.getInstance().getConfigHolder().apiPort);
            Bukkit.getScheduler().runTaskAsynchronously(AntiLeakAccount.getInstance(), this::accept);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accept() {
        while (AntiLeakAccount.getInstance().isEnabled()) {
            try {
                Socket client = socket.accept();

                Bukkit.getScheduler().runTaskAsynchronously(AntiLeakAccount.getInstance(), () -> process(client));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(Socket client) {
        try {
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            try {
                UUID uid = UUID.fromString(dis.readUTF());
                String session = dis.readUTF();

                boolean succ = AntiLeakAccount.getInstance().getVerifyManager().processRequest(uid, session);
                dos.writeInt(succ ? 1 : 0);
            } catch (PlayerNotFoundException e) {
                dos.writeInt(0);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
