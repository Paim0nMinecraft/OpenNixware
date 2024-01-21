package dev.tenacity.protection;

import dev.tenacity.NIXWARE;
import dev.tenacity.commands.CommandHandler;
import dev.tenacity.commands.impl.*;
import dev.tenacity.config.ConfigManager;
import dev.tenacity.config.DragManager;
import dev.tenacity.intent.api.account.IntentAccount;
import dev.tenacity.module.BackgroundProcess;
import dev.tenacity.module.Module;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.module.api.TargetManager;
import dev.tenacity.module.impl.combat.*;
import dev.tenacity.module.impl.exploit.*;
import dev.tenacity.module.impl.misc.*;
import dev.tenacity.module.impl.movement.*;
import dev.tenacity.module.impl.player.*;
import dev.tenacity.module.impl.render.*;
import dev.tenacity.module.impl.render.killeffects.KillEffects;
import dev.tenacity.module.impl.render.wings.DragonWings;
import dev.tenacity.ui.altmanager.GuiAltManager;
import dev.tenacity.ui.altmanager.helpers.KingGenApi;
import dev.tenacity.utils.render.EntityCulling;
import dev.tenacity.utils.render.Theme;
import dev.tenacity.utils.server.PingerUtils;
import dev.tenacity.viamcp.ViaMCP;
import net.minecraft.client.Minecraft;
import store.intent.intentguard.annotation.Bootstrap;
import store.intent.intentguard.annotation.Native;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

@Native
public class ProtectedLaunch {

    private static final HashMap<Object, Module> modules = new HashMap<>();

