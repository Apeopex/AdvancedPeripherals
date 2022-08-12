package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import de.srendi.advancedperipherals.common.argoggles.RenderActionType;
import de.srendi.advancedperipherals.common.blocks.blockentities.ARControllerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

import java.util.Optional;

public class ARControllerPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<ARControllerEntity>> {
    public static final String TYPE = "arController";

    public ARControllerPeripheral(ARControllerEntity tileEntity) {
        super(TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableARGoggles.get();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult isRelativeMode() {
        int[] virtualScreenSize = owner.tileEntity.getVirtualScreenSize();
        if (virtualScreenSize != null) {
            return MethodResult.of(owner.tileEntity.isRelativeMode(), virtualScreenSize[0], virtualScreenSize[1]);
        } else {
            return MethodResult.of(owner.tileEntity.isRelativeMode());
        }
    }

    @LuaFunction(mainThread = true)
    public final void setRelativeMode(boolean enabled, Optional<Integer> virtualScreenWidth, Optional<Integer> virtualScreenHeight) throws LuaException {
        if (enabled) {
            if (!virtualScreenWidth.isPresent() || !virtualScreenHeight.isPresent())
                throw new LuaException("You need to pass virtual screen width and height to enable relative mode.");
            owner.tileEntity.setRelativeMode(virtualScreenWidth.get(), virtualScreenHeight.get());
        } else {
            owner.tileEntity.disableRelativeMode();
        }
    }

    @LuaFunction(mainThread = true)
    public final void drawString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawStringWithId(String id, String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DrawString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawCenteredString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawCenteredString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawCenteredStringWithId(String id, String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DrawCenteredString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawRightboundString(String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawRightboundString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawRightboundStringWithId(String id, String text, int x, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DrawRightboundString, text, x, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void horizontalLine(int minX, int maxX, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.HorizontalLine, minX, maxX, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void horizontalLineWithId(String id, int minX, int maxX, int y, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.HorizontalLine, minX, maxX, y, color));
    }

    @LuaFunction(mainThread = true)
    public final void verticalLine(int x, int minY, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.VerticalLine, x, minY, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void verticalLineWithId(String id, int x, int minY, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.VerticalLine, x, minY, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fill(int minX, int minY, int maxX, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.Fill, minX, minY, maxX, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillWithId(String id, int minX, int minY, int maxX, int maxY, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.Fill, minX, minY, maxX, maxY, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillGradient(int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FillGradient, minX, minY, maxX, maxY, colorFrom, colorTo));
    }

    @LuaFunction(mainThread = true)
    public final void fillGradientWithId(String id, int minX, int minY, int maxX, int maxY, int colorFrom, int colorTo) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.FillGradient, minX, minY, maxX, maxY, colorFrom, colorTo));
    }

    @LuaFunction(mainThread = true)
    public final void drawCircle(int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawCircle, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawCircleWithId(String id, int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DrawCircle, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillCircle(int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.FillCircle, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void fillCircleWithId(String id, int x, int y, int radius, int color) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.FillCircle, x, y, radius, color));
    }

    @LuaFunction(mainThread = true)
    public final void drawItemIcon(String itemId, int x, int y) {
        owner.tileEntity.addToCanvas(new ARRenderAction(RenderActionType.DrawItemIcon, itemId, x, y));
    }

    @LuaFunction(mainThread = true)
    public final void drawItemIconWithId(String id, String itemId, int x, int y) {
        owner.tileEntity.addToCanvas(new ARRenderAction(id, RenderActionType.DrawItemIcon, itemId, x, y));
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        owner.tileEntity.clearCanvas();
    }

    @LuaFunction(mainThread = true)
    public final void clearElement(String id) {
        owner.tileEntity.clearElement(id);
    }

    //TODO - 0.8r: These two functions do not work. This has several reasons. https://github.com/Seniorendi/AdvancedPeripherals/issues/307
    //Returning 0 instead of crashing
    @LuaFunction(mainThread = true)
    public final float getPlayerRotationY() {
        return 0;
    }

    @LuaFunction(mainThread = true)
    public final float getPlayerRotationZ() {
        return 0;
    }
}
