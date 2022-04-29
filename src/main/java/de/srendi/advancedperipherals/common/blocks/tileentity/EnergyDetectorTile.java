package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import de.srendi.advancedperipherals.common.util.EnergyStorageProxy;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyDetectorTile extends PeripheralTileEntity<EnergyDetectorPeripheral> implements ITickableTileEntity {

    //a zero size, zero transfer energy storage to ensure that cables connect
    private final EnergyStorage zeroStorage = new EnergyStorage(0, 0, 0);
    public int transferRate = 0;
    //storageProxy that will forward the energy to the output but limit it to maxTransferRate
    public EnergyStorageProxy storageProxy = new EnergyStorageProxy(this, APConfig.PERIPHERALS_CONFIG.ENERGY_DETECTOR_MAX_FLOW.get());
    LazyOptional<IEnergyStorage> energyStorageCap = LazyOptional.of(() -> storageProxy);
    Direction energyInDirection = Direction.NORTH;
    Direction energyOutDirection = Direction.SOUTH;
    LazyOptional<IEnergyStorage> zeroStorageCap = LazyOptional.of(() -> zeroStorage);
    @NotNull
    private Optional<IEnergyStorage> outReceivingStorage = Optional.empty();

    public EnergyDetectorTile() {
        super(TileEntityTypes.ENERGY_DETECTOR.get());
    }

    @NotNull
    @Override
    protected EnergyDetectorPeripheral createPeripheral() {
        return new EnergyDetectorPeripheral(this);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        energyInDirection = getBlockState().getValue(APTileEntityBlock.FACING);
        energyOutDirection = getBlockState().getValue(APTileEntityBlock.FACING).getOpposite();
        if (cap == CapabilityEnergy.ENERGY) {
            if (direction == energyInDirection) {
                return energyStorageCap.cast();
            } else if (direction == energyOutDirection) {
                return zeroStorageCap.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            // this handles the rare edge case that receiveEnergy is called multiple times in one tick
            transferRate = storageProxy.getTransferedInThisTick();
            storageProxy.resetTransferedInThisTick();
        }
    }

    @NotNull
    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.putInt("rateLimit", storageProxy.getMaxTransferRate());
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        storageProxy.setMaxTransferRate(compound.getInt("rateLimit"));
        super.load(state, compound);
    }

    // returns the cached output storage of the receiving block or refetches it if it has been invalidated
    @NotNull
    public Optional<IEnergyStorage> getOutputStorage() {
        // the documentation says that the value of the LazyOptional should be cached locally and invallidated using addListener
        if (!outReceivingStorage.isPresent()) {
            TileEntity teOut = level.getBlockEntity(worldPosition.relative(energyOutDirection));
            if (teOut == null) {
                return Optional.empty();
            }
            LazyOptional<IEnergyStorage> lazyOptionalOutStorage = teOut.getCapability(CapabilityEnergy.ENERGY, energyOutDirection.getOpposite());
            outReceivingStorage = lazyOptionalOutStorage.resolve();
            lazyOptionalOutStorage.addListener(l -> {
                outReceivingStorage = Optional.empty();
            });
        }
        return outReceivingStorage;
    }
}