    @Native
    @Bootstrap
    public static void start() {
        // Setup Intent API access
        NIXWARE.INSTANCE.setIntentAccount(new IntentAccount());
        NIXWARE.INSTANCE.setModuleCollection(new ModuleCollection());

        // Combat
        modules.put(KillAura.class, new KillAura());
        modules.put(AutoBot.class, new AutoBot());
        modules.put(MultiAura.class, new MultiAura());
        modules.put(Velocity.class, new Velocity());
        modules.put(Criticals.class, new Criticals());
        modules.put(AutoHead.class, new AutoHead());
        modules.put(AutoPot.class, new AutoPot());
        modules.put(FastBow.class, new FastBow());
        modules.put(KeepSprint.class, new KeepSprint());
        modules.put(SuperKnockback.class, new SuperKnockback());
        modules.put(TargetManager.class, new TargetManager());

        // Exploit
        modules.put(Disabler.class, new Disabler());
        modules.put(FakeLag.class, new FakeLag());
        modules.put(Regen.class, new Regen());
        modules.put(TpAura.class, new TpAura());
        modules.put(AntiAim.class, new AntiAim());
        modules.put(Crasher.class, new Crasher());

        // Misc
        modules.put(AntiDesync.class, new AntiDesync());
        modules.put(AntiFreeze.class, new AntiFreeze());
        modules.put(LightningTracker.class, new LightningTracker());
        modules.put(AutoHypixel.class, new AutoHypixel());
        modules.put(NoRotate.class, new NoRotate());
        modules.put(Sniper.class, new Sniper());
        modules.put(AutoAuthenticate.class, new AutoAuthenticate());
        modules.put(AutoRespawn.class, new AutoRespawn());
        modules.put(MCF.class, new MCF());
        modules.put(IQBoost.class, new IQBoost());
        modules.put(HackerDetector.class, new HackerDetector());
        modules.put(Spammer.class, new Spammer());
        modules.put(Killsults.class, new Killsults());

        // Movement
        modules.put(Sprint.class, new Sprint());
        modules.put(Scaffold.class, new Scaffold());
        modules.put(Speed.class, new Speed());
        modules.put(Flight.class, new Flight());
        modules.put(Clach.class, new Clach());
        modules.put(LongJump.class, new LongJump());
        modules.put(Step.class, new Step());
        modules.put(TargetStrafe.class, new TargetStrafe());
        modules.put(Inventory.class, new Inventory());
        modules.put(Jesus.class, new Jesus());

        // Player
        modules.put(ChestStealer.class, new ChestStealer());
        modules.put(InvManager.class, new InvManager());
        modules.put(AutoArmor.class, new AutoArmor());
        modules.put(LegitScaffold.class, new LegitScaffold());
        modules.put(SpeedMine.class, new SpeedMine());
        modules.put(Blink.class, new Blink());
        modules.put(NoFall.class, new NoFall());
        modules.put(Timer.class, new Timer());
        modules.put(Freecam.class, new Freecam());
        modules.put(AutoGapple.class, new AutoGapple());
        modules.put(FastUse.class, new FastUse());
        modules.put(FastPlace.class, new FastPlace());
        modules.put(SafeWalk.class, new SafeWalk());
        modules.put(NoSlowdown.class, new NoSlowdown());
        modules.put(AutoTool.class, new AutoTool());
        modules.put(AntiVoid.class, new AntiVoid());
        modules.put(KillEffects.class, new KillEffects());

        // Render
        modules.put(ArrayListMod.class, new ArrayListMod());
        modules.put(NotificationsMod.class, new NotificationsMod());
        modules.put(ScoreboardMod.class, new ScoreboardMod());
        modules.put(HUDMod.class, new HUDMod());
        modules.put(ClickGUIMod.class, new ClickGUIMod());
        modules.put(Radar.class, new Radar());
        modules.put(Animations.class, new Animations());
        modules.put(SpotifyMod.class, new SpotifyMod());
        modules.put(FollowTarget.class, new FollowTarget());
        modules.put(Ambience.class, new Ambience());
        modules.put(PositionRender.class, new PositionRender());
        modules.put(ChinaHat.class, new ChinaHat());
        modules.put(GlowESP.class, new GlowESP());
        modules.put(Brightness.class, new Brightness());
        modules.put(NameTags.class, new NameTags());
        modules.put(Shader.class, new Shader());
        modules.put(Statistics.class, new Statistics());
        modules.put(TargetHUDMod.class, new TargetHUDMod());
        modules.put(RenderMemory.class, new RenderMemory());
        modules.put(Glint.class, new Glint());
        modules.put(Breadcrumbs.class, new Breadcrumbs());
        modules.put(Streamer.class, new Streamer());
        modules.put(NoHurtCam.class, new NoHurtCam());
        modules.put(Keystrokes.class, new Keystrokes());
        modules.put(ItemPhysics.class, new ItemPhysics());
        modules.put(XRay.class, new XRay());
        modules.put(EntityCulling.class, new EntityCulling());
        modules.put(DragonWings.class, new DragonWings());
        modules.put(PlayerList.class, new PlayerList());
        modules.put(JumpCircle.class, new JumpCircle());
        modules.put(CustomModel.class, new CustomModel());
        modules.put(EntityEffects.class, new EntityEffects());
        modules.put(Chams.class, new Chams());
        modules.put(BrightPlayers.class, new BrightPlayers());
        modules.put(MusicPlayer.class,new MusicPlayer());
        modules.put(MusicBar.class,new MusicBar());

        NIXWARE.INSTANCE.getModuleCollection().setModules(modules);

        Theme.init();

        NIXWARE.INSTANCE.setPingerUtils(new PingerUtils());

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(),
                new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(),
                new LoadCommand(), new ToggleCommand()
        ));
        NIXWARE.INSTANCE.setCommandHandler(commandHandler);
        NIXWARE.INSTANCE.getEventProtocol().register(new BackgroundProcess());

        NIXWARE.INSTANCE.setConfigManager(new ConfigManager());
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/nixware/Config.json");
        NIXWARE.INSTANCE.getConfigManager().collectConfigs();
        if (ConfigManager.defaultConfig.exists()) {
            NIXWARE.INSTANCE.getConfigManager().loadConfig(NIXWARE.INSTANCE.getConfigManager().readConfigData(ConfigManager.defaultConfig.toPath()), true);
        }

        DragManager.loadDragData();

        NIXWARE.INSTANCE.setAltManager(new GuiAltManager());

        NIXWARE.INSTANCE.setKingGenApi(new KingGenApi());

        try {
            NIXWARE.LOGGER.info("Starting ViaMCP...");
            ViaMCP viaMCP = ViaMCP.getInstance();
            viaMCP.start();
            viaMCP.initAsyncSlider(100, 100, 110, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SafeVarargs
    private static void addModules(Class<? extends Module>... classes) {
        for (Class<? extends Module> moduleClass : classes) {
            try {
                modules.put(moduleClass, moduleClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
