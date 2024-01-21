package com.altening.auth.service;

import java.net.URL;

public final class ServiceSwitcher {

    private final String MINECRAFT_SESSION_SERVICE_CLASS = "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService";
    private final String MINECRAFT_AUTHENTICATION_SERVICE_CLASS = "com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication";

    private final String[] WHITELISTED_DOMAINS = new String[]{".minecraft.net", ".mojang.com", ".thealtening.com"};

    private final FieldAdapter minecraftSessionServer = new FieldAdapter(MINECRAFT_SESSION_SERVICE_CLASS);
    private final FieldAdapter userAuthentication = new FieldAdapter(MINECRAFT_AUTHENTICATION_SERVICE_CLASS);

    public ServiceSwitcher() {
        try {
            minecraftSessionServer.updateFieldIfPresent("WHITELISTED_DOMAINS", WHITELISTED_DOMAINS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AlteningServiceType switchToService(AlteningServiceType service) {
        try {
            String authServer = service.getAuthServer();
            FieldAdapter userAuth = this.userAuthentication;
            userAuth.updateFieldIfPresent("BASE_URL", authServer);
            userAuth.updateFieldIfPresent("ROUTE_AUTHENTICATE", new URL(authServer + "authenticate"));
            userAuth.updateFieldIfPresent("ROUTE_INVALIDATE", new URL(authServer + "invalidate"));
            userAuth.updateFieldIfPresent("ROUTE_REFRESH", new URL(authServer + "refresh"));
            userAuth.updateFieldIfPresent("ROUTE_VALIDATE", new URL(authServer + "validate"));
            userAuth.updateFieldIfPresent("ROUTE_SIGNOUT", new URL(authServer + "signout"));

            String sessionServer = service.getSessionServer();
            FieldAdapter userSession = this.minecraftSessionServer;
            userSession.updateFieldIfPresent("BASE_URL", sessionServer + "session/minecraft/");
            userSession.updateFieldIfPresent("JOIN_URL", new URL(sessionServer + "session/minecraft/join"));
            userSession.updateFieldIfPresent("CHECK_URL", new URL(sessionServer + "session/minecraft/hasJoined"));
        } catch (Exception any) {
            any.printStackTrace();
            return AlteningServiceType.MOJANG;
        }

        return service;
    }
}