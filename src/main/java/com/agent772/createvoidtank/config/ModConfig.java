package com.agent772.createvoidtank.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<ActivationMode> ACTIVATION_MODE;
    public static final ModConfigSpec.EnumValue<MinimumHeatLevel> MINIMUM_HEAT_LEVEL;
    public static final ModConfigSpec.BooleanValue VOID_DRILL_DRAIN_FLUID;

    public enum ActivationMode {
        ALWAYS_ACTIVE,
        REQUIRES_HEAT,
        REQUIRES_REDSTONE
    }

    public enum MinimumHeatLevel {
        PASSIVE,
        BLAZE_BURNER,
        SUPERHEATED
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("activation");

        ACTIVATION_MODE = builder
                .comment("Condition that must be met for the Void Tank to void fluid.",
                        "ALWAYS_ACTIVE = no condition needed (default)",
                        "REQUIRES_HEAT = must be placed above a heat source (Blaze Burner, fire, lava, campfire)",
                        "REQUIRES_REDSTONE = must receive a redstone signal")
                .defineEnum("activationMode", ActivationMode.ALWAYS_ACTIVE);

        MINIMUM_HEAT_LEVEL = builder
                .comment("Minimum heat level required when activationMode is REQUIRES_HEAT.",
                        "PASSIVE = campfire, fire, lava (default)",
                        "BLAZE_BURNER = lit Blaze Burner",
                        "SUPERHEATED = superheated Blaze Burner",
                        "Higher heat levels satisfy lower requirements.")
                .defineEnum("minimumHeatLevel", MinimumHeatLevel.PASSIVE);

        builder.pop();

        builder.push("contraptions");

        VOID_DRILL_DRAIN_FLUID = builder
                .comment("Drill Drain compat (only when the Drill Drain mod is installed).",
                        "Whether a moving contraption with a Void Tank on board voids the fluid its Drill Drains collect.",
                        "That fluid is voided right away and never goes into fluid tanks on the contraption.",
                        "The Void Tank must have been active (heat/redstone, see activationMode) when the contraption was assembled.",
                        "Fluid from other sources (e.g. Portable Fluid Interfaces) is never voided on contraptions.")
                .define("voidDrillDrainFluid", true);

        builder.pop();

        SPEC = builder.build();
    }
}
