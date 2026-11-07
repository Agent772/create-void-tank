package com.agent772.createvoidtank.mixins;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.neoforged.fml.loading.LoadingModList;

/** Applies the soft-compat mixins only when the mod they target is installed. */
public class CreateVoidTankMixinPlugin implements IMixinConfigPlugin {

    private static final String DRILL_DRAIN_MIXINS = "com.agent772.createvoidtank.mixins.compat.drilldrain.";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(DRILL_DRAIN_MIXINS)) {
            return LoadingModList.get().getModFileById("drill_drain") != null;
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
