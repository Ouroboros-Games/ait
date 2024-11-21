package loqor.ait.core.engine.impl;

import java.util.List;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import loqor.ait.core.AITBlocks;
import loqor.ait.core.engine.DurableSubSystem;
import loqor.ait.core.engine.StructureHolder;
import loqor.ait.core.engine.block.multi.MultiBlockStructure;
import loqor.ait.core.tardis.util.TardisUtil;

public class LifeSupportCircuit extends DurableSubSystem implements StructureHolder {
    private static final MultiBlockStructure STRUCTURE = createStructure();
    private static MultiBlockStructure createStructure() {
        MultiBlockStructure made = new MultiBlockStructure();

        // TODO im too lazy to figure out a structure for now but ill force saturn to do it - Loqor
        made.addAll(MultiBlockStructure.BlockOffset.square(Blocks.QUARTZ_BLOCK, Direction.NORTH, 2, AITBlocks.ZEITON_COBBLE, AITBlocks.COMPACT_ZEITON));
        made.addAll(MultiBlockStructure.BlockOffset.square(Blocks.QUARTZ_BLOCK, Direction.EAST, 2, AITBlocks.ZEITON_COBBLE, AITBlocks.COMPACT_ZEITON));
        made.addAll(MultiBlockStructure.BlockOffset.square(Blocks.QUARTZ_BLOCK, Direction.UP, 2, AITBlocks.ZEITON_COBBLE, AITBlocks.COMPACT_ZEITON));

        // the blocks above and below the center can be cables
        made.at(new BlockPos(0, 2, 0)).orElseThrow().allow(AITBlocks.CABLE_BLOCK);
        made.at(new BlockPos(0,-2, 0)).orElseThrow().allow(AITBlocks.CABLE_BLOCK);
        made.remove(new BlockPos(0, 0, 0));

        return made;
    }

    public LifeSupportCircuit() {
        super(Id.LIFE_SUPPORT);
    }

    @Override
    protected float cost() {
        return 1f;
    }

    @Override
    protected boolean shouldDurabilityChange() {
        return !this.tardis.crash().isNormal();
    }

    @Override
    protected void onRepair() {
    }

    @Override
    public MultiBlockStructure getStructure() {
        return STRUCTURE;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tardis().asServer().getInteriorWorld().getServer().getTicks() % 20 != 0)
            return;

        List<LivingEntity> entities = TardisUtil.getLivingEntitiesInInterior(this.tardis().asServer());

        for (LivingEntity entity : entities) {
            entity.heal(1.0f);
        }
    }
}
