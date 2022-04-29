package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.codec.binary.Hex;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class RefinedStorage {

    public static RefinedStorage instance;
    private final IRSAPI api;

    public RefinedStorage() {
        this.api = API.instance();
        initiate();
    }

    private static INetworkNode read(CompoundNBT tag, NetworkNode node) {
        node.read(tag);
        return node;
    }

    public static Object listFluids(INetwork network) {
        List<Object> fluids = new ArrayList<>();
        getFluids(network).forEach(item -> fluids.add(getObjectFromFluid(item, network)));
        return fluids;
    }

    public static Object listItems(INetwork network) {
        List<Object> items = new ArrayList<>();
        getItems(network).forEach(item -> items.add(getObjectFromStack(item, network)));
        return items;
    }

    public static boolean isItemCraftable(INetwork network, ItemStack stack) {
        ItemStack itemStack = stack.copy();
        //RS does not like empty tag values. So we set it to null when it's empty.
        //When we use an empty tag, RS will not find a valid pattern.
        itemStack.setTag(itemStack.getOrCreateTag().isEmpty() ? null : itemStack.getTag());
        return network.getCraftingManager().getPattern(itemStack) != null;
    }

    public static boolean isFluidCraftable(INetwork network, FluidStack stack) {
        return network.getCraftingManager().getPattern(stack) != null;
    }

    public static int getMaxItemDiskStorage(INetwork network) {
        int total = 0;
        boolean creative = false;
        for (IStorage<ItemStack> store : network.getItemStorageCache().getStorages()) {
            if (store instanceof IStorageDisk) {
                int cap = ((IStorageDisk<ItemStack>) store).getCapacity();
                if (cap > 0) total += cap;
                else creative = true;
            }
        }
        return creative ? -1 : total;
    }

    public static int getMaxFluidDiskStorage(INetwork network) {
        int total = 0;
        boolean creative = false;
        for (IStorage<FluidStack> store : network.getFluidStorageCache().getStorages()) {
            if (store instanceof IStorageDisk) {
                int cap = ((IStorageDisk<FluidStack>) store).getCapacity();
                if (cap > 0) total += cap;
                else creative = true;
            }
        }
        return creative ? -1 : total;
    }

    public static int getMaxItemExternalStorage(INetwork network) {
        int total = 0;
        for (IStorage<ItemStack> store : network.getItemStorageCache().getStorages()) {
            if (store instanceof IExternalStorage) {
                total += ((IExternalStorage<ItemStack>) store).getCapacity();
            }
        }
        return total;
    }

    public static int getMaxFluidExternalStorage(INetwork network) {
        int total = 0;
        for (IStorage<FluidStack> store : network.getFluidStorageCache().getStorages()) {
            if (store instanceof IExternalStorage) {
                total += ((IExternalStorage<FluidStack>) store).getCapacity();
            }
        }
        return total;
    }

    public static Object getObjectFromPattern(ICraftingPattern pattern, INetwork network) {
        if (pattern == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        List<ItemStack> outputsList = pattern.getOutputs();
        List<Object> outputs = new ArrayList<>();
        for (ItemStack itemStack : outputsList)
            outputs.add(getObjectFromStack(itemStack.copy(), network));

        map.put("outputs", outputs);

        List<NonNullList<ItemStack>> inputList = pattern.getInputs();
        List<Object> inputs = new ArrayList<>();
        for (List<ItemStack> singleInputList : inputList) {
            List<Object> inputs1 = new ArrayList<>();
            for (ItemStack stack : singleInputList)
                inputs1.add(getObjectFromStack(stack.copy(), network));

            inputs.add(inputs1);
        }

        List<Object> byproducts = new ArrayList<>();
        if(!pattern.isProcessing()) {
            List<ItemStack> byproductsList = pattern.getByproducts();
            for (ItemStack stack : byproductsList)
                byproducts.add(getObjectFromStack(stack.copy(), network));
        }

        map.put("inputs", inputs);
        map.put("outputs", outputs);
        map.put("byproducts", byproducts);
        map.put("processing", pattern.isProcessing());
        return map;
    }

    public static Map<String, Object> getObjectFromStack(@Nullable ItemStack itemStack, INetwork network) {
        if (itemStack == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        CompoundNBT nbt = itemStack.getOrCreateTag();
        Set<ResourceLocation> tags = itemStack.getItem().getTags();
        map.put("fingerprint", getFingerpint(itemStack));
        map.put("name", itemStack.getItem().getRegistryName().toString());
        map.put("amount", itemStack.getCount());
        map.put("displayName", itemStack.getDisplayName().getString());
        map.put("isCraftable", isItemCraftable(network, itemStack));
        map.put("nbt", nbt.isEmpty() ? null : NBTUtil.toLua(nbt));
        map.put("tags", tags.isEmpty() ? null : LuaConverter.tagsToList(itemStack.getItem().getTags()));

        return map;
    }

    public static Map<String, Object> getObjectFromFluid(@Nullable FluidStack fluidStack, INetwork network) {
        if (fluidStack == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        Set<ResourceLocation> tags = fluidStack.getFluid().getTags();
        map.put("name", fluidStack.getFluid().getRegistryName().toString());
        map.put("amount", fluidStack.getAmount());
        map.put("displayName", fluidStack.getDisplayName().getString());
        map.put("isCraftable", isFluidCraftable(network, fluidStack));
        map.put("tags", tags.isEmpty() ? null : LuaConverter.tagsToList(fluidStack.getFluid().getTags()));

        return map;
    }

    public static Object getItem(INetwork network, ItemStack item) {
        for (ItemStack itemStack : getItems(network)) {
            if (itemStack.sameItem(item) && Objects.equals(itemStack.getOrCreateTag(), item.getOrCreateTag()))
                return getObjectFromStack(itemStack, network);

        }
        return null;
    }

    public static List<ItemStack> getCraftableItems(INetwork network) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        Collection<StackListEntry<ItemStack>> craftableEntries = cache.getCraftablesList().getStacks();
        List<ItemStack> result = new ArrayList<>(craftableEntries.size());

        for (StackListEntry<ItemStack> entry : craftableEntries) {
            result.add(entry.getStack());
        }

        return result;
    }

    public static List<ItemStack> getItems(INetwork network) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        Collection<StackListEntry<ItemStack>> entries = cache.getList().getStacks();
        Collection<StackListEntry<ItemStack>> craftableEntries = cache.getCraftablesList().getStacks();
        List<ItemStack> result = new ArrayList<>(entries.size());

        for (StackListEntry<ItemStack> entry : entries)
            result.add(entry.getStack().copy());

        //We add all craftable items to the list too. If we wanted to use getItem() on an empty item, it would return null
        for (StackListEntry<ItemStack> entry : craftableEntries) {
            result.add(entry.getStack().copy());
        }

        return result;
    }

    public static List<FluidStack> getFluids(INetwork network) {
        IStorageCache<FluidStack> cache = network.getFluidStorageCache();
        Collection<StackListEntry<FluidStack>> entries = cache.getList().getStacks();
        List<FluidStack> result = new ArrayList<>(entries.size());

        for (StackListEntry<FluidStack> entry : entries)
            result.add(entry.getStack().copy());

        return result;
    }

    public static CompoundNBT findMatchingTag(ItemStack stack, String nbtHash, List<ItemStack> items) {
        for (ItemStack rsStack : items) {
            if (rsStack.getCount() > 0 && rsStack.getItem().equals(stack.getItem())) {
                CompoundNBT tag = rsStack.getTag();
                String hash = NBTUtil.getNBTHash(tag);
                if (nbtHash.equals(hash))
                    return tag.copy();
            }
        }
        return null;
    }

    public static ItemStack findMatchingFingerprint(String fingerprint, List<ItemStack> items) {
        for (ItemStack rsStack : items) {
            if (rsStack.getCount() > 0) {
                if (fingerprint.equals(getFingerpint(rsStack)))
                    return rsStack;

            }
        }
        return ItemStack.EMPTY;
    }

    public static String getFingerpint(ItemStack stack) {
        String fingerprint = stack.getOrCreateTag() + stack.getItem().getRegistryName().toString() + stack.getDisplayName().getString();
        try {
            byte[] bytesOfHash = fingerprint.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return new String(Hex.encodeHex(md.digest(bytesOfHash)));
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not parse fingerprint.");
            ex.printStackTrace();
        }
        return "";
    }

    public void initiate() {
        api.getNetworkNodeRegistry().add(new ResourceLocation(AdvancedPeripherals.MOD_ID, "rs_bridge"), (tag, world, pos) -> read(tag, new RefinedStorageNode(world, pos)));
    }

    public IRSAPI getApi() {
        return api;
    }

}
