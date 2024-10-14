package nowhed.ringlesgunturret.util;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import nowhed.ringlesgunturret.block.ModBlocks;
import nowhed.ringlesgunturret.item.ModItems;

import java.util.function.Consumer;

public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT,
                ModItems.REINFORCED_ARROW, 2)
                .input(Items.LAPIS_LAZULI)
                .input(Items.ARROW)
                .input(Items.FIRE_CHARGE)
                .criterion(FabricRecipeProvider.hasItem(ModBlocks.GUN_TURRET_ITEM),
                        FabricRecipeProvider.conditionsFromItem(ModBlocks.GUN_TURRET_ITEM))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT,
                ModBlocks.GUN_TURRET_ITEM, 1)
                .pattern("abc")
                .pattern("acc")
                .pattern("ddd")
                .input('a', Items.DISPENSER)
                .input('b', Items.OBSERVER)
                .input('c', Items.COPPER_BLOCK)
                .input('d', Items.PISTON)
                .criterion(FabricRecipeProvider.hasItem(Items.DISPENSER),
                        FabricRecipeProvider.conditionsFromItem(Items.DISPENSER))
                .offerTo(exporter);
    }
}
