package cn.mcres.luckyfish.antileakaccount.verify.session;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyRequest;

import java.util.*;

public class RequestHolder {
    protected Map<UUID, VerifyRequest> requests = new HashMap<>();

    public void putRequest(UUID playerUid) {
        requests.put(playerUid, new VerifyRequest(playerUid));
    }

    public VerifyRequest getRequest(UUID playerUid) {
        return requests.get(playerUid);
    }

    public void removeRequest(UUID playerUid) {
        requests.remove(playerUid);
    }

    public boolean hasRequest(UUID playerUid) {
        return requests.containsKey(playerUid);
    }

    public void removeTimedoutSession() {
        List<UUID> removingUids = new ArrayList<>();
        requests.forEach((uid, request) -> {
            if (System.currentTimeMillis() - request.getCreatedTime() >= AntiLeakAccount.getInstance().getConfigHolder().urlTimeout) {
                removingUids.add(uid);
            }
        });

        for (UUID uid : removingUids) {
            requests.remove(uid);
        }
    }
}
