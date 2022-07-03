package naturalism.addon.leandick.mixins.meteor;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.annotation.Target;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(CrystalAura.class)
public class CrystalAuraMixin {
    @Shadow(remap = false)
    @Final private SettingGroup sgRender;

    @Shadow(remap = false)
    @Final
    private Setting<Boolean> render;

    @Shadow(remap = false)
    @Final
    private Setting<Boolean> renderBreak;

    @Shadow(remap = false)
    @Final
    private Setting<Color> sideColor;

    @Shadow(remap = false)
    @Final
    private Setting<Color> lineColor;

    @Shadow(remap = false)
    @Final
    private Setting<ShapeMode> shapeMode;

    @Shadow(remap = false)
    private int renderTimer, breakRenderTimer;

    @Shadow(remap = false)
    @Final private BlockPos.Mutable renderPos, breakRenderPos;

    @Unique private Setting<Boolean> trollHackRender = null;
    @Unique private Setting<Integer> factor = null;
    @Unique private Box renderBox, breakRenderBox;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onInit(CallbackInfo ci) {
        trollHackRender = sgRender.add(new BoolSetting.Builder().name("troll-hack-render").defaultValue(true).build());
        factor = sgRender.add(new IntSetting.Builder().name("smooth-factor").min(1).max(20).defaultValue(5).build());
    }

    @Inject(method = "onRender", at = @At("HEAD"), remap = false, cancellable = true)
    private void onRender(Render3DEvent event, CallbackInfo ci){
        if (renderTimer > 0 && render.get()) {
            if (trollHackRender.get()){
                Box post = new Box(renderPos);
                if (renderBox == null) renderBox = post;

                double x = (post.minX - renderBox.minX) / factor.get();
                double y = (post.minY - renderBox.minY) / factor.get();
                double z = (post.minZ - renderBox.minZ) / factor.get();

                renderBox = new Box(renderBox.minX + x, renderBox.minY + y, renderBox.minZ + z, renderBox.maxX + x, renderBox.maxY + y,  renderBox.maxZ + z);
            }
            else renderBox = new Box(renderPos);
            event.renderer.box(renderBox, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }

        if (breakRenderTimer > 0 && renderBreak.get() && !mc.world.getBlockState(breakRenderPos).isAir()) {
            int preSideA = sideColor.get().a;
            sideColor.get().a -= 20;
            sideColor.get().validate();

            int preLineA = lineColor.get().a;
            lineColor.get().a -= 20;
            lineColor.get().validate();

            if (trollHackRender.get()){
                Box post = new Box(breakRenderPos);
                if (breakRenderBox == null) breakRenderBox = post;

                double x = (post.minX - breakRenderBox.minX) / factor.get();
                double y = (post.minY - breakRenderBox.minY) / factor.get();
                double z = (post.minZ - breakRenderBox.minZ) / factor.get();

                breakRenderBox = new Box(breakRenderBox.minX + x, breakRenderBox.minY + y, breakRenderBox.minZ + z, breakRenderBox.maxX + x, breakRenderBox.maxY + y,  breakRenderBox.maxZ + z);
            }else breakRenderBox = new Box(breakRenderPos);

            event.renderer.box(breakRenderBox, sideColor.get(), lineColor.get(), shapeMode.get(), 0);

            sideColor.get().a = preSideA;
            lineColor.get().a = preLineA;
        }
        ci.cancel();
    }
}
