package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.Registration;
import de.srendi.advancedperipherals.common.village.VillageStructures;
import de.srendi.advancedperipherals.network.MNetwork;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.Random;

@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {

    public static final String MOD_ID = "advancedperipherals";
    public static final Logger LOGGER = LogManager.getLogger("Advanced Peripherals");
    public static final Random RANDOM = new Random();
    public static final CreativeModeTab TAB = new CreativeModeTab("advancedperipheralstab") {

        @Override
        @NotNull
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.CHAT_BOX.get());
        }

    };

    private static boolean curiosLoaded;

    public AdvancedPeripherals() {
        LOGGER.info("AdvancedPeripherals says hello!");
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        APConfig.register(ModLoadingContext.get());

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::interModComms);
        modBus.addListener(this::clientSetup);
        Registration.register();
        MinecraftForge.EVENT_BUS.register(this);

        //TODO: Refactor this to a dedicated class
        curiosLoaded = ModList.get().isLoaded("curios");
        if (ModList.get().isLoaded("refinedstorage")) {
            RefinedStorage.instance = new RefinedStorage();
            RefinedStorage.instance.initiate();
        }
    }

    public static void debug(String message) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get()) LOGGER.debug("[DEBUG] " + message);
    }

    public static void debug(String message, Level level) {
        if (APConfig.GENERAL_CONFIG.enableDebugMode.get()) LOGGER.log(level, "[DEBUG] " + message);
    }

    public static boolean isCuriosLoaded() {
        return curiosLoaded;
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VillageStructures.init();
            MNetwork.init();
        });
    }

    public void clientSetup(FMLClientSetupEvent event) {
        HudOverlayHandler.init();
    }

    @SubscribeEvent
    public void interModComms(InterModEnqueueEvent event) {
        if (!curiosLoaded) return;
        //InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("glasses")
        //.size(1).icon(new ResourceLocation(MOD_ID, "textures/item/empty_glasses_slot.png")).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("glasses").size(1).build());
    }

}
