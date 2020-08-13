package cn.mcres.luckyfish.antileakaccount.mojang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MojangApiHelper {
    private static final String profileUrl = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String authUrl = "https://authserver.mojang.com/authenticate";

    private static final Gson gson = new Gson();
    private static final UserCache userCache = new UserCache();

    public static String getMinecraftNameByUuid(UUID uuid) {
        String cache = userCache.getCachedUsername(uuid);
        if (cache != null) {
            return cache;
        }

        try {
            URL url = new URL(profileUrl + uuid.toString().replaceAll("-", ""));
            HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
            uc.setRequestMethod("GET");
            uc.setConnectTimeout(5000);
            uc.setReadTimeout(5000);
            uc.setDoInput(true);
            uc.connect();
            if (uc.getResponseCode() != 200) {
                return null;
            }
            Map<?, ?> content = gson.fromJson(new InputStreamReader(uc.getInputStream()), Map.class);
            String fetchName = content.get("name").toString();
            if (fetchName != null) {
                userCache.writeUser(uuid, fetchName);
            }
            return fetchName;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateWithEmailAndPassword(String email, String password) {
        try {
            HttpsURLConnection uc = (HttpsURLConnection) new URL(authUrl).openConnection();
            try {
                uc.setRequestMethod("POST");
                uc.setConnectTimeout(5000);
                uc.setReadTimeout(5000);
                uc.setDoInput(true);
                uc.setDoOutput(true);
                uc.setRequestProperty("Content-Type", "application/json");

                Map<String, Object> payload = new HashMap<>();
                Map<String, Object> agent = new HashMap<>();
                agent.put("name", "Minecraft");
                agent.put("version", 1);
                payload.put("agent", agent);
                payload.put("username", email);
                payload.put("password", password);

                OutputStreamWriter osw = new OutputStreamWriter(uc.getOutputStream());
                osw.write(gson.toJson(payload));
                osw.flush();

                Map<String, Object> response = gson.fromJson(new InputStreamReader(uc.getInputStream()), new TypeToken<Map<String, Object>>() {
                }.getType());
                String accessToken = response.get("accessToken").toString();

                return accessToken != null;
            } catch (Exception e) {
                IOUtils.copy(uc.getErrorStream(), System.out);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
