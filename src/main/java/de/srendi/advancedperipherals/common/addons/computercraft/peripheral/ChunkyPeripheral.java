package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;

public class ChunkyPeripheral extends BasePeripheral {

    public static final String TYPE = "chunky";

    public ChunkyPeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, turtle, side);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChunkyTurtle;
    }

}
