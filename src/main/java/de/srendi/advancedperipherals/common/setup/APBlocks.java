package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.blocks.PlayerDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.APBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class APBlocks {

    static void register() {
    }

    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.ENVIRONMENT_DETECTOR, false), () -> new APBlockItem(APBlocks.ENVIRONMENT_DETECTOR.get(), CCRegistration.ID.ENVIRONMENT_TURTLE, CCRegistration.ID.ENVIRONMENT_POCKET, APConfig.PERIPHERALS_CONFIG.enableEnvironmentDetector));
    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", () -> new APBlockEntityBlock<>(APBlockEntityTypes.CHAT_BOX, true), () -> new APBlockItem(APBlocks.CHAT_BOX.get(), CCRegistration.ID.CHATTY_TURTLE, CCRegistration.ID.CHATTY_POCKET, APConfig.PERIPHERALS_CONFIG.enableChatBox));
    public static final RegistryObject<Block> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new, () -> new APBlockItem(APBlocks.PLAYER_DETECTOR.get(), CCRegistration.ID.PLAYER_TURTLE, CCRegistration.ID.PLAYER_POCKET, APConfig.PERIPHERALS_CONFIG.enablePlayerDetector));
    public static final RegistryObject<Block> ME_BRIDGE = register("me_bridge", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("ae2") ? APBlockEntityTypes.ME_BRIDGE : null, ModList.get().isLoaded("ae2")), () -> new APBlockItem(APBlocks.ME_BRIDGE.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableMEBridge));
    public static final RegistryObject<Block> RS_BRIDGE = register("rs_bridge", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("refinedstorage") ? APBlockEntityTypes.RS_BRIDGE : null, false), () -> new APBlockItem(APBlocks.RS_BRIDGE.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableRSBridge));
    public static final RegistryObject<Block> ENERGY_DETECTOR = register("energy_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.ENERGY_DETECTOR, true), () -> new APBlockItem(APBlocks.ENERGY_DETECTOR.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableEnergyDetector));
    public static final RegistryObject<Block> FLUID_DETECTOR = register("fluid_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.FLUID_DETECTOR, true), () -> new APBlockItem(APBlocks.FLUID_DETECTOR.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableFluidDetector));
    public static final RegistryObject<Block> GAS_DETECTOR = register("gas_detector", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("mekanism") ? APBlockEntityTypes.GAS_DETECTOR : null, true), () -> new APBlockItem(APBlocks.GAS_DETECTOR.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableGasDetector));
    public static final RegistryObject<Block> PERIPHERAL_CASING = register("peripheral_casing", BaseBlock::new, () -> new APBlockItem(APBlocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), null, null, () -> true));
    public static final RegistryObject<Block> AR_CONTROLLER = register("ar_controller", () -> new APBlockEntityBlock<>(APBlockEntityTypes.AR_CONTROLLER, false), () -> new APBlockItem(APBlocks.AR_CONTROLLER.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableSmartGlasses));
    public static final RegistryObject<Block> INVENTORY_MANAGER = register("inventory_manager", () -> new APBlockEntityBlock<>(APBlockEntityTypes.INVENTORY_MANAGER, false), () -> new APBlockItem(APBlocks.INVENTORY_MANAGER.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableInventoryManager));
    public static final RegistryObject<Block> REDSTONE_INTEGRATOR = register("redstone_integrator", RedstoneIntegratorBlock::new, () -> new APBlockItem(APBlocks.REDSTONE_INTEGRATOR.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableRedstoneIntegrator));
    public static final RegistryObject<Block> BLOCK_READER = register("block_reader", () -> new APBlockEntityBlock<>(APBlockEntityTypes.BLOCK_READER, true), () -> new APBlockItem(APBlocks.BLOCK_READER.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableBlockReader));
    public static final RegistryObject<Block> GEO_SCANNER = register("geo_scanner", () -> new APBlockEntityBlock<>(APBlockEntityTypes.GEO_SCANNER, false), () -> new APBlockItem(APBlocks.GEO_SCANNER.get(), CCRegistration.ID.GEOSCANNER_TURTLE, CCRegistration.ID.GEOSCANNER_POCKET, APConfig.PERIPHERALS_CONFIG.enableGeoScanner));
    public static final RegistryObject<Block> COLONY_INTEGRATOR = register("colony_integrator", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("minecolonies") ? APBlockEntityTypes.COLONY_INTEGRATOR : null, false), () -> new APBlockItem(APBlocks.COLONY_INTEGRATOR.get(), null, CCRegistration.ID.COLONY_POCKET, APConfig.PERIPHERALS_CONFIG.enableColonyIntegrator));
    public static final RegistryObject<Block> NBT_STORAGE = register("nbt_storage", () -> new APBlockEntityBlock<>(APBlockEntityTypes.NBT_STORAGE, false), () -> new APBlockItem(APBlocks.NBT_STORAGE.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableNBTStorage));

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return APRegistration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        RegistryObject<T> registryObject = registerNoItem(name, block);
        APRegistration.ITEMS.register(name, blockItem);
        return registryObject;
    }

    public static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

}
